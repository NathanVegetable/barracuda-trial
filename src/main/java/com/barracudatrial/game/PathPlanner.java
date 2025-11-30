package com.barracudatrial.game;

import com.barracudatrial.CachedConfig;
import com.barracudatrial.game.route.*;
import com.barracudatrial.pathfinding.AStarPathfinder;
import com.barracudatrial.pathfinding.BarracudaTileCostCalculator;
import com.barracudatrial.pathfinding.PathResult;
import com.barracudatrial.pathfinding.PathStabilizer;
import com.barracudatrial.rendering.RenderingUtils;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.function.Predicate;

@Slf4j
public class PathPlanner
{
	private final State state;
	private final CachedConfig cachedConfig;
	private final Client client;
	private final PathStabilizer pathStabilizer;

	public PathPlanner(Client client, State state, CachedConfig cachedConfig)
	{
		this.client = client;
		this.state = state;
		this.cachedConfig = cachedConfig;

		AStarPathfinder aStarPathfinder = new AStarPathfinder();
		this.pathStabilizer = new PathStabilizer(aStarPathfinder);
	}

	/**
	 * Recalculates the optimal path based on current game state
	 * Uses static routes for strategic planning and A* for tactical navigation
	 * @param recalculationTriggerReason Description of what triggered this recalculation (for debugging)
	 */
	public void recalculateOptimalPathFromCurrentState(String recalculationTriggerReason)
	{
		state.setLastPathRecalcCaller(recalculationTriggerReason);
		log.debug("Path recalculation triggered by: {}", recalculationTriggerReason);

		if (!state.isInTrialArea())
		{
			state.getPath().clear();
			return;
		}

		state.setTicksSinceLastPathRecalc(0);

		// Use front boat tile for pathfinding (fallback to center if not available)
		WorldPoint playerBoatLocation = state.getFrontBoatTileEstimatedActual();
		if (playerBoatLocation == null)
		{
			playerBoatLocation = state.getBoatLocation();
		}
		if (playerBoatLocation == null)
		{
			return;
		}

		if (state.getCurrentStaticRoute() == null)
		{
			loadStaticRouteForCurrentDifficulty();
		}

		List<RouteWaypoint> nextWaypoints = findNextUncompletedWaypoints(cachedConfig.getPathLookahead());

		if (nextWaypoints.isEmpty())
		{
			state.setPath(new ArrayList<>());
			log.debug("No uncompleted waypoints found in static route");
			return;
		}

		List<WorldPoint> fullPath = pathThroughMultipleWaypoints(playerBoatLocation, nextWaypoints);

		state.setPath(fullPath);

		log.debug("Pathing through {} waypoints starting at index {}", nextWaypoints.size(), state.getNextNavigatableWaypointIndex());
	}

	private void loadStaticRouteForCurrentDifficulty()
	{
		var trial = state.getCurrentTrial();
		if (trial == null)
		{
			log.warn("Trial config not initialized, cannot load route");
			state.setCurrentStaticRoute(new ArrayList<>());
			return;
		}

		Difficulty difficulty = State.getCurrentDifficulty(client);
		List<RouteWaypoint> staticRoute = trial.getRoute(difficulty);

		if (staticRoute == null || staticRoute.isEmpty())
		{
			log.warn("No static route found for trial {} difficulty: {}", trial.getTrialType(), difficulty);
			state.setCurrentStaticRoute(new ArrayList<>());
			return;
		}

		state.setCurrentStaticRoute(staticRoute);
		log.info("Loaded static route for {} difficulty {} with {} waypoints",
			trial.getTrialType(), difficulty, staticRoute.size());
	}

