package com.barracudatrial.rendering;

import com.barracudatrial.CachedConfig;
import com.barracudatrial.BarracudaTrialPlugin;
import com.barracudatrial.game.route.RouteWaypoint;
import com.barracudatrial.pathfinding.BarracudaTileCostCalculator;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PathRenderer
{
	private final Client client;
	private final BarracudaTrialPlugin plugin;
	private final ObjectRenderer objectRenderer;

	public void renderOptimalPath(Graphics2D graphics)
	{
		CachedConfig cachedConfig = plugin.getCachedConfig();
		List<WorldPoint> currentSegmentPath = plugin.getGameState().getCurrentSegmentPath();
		if (currentSegmentPath.isEmpty())
		{
			return;
		}

		// Get the visual front position transformed to main world coordinates
		// This preserves sub-tile accuracy while being in the correct coordinate system for interpolation
		LocalPoint visualFrontPositionTransformed = getTransformedFrontPosition();
		if (visualFrontPositionTransformed == null)
		{
			return;
		}

		// Trim the path to start from the closest point to our visual position
		// This prevents visual lag when the pathfinding position is behind the rendering position
		List<WorldPoint> trimmedPath = getTrimmedPathForRendering(visualFrontPositionTransformed, currentSegmentPath);

		drawSmoothPathWithBezier(graphics, trimmedPath, visualFrontPositionTransformed);
		renderWindCatcherHighlights(graphics);

		// Render debug visualizations
		if (cachedConfig.isDebugMode())
		{
			renderPathTiles(graphics, currentSegmentPath);
			renderWaypointLabels(graphics);
			renderPathfindingHints(graphics);
		}
	}

	private LocalPoint getTransformedFrontPosition()
	{
		LocalPoint frontBoatTileLocal = plugin.getGameState().getFrontBoatTileLocal();
		if (frontBoatTileLocal == null)
		{
			return null;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return null;
		}

		WorldView playerWorldView = localPlayer.getWorldView();
		if (playerWorldView == null)
		{
			return null;
		}

		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return null;
		}

		int playerWorldViewId = playerWorldView.getId();
		WorldEntity boatWorldEntity = topLevelWorldView.worldEntities().byIndex(playerWorldViewId);
		if (boatWorldEntity == null)
		{
			return null;
		}

		return boatWorldEntity.transformToMainWorld(frontBoatTileLocal);
	}

	private List<WorldPoint> getTrimmedPathForRendering(LocalPoint visualPosition, List<WorldPoint> path)
	{
		if (path.isEmpty() || visualPosition == null)
		{
			return path;
		}

		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return path;
		}

		int closestIndex = findClosestPointOnPath(visualPosition, path, topLevelWorldView);

		// Step forward along the path to bias toward showing "forward progress"
		// This prevents the path from appearing to start "alongside" when moving fast
		int forwardBiasOffset = 2;
		int startIndex = Math.min(path.size() - 1, closestIndex + forwardBiasOffset);

		if (startIndex >= path.size())
		{
			return new ArrayList<>();
		}

		return new ArrayList<>(path.subList(startIndex, path.size()));
	}

	private int findClosestPointOnPath(LocalPoint visualPosition, List<WorldPoint> path, WorldView worldView)
	{
		int closestIndex = 0;
		double minDistance = Double.POSITIVE_INFINITY;

		for (int i = 0; i < path.size(); i++)
		{
			LocalPoint pathPointLocal = RenderingUtils.localPointFromWorldIncludingExtended(worldView, path.get(i));
			if (pathPointLocal == null)
			{
				continue;
			}

			int dx = visualPosition.getX() - pathPointLocal.getX();
			int dy = visualPosition.getY() - pathPointLocal.getY();
			double distance = Math.sqrt(dx * dx + dy * dy);

			if (distance < minDistance)
			{
				minDistance = distance;
				closestIndex = i;
			}
		}

		return closestIndex;
	}

	private void drawSmoothPathWithBezier(Graphics2D graphics, List<WorldPoint> waypoints, LocalPoint visualStartPosition)
	{
		if (waypoints.isEmpty() || visualStartPosition == null)
		{
			return;
		}

		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return;
		}

		CachedConfig cachedConfig = plugin.getCachedConfig();

		// Convert visual start position to canvas coordinates
		Point startCanvas = Perspective.localToCanvas(client, visualStartPosition, client.getPlane(), 0);
		if (startCanvas == null)
		{
			return;
		}

		List<WindCatcherGroup> windCatcherGroups = getWindCatcherGroups();

		List<Point> canvasPoints = new ArrayList<>();
		List<Boolean> isWindCatcherSegment = new ArrayList<>();

		for (int wpIdx = 0; wpIdx < waypoints.size(); wpIdx++)
		{
			WorldPoint wp = waypoints.get(wpIdx);
			LocalPoint lp = RenderingUtils.localPointFromWorldIncludingExtended(topLevelWorldView, wp);
			if (lp != null)
			{
				Point cp = Perspective.localToCanvas(client, lp, wp.getPlane(), 0);
				if (cp != null)
				{
					canvasPoints.add(cp);

					boolean isWindCatcher = false;
					for (WindCatcherGroup group : windCatcherGroups)
					{
						int firstIdx = waypoints.indexOf(group.firstLocation);
						int lastIdx = waypoints.lastIndexOf(group.lastLocation);

						if (firstIdx != -1 && lastIdx != -1 && wpIdx >= firstIdx && wpIdx <= lastIdx)
						{
							isWindCatcher = true;
							break;
						}
					}

					isWindCatcherSegment.add(isWindCatcher);
				}
			}
		}

		if (canvasPoints.isEmpty())
		{
			return;
		}

		graphics.setStroke(new BasicStroke(cachedConfig.getPathWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		drawPathSegments(graphics, canvasPoints, waypoints, isWindCatcherSegment, startCanvas, cachedConfig);
	}

	private void drawPathSegments(Graphics2D graphics, List<Point> canvasPoints, List<WorldPoint> waypoints,
	                               List<Boolean> isWindCatcherSegment, Point startCanvas, CachedConfig cachedConfig)
	{
		Color normalColor = cachedConfig.getPathColor();
		Color windCatcherColor = cachedConfig.getWindCatcherColor();

		int segmentStart = -1;
		Color currentColor = isWindCatcherSegment.get(0) ? windCatcherColor : normalColor;

		for (int i = 0; i <= canvasPoints.size(); i++)
		{
			boolean isLastPoint = (i == canvasPoints.size());
			boolean colorChanged = !isLastPoint && i > 0 &&
				(!isWindCatcherSegment.get(i).equals(isWindCatcherSegment.get(i - 1)));

			if (colorChanged || isLastPoint)
			{
				int segmentEnd = i - 1;
				drawSinglePathSegment(graphics, canvasPoints, segmentStart, segmentEnd, startCanvas, currentColor, cachedConfig);

				if (!isLastPoint)
				{
					segmentStart = i - 1;
					currentColor = isWindCatcherSegment.get(i) ? windCatcherColor : normalColor;
				}
			}
		}
	}

	private void drawSinglePathSegment(Graphics2D graphics, List<Point> canvasPoints, int startIdx, int endIdx,
	                                    Point startCanvas, Color color, CachedConfig cachedConfig)
	{
		if (startIdx > endIdx)
		{
			return;
		}

		Path2D.Double path = new Path2D.Double();

		if (startIdx == -1)
		{
			path.moveTo(startCanvas.getX(), startCanvas.getY());
			if (canvasPoints.size() > 0)
			{
				path.lineTo(canvasPoints.get(0).getX(), canvasPoints.get(0).getY());
			}
			startIdx = 0;
		}
		else
		{
			path.moveTo(canvasPoints.get(startIdx).getX(), canvasPoints.get(startIdx).getY());
		}

		if (startIdx == endIdx)
		{
		}
		else if (endIdx - startIdx == 1)
		{
			path.lineTo(canvasPoints.get(endIdx).getX(), canvasPoints.get(endIdx).getY());
		}
		else
		{
			for (int i = startIdx; i < endIdx; i++)
			{
				Point p0 = i > 0 ? canvasPoints.get(i - 1) : canvasPoints.get(i);
				Point p1 = canvasPoints.get(i);
				Point p2 = canvasPoints.get(i + 1);
				Point p3 = (i + 2 < canvasPoints.size()) ? canvasPoints.get(i + 2) : p2;

				double tension = 0.1;

				double cp1x = p1.getX() + (p2.getX() - p0.getX()) * tension;
				double cp1y = p1.getY() + (p2.getY() - p0.getY()) * tension;

				double cp2x = p2.getX() - (p3.getX() - p1.getX()) * tension;
				double cp2y = p2.getY() - (p3.getY() - p1.getY()) * tension;

				path.curveTo(cp1x, cp1y, cp2x, cp2y, p2.getX(), p2.getY());
			}
		}

		graphics.setColor(color);
		graphics.draw(path);
	}

	private static class WindCatcherGroup
	{
		final WorldPoint firstLocation;
		final WorldPoint lastLocation;

		WindCatcherGroup(WorldPoint firstLocation, WorldPoint lastLocation)
		{
			this.firstLocation = firstLocation;
			this.lastLocation = lastLocation;
		}
	}

	private List<WindCatcherGroup> getWindCatcherGroups()
	{
		List<WindCatcherGroup> groups = new ArrayList<>();
		List<RouteWaypoint> staticRoute = plugin.getGameState().getCurrentStaticRoute();

		if (staticRoute == null || staticRoute.isEmpty())
		{
			return groups;
		}

		var completedIndices = plugin.getGameState().getCompletedWaypointIndices();

		WorldPoint groupStart = null;
		WorldPoint groupEnd = null;

		for (int i = 0; i < staticRoute.size(); i++)
		{
			if (completedIndices.contains(i))
			{
				continue;
			}

			RouteWaypoint waypoint = staticRoute.get(i);

			if (waypoint.getType() == RouteWaypoint.WaypointType.USE_WIND_CATCHER)
			{
				if (groupStart == null)
				{
					groupStart = waypoint.getLocation();
				}
				groupEnd = waypoint.getLocation();
			}
			else if (!waypoint.getType().isNonNavigatableHelper())
			{
				if (groupStart != null && groupEnd != null)
				{
					groups.add(new WindCatcherGroup(groupStart, groupEnd));
				}
				groupStart = null;
				groupEnd = null;
			}
		}

		if (groupStart != null && groupEnd != null)
		{
			groups.add(new WindCatcherGroup(groupStart, groupEnd));
		}

		return groups;
	}

	private void renderPathTiles(Graphics2D graphics, List<WorldPoint> path)
	{
		if (path == null || path.isEmpty())
		{
			return;
		}

		var boostLocations = plugin.getGameState().getKnownSpeedBoostLocations();

		Set<WorldPoint> allBoostTiles = boostLocations.values()
				.stream()
				.flatMap(List::stream)
				.collect(Collectors.toSet());

		Color normalTileColor = new Color(255, 255, 0, 80);
		Color boostTileColor = new Color(135, 206, 250, 100);

		for (WorldPoint pathTile : path)
		{
			boolean isBoostTile = allBoostTiles.contains(pathTile);
			Color tileColor = isBoostTile ? boostTileColor : normalTileColor;
			String label = isBoostTile ? "Boost!" : null;

			RenderingUtils.renderTileHighlightAtWorldPoint(client, graphics, pathTile, tileColor, label);
		}
	}

	private void renderWaypointLabels(Graphics2D graphics)
	{
		List<RouteWaypoint> staticRoute = plugin.getGameState().getCurrentStaticRoute();
		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (staticRoute == null || topLevelWorldView == null) return;

		graphics.setColor(Color.WHITE);
		int index = 0;
		for (RouteWaypoint waypoint : staticRoute)
		{
			WorldPoint loc = waypoint.getLocation();
			if (loc != null)
			{
				LocalPoint lp = RenderingUtils.localPointFromWorldIncludingExtended(topLevelWorldView, loc);
				Point cp = lp != null ? Perspective.getCanvasTextLocation(client, graphics, lp, "", 20) : null;
				if (cp != null)
				{
					graphics.drawString(String.format("#%d %s @ (%d, %d)", index, waypoint.getType(), loc.getX(), loc.getY()), cp.getX(), cp.getY());
				}
			}
			index++;
		}
	}

	private void renderWindCatcherHighlights(Graphics2D graphics)
	{
		List<RouteWaypoint> staticRoute = plugin.getGameState().getCurrentStaticRoute();
		if (staticRoute == null || staticRoute.isEmpty())
		{
			return;
		}

		CachedConfig cachedConfig = plugin.getCachedConfig();
		Color windCatcherColor = cachedConfig.getWindCatcherColor();

		WorldPoint lastWindCatcherTile = null;
		for (RouteWaypoint waypoint : staticRoute)
		{
			if (waypoint.getType() == RouteWaypoint.WaypointType.USE_WIND_CATCHER)
			{
				WorldPoint loc = waypoint.getLocation();
				if (loc != null)
				{
					// Only highlight the first wind catcher in a sequence
					if (lastWindCatcherTile == null)
					{
						RenderingUtils.renderTileHighlightAtWorldPoint(client, graphics, loc, windCatcherColor, "USE WIND CATCHER");
					}
					lastWindCatcherTile = loc;
				}
			}
			else
			{
				lastWindCatcherTile = null;
			}
		}
	}

	private void renderPathfindingHints(Graphics2D graphics)
	{
		List<RouteWaypoint> staticRoute = plugin.getGameState().getCurrentStaticRoute();
		if (staticRoute == null || staticRoute.isEmpty())
		{
			return;
		}

		Color hintColor = new Color(255, 255, 0, 100);
		for (RouteWaypoint waypoint : staticRoute)
		{
			if (waypoint.getType() == RouteWaypoint.WaypointType.PATHFINDING_HINT)
			{
				WorldPoint loc = waypoint.getLocation();
				if (loc != null)
				{
					RenderingUtils.renderTileHighlightAtWorldPoint(client, graphics, loc, hintColor, null);
				}
			}
		}
	}
}