	/**
	 * Finds the next N uncompleted waypoints in the static route sequence.
	 * Routes to waypoints even if not yet visible (game only reveals nearby shipments).
	 * Supports backtracking if a waypoint was missed.
	 *
	 * @param count Maximum number of uncompleted navigatable waypoints
	 * @return List of uncompleted waypoints in route order (includes all helper waypoints between real waypoints)
	 */
	private List<RouteWaypoint> findNextUncompletedWaypoints(int count)
	{
		List<RouteWaypoint> uncompletedWaypoints = new ArrayList<>();

		var route = state.getCurrentStaticRoute();
		if (route == null || route.isEmpty())
		{
			return uncompletedWaypoints;
		}

		int routeSize = route.size();
		int nextNavIndex = state.getNextNavigatableWaypointIndex();

		// Scan backwards from nextNavigatableWaypointIndex to find uncompleted helpers that precede it
		List<RouteWaypoint> precedingHelpers = new ArrayList<>();
		for (int i = 1; i < routeSize; i++)
		{
			int checkIndex = (nextNavIndex - i + routeSize) % routeSize;
			RouteWaypoint waypoint = route.get(checkIndex);

			if (state.isWaypointCompleted(checkIndex))
			{
				break;
			}

			if (waypoint.getType().isNonNavigatableHelper())
			{
				precedingHelpers.add(0, waypoint);
			}
			else
			{
				break;
			}
		}

		uncompletedWaypoints.addAll(precedingHelpers);

		int navigatableWaypointCount = 0;

		// Scan forward from nextNavigatableWaypointIndex
		for (int offset = 0; offset < routeSize && navigatableWaypointCount < count; offset++)
		{
			int checkIndex = (nextNavIndex + offset) % routeSize;
			RouteWaypoint waypoint = route.get(checkIndex);

			if (!state.isWaypointCompleted(checkIndex))
			{
				uncompletedWaypoints.add(waypoint);

				if (!waypoint.getType().isNonNavigatableHelper())
				{
					navigatableWaypointCount++;
				}
			}
		}

		return uncompletedWaypoints;
	}

	private static class BoatHeading
	{
		private final int dx;
		private final int dy;

		BoatHeading(int dx, int dy)
		{
			this.dx = dx;
			this.dy = dy;
		}

		int dx()
		{
			return dx;
		}

		int dy()
		{
			return dy;
		}
	}

	private List<WorldPoint> extendPath(List<WorldPoint> fullPath, List<WorldPoint> segment)
	{
		List<WorldPoint> result = new ArrayList<>(fullPath);

		if (result.isEmpty())
		{
			result.addAll(segment);
		}
		else if (!segment.isEmpty())
		{
			result.addAll(segment.subList(1, segment.size()));
		}

		return result;
	}

	private BoatHeading calculateBoatHeading(List<WorldPoint> fullPath)
	{
		if (fullPath.isEmpty())
		{
			WorldPoint frontBoatTile = state.getFrontBoatTileEstimatedActual();
			WorldPoint backBoatTile = state.getBoatLocation();

			if (frontBoatTile != null && backBoatTile != null)
			{
				return new BoatHeading(
					frontBoatTile.getX() - backBoatTile.getX(),
					frontBoatTile.getY() - backBoatTile.getY()
				);
			}

			return new BoatHeading(0, 0);
		}

		if (fullPath.size() >= 2)
		{
			WorldPoint prev = fullPath.get(fullPath.size() - 2);
			WorldPoint last = fullPath.get(fullPath.size() - 1);
			return new BoatHeading(
				last.getX() - prev.getX(),
				last.getY() - prev.getY()
			);
		}

		return new BoatHeading(0, 0);
	}

	private WorldPoint handlePortalExitTeleport(WorldPoint currentPosition, WorldPoint portalExitLocation)
	{
		int distance = currentPosition.distanceTo(portalExitLocation);

		if (distance > 10)
		{
			return portalExitLocation;
		}

		return currentPosition;
	}

	private static class WaypointHandlingResult
	{
		final List<WorldPoint> pathSegment;
		final WorldPoint newPosition;
		final boolean shouldStopPathing;
		final int skipToIndex;

		WaypointHandlingResult(List<WorldPoint> pathSegment, WorldPoint newPosition, boolean shouldStopPathing, int skipToIndex)
		{
			this.pathSegment = pathSegment;
			this.newPosition = newPosition;
			this.shouldStopPathing = shouldStopPathing;
			this.skipToIndex = skipToIndex;
		}
	}

	private WaypointHandlingResult handleSingleWaypoint(
		WorldPoint currentPosition,
		RouteWaypoint waypoint,
		boolean isPlayerCurrentlyOnPath,
		int initialBoatDx,
		int initialBoatDy,
		Set<WorldPoint> pathfindingHints,
		boolean stopAfterPathing)
	{
		WorldPoint pathfindingTarget = getInSceneTarget(currentPosition, waypoint);
		PathResult segmentResult = pathToSingleTarget(
			currentPosition,
			pathfindingTarget,
			waypoint.getType().getToleranceTiles(),
			isPlayerCurrentlyOnPath,
			initialBoatDx,
			initialBoatDy,
			pathfindingHints
		);

		List<WorldPoint> segmentPath = segmentResult.getPath();
		WorldPoint newPosition = segmentPath.isEmpty() ? currentPosition : segmentPath.get(segmentPath.size() - 1);
		boolean shouldStop = stopAfterPathing || !segmentResult.isReachedGoal();

		return new WaypointHandlingResult(segmentPath, newPosition, shouldStop, -1);
	}

	/**
	 * Wind catchers provide speed boosts but force specific routes. Sometimes going direct is faster/safer.
	 * This method tries BOTH options and picks the winner based on: goal reached > path cost.
	 */
	private WaypointHandlingResult handleWindCatcherSequence(
		WorldPoint currentPosition,
		List<RouteWaypoint> waypoints,
		int currentIndex,
		boolean isPlayerCurrentlyOnPath,
		int initialBoatDx,
		int initialBoatDy,
		Set<WorldPoint> pathfindingHints)
	{
		List<RouteWaypoint> windCatcherSequence = collectConsecutiveWindCatchers(waypoints, currentIndex);

		int indexAfterWindCatchers = currentIndex + windCatcherSequence.size();
		RouteWaypoint destinationAfterWindCatchers = findDestinationAfterWindCatchers(waypoints, indexAfterWindCatchers);
		Set<WorldPoint> hintsAfterWindCatchers = collectPathfindingHints(waypoints, indexAfterWindCatchers);

		int nextWaypointIndex = indexAfterWindCatchers + hintsAfterWindCatchers.size();
		if (destinationAfterWindCatchers != null)
		{
			nextWaypointIndex++;
		}

		WindCatcherPathResult pathUsingWindCatchers = pathThroughWindCatcherSequence(
			currentPosition,
			windCatcherSequence,
			destinationAfterWindCatchers,
			isPlayerCurrentlyOnPath,
			initialBoatDx,
			initialBoatDy,
			pathfindingHints,
			hintsAfterWindCatchers
		);

		PathResult pathSkippingWindCatchers = null;
		if (destinationAfterWindCatchers != null)
		{
			WorldPoint directTarget = getInSceneTarget(currentPosition, destinationAfterWindCatchers);
			pathSkippingWindCatchers = pathToSingleTarget(
				currentPosition,
				directTarget,
				destinationAfterWindCatchers.getType().getToleranceTiles(),
				isPlayerCurrentlyOnPath,
				initialBoatDx,
				initialBoatDy,
				hintsAfterWindCatchers
			);
		}

		List<WorldPoint> winningPath = chooseBetterPath(pathUsingWindCatchers, pathSkippingWindCatchers);
		WorldPoint finalPosition = winningPath.isEmpty() ? currentPosition : winningPath.get(winningPath.size() - 1);

		return new WaypointHandlingResult(winningPath, finalPosition, false, nextWaypointIndex);
	}

	private List<RouteWaypoint> collectConsecutiveWindCatchers(List<RouteWaypoint> waypoints, int startIndex)
	{
		List<RouteWaypoint> windCatchers = new ArrayList<>();
		windCatchers.add(waypoints.get(startIndex));

		for (int i = startIndex + 1; i < waypoints.size(); i++)
		{
			if (waypoints.get(i).getType() == RouteWaypoint.WaypointType.USE_WIND_CATCHER)
			{
				windCatchers.add(waypoints.get(i));
			}
			else
			{
				break;
			}
		}

		return windCatchers;
	}

	private RouteWaypoint findDestinationAfterWindCatchers(List<RouteWaypoint> waypoints, int startIndex)
	{
		for (int i = startIndex; i < waypoints.size(); i++)
		{
			var type = waypoints.get(i).getType();
			if (type != RouteWaypoint.WaypointType.PATHFINDING_HINT)
			{
				return waypoints.get(i);
			}
		}
		return null;
	}

	private Set<WorldPoint> collectPathfindingHints(List<RouteWaypoint> waypoints, int startIndex)
	{
		Set<WorldPoint> hints = new HashSet<>();

		for (int i = startIndex; i < waypoints.size(); i++)
		{
			if (waypoints.get(i).getType() == RouteWaypoint.WaypointType.PATHFINDING_HINT)
			{
				hints.add(waypoints.get(i).getLocation());
			}
			else
			{
				break;
			}
		}

		return hints;
	}

	private List<WorldPoint> chooseBetterPath(WindCatcherPathResult windCatcherPath, PathResult directPath)
	{
		boolean windCatcherReachedGoal = windCatcherPath.reachedGoal;
		boolean directReachedGoal = directPath != null && directPath.isReachedGoal();

		if (windCatcherReachedGoal && !directReachedGoal)
		{
			return windCatcherPath.path;
		}

		if (directReachedGoal && !windCatcherReachedGoal)
		{
			return directPath.getPath();
		}

		if (windCatcherReachedGoal && directReachedGoal)
		{
			return windCatcherPath.cost <= directPath.getCost() ? windCatcherPath.path : directPath.getPath();
		}

		return (directPath != null && directPath.getCost() < windCatcherPath.cost)
			? directPath.getPath()
			: windCatcherPath.path;
	}

	/**
	 * Paths through multiple waypoints in sequence using A*
	 * @param start Starting position
	 * @param waypoints List of waypoints to path through in order
	 * @return Complete path through all waypoints
	 */
	private List<WorldPoint> pathThroughMultipleWaypoints(WorldPoint start, List<RouteWaypoint> waypoints)
	{
		if (waypoints.isEmpty())
		{
			return new ArrayList<>();
		}

		List<WorldPoint> fullPath = new ArrayList<>();
		WorldPoint currentPosition = start;
		boolean isPlayerCurrentlyOnPath = true;
		Set<WorldPoint> pathfindingHints = new HashSet<>();

		for (int i = 0; i < waypoints.size(); i++)
		{
			RouteWaypoint waypoint = waypoints.get(i);
			var waypointType = waypoint.getType();

			if (waypointType == RouteWaypoint.WaypointType.PATHFINDING_HINT)
			{
				pathfindingHints.add(waypoint.getLocation());
				continue;
			}

			if (waypointType == RouteWaypoint.WaypointType.PORTAL_EXIT)
			{
				currentPosition = handlePortalExitTeleport(currentPosition, waypoint.getLocation());
				continue;
			}

			BoatHeading heading = calculateBoatHeading(fullPath);
			int initialBoatDx = heading.dx();
			int initialBoatDy = heading.dy();

			if (waypointType == RouteWaypoint.WaypointType.PORTAL_ENTER)
			{
				WaypointHandlingResult result = handleSingleWaypoint(
					currentPosition,
					waypoint,
					isPlayerCurrentlyOnPath,
					initialBoatDx,
					initialBoatDy,
					pathfindingHints,
					true
				);

				pathfindingHints.clear();
				fullPath = extendPath(fullPath, result.pathSegment);
				break;
			}

			if (waypointType == RouteWaypoint.WaypointType.USE_WIND_CATCHER)
			{
				WaypointHandlingResult result = handleWindCatcherSequence(
					currentPosition,
					waypoints,
					i,
					isPlayerCurrentlyOnPath,
					initialBoatDx,
					initialBoatDy,
					pathfindingHints
				);

				pathfindingHints.clear();

				fullPath = extendPath(fullPath, result.pathSegment);
				currentPosition = result.newPosition;
				isPlayerCurrentlyOnPath = false;

				i = result.skipToIndex;
				continue;
			}

			WaypointHandlingResult result = handleSingleWaypoint(
				currentPosition,
				waypoint,
				isPlayerCurrentlyOnPath,
				initialBoatDx,
				initialBoatDy,
				pathfindingHints,
				false
			);

			pathfindingHints.clear();
			fullPath = extendPath(fullPath, result.pathSegment);

			if (result.shouldStopPathing)
			{
				break;
			}

			currentPosition = result.newPosition;
			isPlayerCurrentlyOnPath = false;
		}

		return fullPath;
	}

	private static class WindCatcherPathResult
	{
		final List<WorldPoint> path;
		final double cost;
		final boolean reachedGoal;

		WindCatcherPathResult(List<WorldPoint> path, double cost, boolean reachedGoal)
		{
			this.path = path;
			this.cost = cost;
			this.reachedGoal = reachedGoal;
		}
	}

	/**
	 * Handles pathing through a sequence of wind catcher waypoints as one segment:
	 * 1. Pathfind TO the first wind catcher
	 * 2. Add straight lines between all consecutive wind catchers
	 * 3. Pathfind FROM the last wind catcher TO the next normal waypoint (if any)
	 */
	private WindCatcherPathResult pathThroughWindCatcherSequence(
		WorldPoint start,
		List<RouteWaypoint> windCatcherSequence,
		RouteWaypoint nextNormalWaypoint,
		boolean isPlayerCurrentlyOnPath,
		int initialBoatDx,
		int initialBoatDy,
		Set<WorldPoint> pathfindingHints,
		Set<WorldPoint> postWindCatcherHints)
	{
		List<WorldPoint> segmentPath = new ArrayList<>();
		double totalCost = 0;
		boolean reachedGoal = false;

		if (windCatcherSequence.isEmpty())
		{
			return new WindCatcherPathResult(segmentPath, Double.POSITIVE_INFINITY, false);
		}

		// Step 1: Pathfind TO the first wind catcher
		RouteWaypoint firstWindCatcher = windCatcherSequence.get(0);
		WorldPoint firstWindCatcherTarget = getInSceneTarget(start, firstWindCatcher);

		PathResult pathToFirst = pathToSingleTarget(
			start,
			firstWindCatcherTarget,
			1,
			isPlayerCurrentlyOnPath,
			initialBoatDx,
			initialBoatDy,
			pathfindingHints
		);

		segmentPath.addAll(pathToFirst.getPath());
		totalCost += pathToFirst.getCost();

		if (!pathToFirst.isReachedGoal())
		{
			return new WindCatcherPathResult(segmentPath, totalCost, false);
		}

		// Ensure we end exactly at the first wind catcher location
		WorldPoint firstWindCatcherLocation = firstWindCatcher.getLocation();
		WorldPoint lastPoint = segmentPath.isEmpty() ? null : segmentPath.get(segmentPath.size() - 1);
		if (!firstWindCatcherLocation.equals(lastPoint))
		{
			segmentPath.add(firstWindCatcherLocation);
		}

		// Step 2: Add straight lines between all wind catchers
		for (int i = 1; i < windCatcherSequence.size(); i++)
		{
			segmentPath.add(windCatcherSequence.get(i).getLocation());
		}

		// Step 3: Pathfind FROM last wind catcher TO next normal waypoint (if exists)
		if (nextNormalWaypoint != null)
		{
			WorldPoint lastWindCatcherLocation = windCatcherSequence.get(windCatcherSequence.size() - 1).getLocation();
			WorldPoint nextTarget = getInSceneTarget(lastWindCatcherLocation, nextNormalWaypoint);

			// Derive heading from the last wind catcher transition
			int nextBoatDx = 0;
			int nextBoatDy = 0;
			if (segmentPath.size() >= 2)
			{
				WorldPoint prev = segmentPath.get(segmentPath.size() - 2);
				WorldPoint last = segmentPath.get(segmentPath.size() - 1);
				nextBoatDx = last.getX() - prev.getX();
				nextBoatDy = last.getY() - prev.getY();
			}

			PathResult pathFromLast = pathToSingleTarget(
				lastWindCatcherLocation,
				nextTarget,
				nextNormalWaypoint.getType().getToleranceTiles(),
				false,
				nextBoatDx,
				nextBoatDy,
				postWindCatcherHints
			);

			totalCost += pathFromLast.getCost();
			reachedGoal = pathFromLast.isReachedGoal();

			if (!pathFromLast.getPath().isEmpty())
			{
				segmentPath.addAll(pathFromLast.getPath().subList(1, pathFromLast.getPath().size()));
			}
		}
		else
		{
			// No next waypoint, we've reached the end of the wind catcher sequence
			reachedGoal = true;
		}

		return new WindCatcherPathResult(segmentPath, totalCost, reachedGoal);
	}

	/**
	 * Paths from current position to a single target using A*
	 * @param start Starting position
	 * @param target Target position
	 * @param goalTolerance Number of tiles away from target that counts as reaching it (0 = exact)
	 * @param isPlayerCurrentlyOnPath Whether or not this is the path that the player is currently navigating
	 * @param pathfindingHints Set of tiles that should have reduced cost during pathfinding
	 * @return PathResult containing path from start to target and whether goal was reached
	 */
	private PathResult pathToSingleTarget(WorldPoint start, WorldPoint target, int goalTolerance, boolean isPlayerCurrentlyOnPath, int initialBoatDx, int initialBoatDy, Set<WorldPoint> pathfindingHints)
	{
		var tileCostCalculator = getBarracudaTileCostCalculator(pathfindingHints);

		// Use provided initial heading (may be 0,0 for neutral)
		int boatDirectionDx = initialBoatDx;
		int boatDirectionDy = initialBoatDy;

		int tileDistance = start.distanceTo(target); // Chebyshev distance in tiles

		// Never too high, but allow seeking longer on long paths
		int maximumAStarSearchDistance = Math.max(35, Math.min(80, tileDistance * 8));

		var currentStaticRoute = state.getCurrentStaticRoute();

		PathResult pathResult = pathStabilizer.findPath(tileCostCalculator, cachedConfig.getRouteOptimization(), currentStaticRoute, start, target, maximumAStarSearchDistance, boatDirectionDx, boatDirectionDy, goalTolerance, isPlayerCurrentlyOnPath);

		if (pathResult.getPath().isEmpty())
		{
			List<WorldPoint> fallbackPath = new ArrayList<>();
			fallbackPath.add(target);
			return new PathResult(new ArrayList<>(), Double.POSITIVE_INFINITY, false);
		}

		return pathResult;
	}

	private BarracudaTileCostCalculator getBarracudaTileCostCalculator(Set<WorldPoint> pathfindingHints)
	{
		Set<NPC> currentlyDangerousClouds = state.getDangerousClouds();

		var trial = state.getCurrentTrial();
		var boatExclusionWidth = trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM
			? TemporTantrumConfig.BOAT_EXCLUSION_WIDTH
			: JubblyJiveConfig.BOAT_EXCLUSION_WIDTH;
		var boatExclusionHeight = trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM
			? TemporTantrumConfig.BOAT_EXCLUSION_HEIGHT
			: JubblyJiveConfig.BOAT_EXCLUSION_HEIGHT;

		WorldPoint secondaryObjectiveLocation = null;

		if (trial != null)
		{
			var trialType = trial.getTrialType();

			if (trialType == TrialType.TEMPOR_TANTRUM)
			{
				secondaryObjectiveLocation = state.getRumReturnLocation();
			}
			else if (trialType == TrialType.JUBBLY_JIVE)
			{
				var route = state.getCurrentStaticRoute();
				if (route != null && !route.isEmpty())
				{
					var completed = state.getCompletedWaypointIndices();
					for (int i = 0; i < route.size(); i++)
					{
						if (completed.contains(i))
							continue;

						var waypoint = route.get(i);
						if (waypoint.getType() == RouteWaypoint.WaypointType.TOAD_PILLAR)
						{
							secondaryObjectiveLocation = waypoint.getLocation();
							break;
						}
					}
				}
			}
		}

		return new BarracudaTileCostCalculator(
			state.getKnownSpeedBoostLocations(),
			state.getKnownRockLocations(),
			state.getKnownFetidPoolLocations(),
			state.getKnownToadPillarLocations(),
			currentlyDangerousClouds,
			state.getExclusionZoneMinX(),
			state.getExclusionZoneMaxX(),
			state.getExclusionZoneMinY(),
			state.getExclusionZoneMaxY(),
			state.getRumPickupLocation(),
			secondaryObjectiveLocation,
			cachedConfig.getRouteOptimization(),
			boatExclusionWidth,
			boatExclusionHeight,
			pathfindingHints
		);
	}

	private WorldPoint getInSceneTarget(WorldPoint start, RouteWaypoint target)
	{
		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return target.getLocation();
		}

		int worldPlane = worldView.getPlane();
		WorldPoint targetLocation = target.getLocation();

		List<WorldPoint> candidates = new ArrayList<>();
		candidates.add(targetLocation);

		var fallbackLocations = target.getFallbackLocations();
		if (fallbackLocations != null)
		{
			for (WorldPoint fallback : fallbackLocations)
			{
				if (!fallback.equals(targetLocation))
				{
					candidates.add(fallback);
				}
			}
		}

		// 1. Prefer same-plane tiles in the normal scene
		for (WorldPoint p : candidates)
		{
			if (p.getPlane() != worldPlane)
			{
				continue;
			}

			if (LocalPoint.fromWorld(worldView, p) != null)
			{
				return p;
			}
		}

		// 2. Any tile that exists in the extended scene
		for (WorldPoint p : candidates)
		{
			if (RenderingUtils.localPointFromWorldIncludingExtended(worldView, p) != null)
			{
				return p;
			}
		}

		// 3. Fall back to nearest valid along the line toward the target
		return findNearestValidPoint(
			start,
			targetLocation,
			p -> RenderingUtils.localPointFromWorldIncludingExtended(worldView, p) != null
		);
	}

	/**
	 * Finds the furthest point from start toward target that satisfies the given validation function.
	 * Uses binary search for O(log n) efficiency.
	 * @param start Starting position
	 * @param target Desired target position
	 * @param isValid Function that returns true if a candidate point is valid
	 * @return The furthest valid point toward target, or start if none found
	 */
	private static WorldPoint findNearestValidPoint(WorldPoint start, WorldPoint target, Predicate<WorldPoint> isValid)
	{
		int dx = target.getX() - start.getX();
		int dy = target.getY() - start.getY();
		int maxDistance = Math.max(Math.abs(dx), Math.abs(dy));

		if (maxDistance < 1)
		{
			return start;
		}

		int plane = start.getPlane();
		int low = 0;
		int high = maxDistance;
		WorldPoint bestCandidate = start;

		while (low <= high)
		{
			int mid = (low + high) / 2;
			int x = start.getX() + (dx * mid / maxDistance);
			int y = start.getY() + (dy * mid / maxDistance);
			WorldPoint candidate = new WorldPoint(x, y, plane);

			if (isValid.test(candidate))
			{
				bestCandidate = candidate;
				low = mid + 1;
			}
			else
			{
				high = mid - 1;
			}
		}

		return bestCandidate;
	}

	public void reset()
	{
		pathStabilizer.clearActivePath();
	}
}
