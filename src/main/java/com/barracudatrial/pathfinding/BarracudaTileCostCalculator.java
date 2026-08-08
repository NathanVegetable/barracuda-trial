package com.barracudatrial.pathfinding;

import com.barracudatrial.RouteOptimization;
import net.runelite.api.coords.WorldPoint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BarracudaTileCostCalculator
{
	private static final int DISCOURAGED_TILE_COST = 100;
	private static final int NEARBY_TILE_COST = 3;

	private final RouteOptimization routeOptimization;
	private final List<PathObjective> objectives;

	private int speedBoostTilesRemaining = 0;
	private WorldPoint lastTile = null;
	private boolean wasOnHintLastTile = false;
	private final Set<WorldPoint> consumedBoosts = new HashSet<>();

	private final Set<WorldPoint> discouragedTiles;
	private final Set<WorldPoint> nearDiscouragedTiles;
	private final Set<WorldPoint> cloudDangerZones;
	private final Map<WorldPoint, List<WorldPoint>> boostGrabbableTiles;

	public BarracudaTileCostCalculator(
		Map<WorldPoint, List<WorldPoint>> knownSpeedBoostLocations,
		Set<WorldPoint> knownRockLocations,
		Set<WorldPoint> knownFetidPoolLocations,
		Set<WorldPoint> knownToadPillarLocations,
		Set<WorldPoint> cloudLocations,
		int exclusionZoneMinX,
		int exclusionZoneMaxX,
		int exclusionZoneMinY,
		int exclusionZoneMaxY,
		WorldPoint primaryObjectiveLocation,
		WorldPoint secondaryObjectiveLocation,
		RouteOptimization routeOptimization,
		int boatExclusionWidth,
		int boatExclusionHeight,
		List<PathObjective> objectives,
		Set<WorldPoint> knownLandTiles)
	{
		this.routeOptimization = routeOptimization;
		this.objectives = objectives != null ? objectives : List.of();
		this.boostGrabbableTiles = knownSpeedBoostLocations;

		this.cloudDangerZones = precomputeCloudDangerZones(cloudLocations);

		this.discouragedTiles = new HashSet<>();
		discouragedTiles.addAll(knownLandTiles);
		discouragedTiles.addAll(knownRockLocations);
		discouragedTiles.addAll(knownFetidPoolLocations);
		discouragedTiles.addAll(knownToadPillarLocations);
		discouragedTiles.addAll(cloudDangerZones);
		addExclusionZoneTiles(discouragedTiles, exclusionZoneMinX, exclusionZoneMaxX, exclusionZoneMinY, exclusionZoneMaxY);
		addBoatExclusionZoneTiles(discouragedTiles, primaryObjectiveLocation, boatExclusionWidth, boatExclusionHeight);
		addBoatExclusionZoneTiles(discouragedTiles, secondaryObjectiveLocation, boatExclusionWidth, boatExclusionHeight);

		this.nearDiscouragedTiles = precomputeTileProximity(discouragedTiles, 1);
	}

	public double getTileCost(WorldPoint from, WorldPoint to, int objectiveIndex)
	{
		boolean isHintTile = isApproachHintFor(to, objectiveIndex);

		if (lastTile == null || !lastTile.equals(from))
		{
			speedBoostTilesRemaining = 0;
			wasOnHintLastTile = false;
		}

		if (isHintTile)
		{
			lastTile = to;
			wasOnHintLastTile = true;
			return -10.0;
		}

		lastTile = to;

		double cost = 1.0;

		if (wasOnHintLastTile)
		{
			cost *= 0.75;
		}

		wasOnHintLastTile = false;

		WorldPoint unconsumedBoost = getUnconsumedBoost(to);
		if (unconsumedBoost != null)
		{
			cost = routeOptimization.getSpeedBoostCost();
			speedBoostTilesRemaining = 15;
			consumedBoosts.add(unconsumedBoost);
		}
		else if (speedBoostTilesRemaining > 0)
		{
			cost /= 2.0; // Double speed
			speedBoostTilesRemaining--;
		}

		if (discouragedTiles.contains(to))
		{
			cost += DISCOURAGED_TILE_COST;
			// Lightning clouds cancel any active speed boost
			if (cloudDangerZones.contains(to))
			{
				speedBoostTilesRemaining = 0;
			}
		}
		else if (nearDiscouragedTiles.contains(to))
		{
			cost += NEARBY_TILE_COST;
		}

		return cost;
	}

	/**
	 * Hints only discount the leg that approaches the objective they were written for, so a hint
	 * placed beyond an objective cannot pull the path toward it before that objective is cleared.
	 */
	private boolean isApproachHintFor(WorldPoint tile, int objectiveIndex)
	{
		if (objectiveIndex < 0 || objectiveIndex >= objectives.size())
		{
			return false;
		}

		return objectives.get(objectiveIndex).getApproachHints().contains(tile);
	}

	private WorldPoint getUnconsumedBoost(WorldPoint tile)
	{
		List<WorldPoint> boosts = boostGrabbableTiles.get(tile);
		if (boosts != null && boosts.contains(tile) && !consumedBoosts.contains(tile))
		{
			return tile;
		}
		return null;
	}

	/**
	 * Get a snapshot of all current discouraged tiles for path stability tracking
	 */
	public Set<WorldPoint> getDangerZoneSnapshot()
	{
		return new HashSet<>(discouragedTiles);
	}

	private static void addExclusionZoneTiles(Set<WorldPoint> tiles, int minX, int maxX, int minY, int maxY)
	{
		if (minX == 0 && maxX == 0 && minY == 0 && maxY == 0)
		{
			return;
		}

		for (int x = minX; x <= maxX; x++)
		{
			for (int y = minY; y <= maxY; y++)
			{
				tiles.add(new WorldPoint(x, y, 0));
			}
		}
	}

	// Discourage tiles around objectives so the pathfinder doesn't try to cut through the boat
	private static void addBoatExclusionZoneTiles(Set<WorldPoint> tiles, WorldPoint center, int width, int height)
	{
		if (center == null)
		{
			return;
		}

		int halfWidth = width / 2;
		int halfHeight = height / 2;

		for (int x = center.getX() - halfWidth; x <= center.getX() + halfWidth; x++)
		{
			for (int y = center.getY() - halfHeight; y <= center.getY() + halfHeight; y++)
			{
				tiles.add(new WorldPoint(x, y, center.getPlane()));
			}
		}
	}

	private Set<WorldPoint> precomputeTileProximity(Set<WorldPoint> locations, int maxDistance)
	{
		Set<WorldPoint> proximityTiles = new HashSet<>();
		int maxDistSq = maxDistance * maxDistance;

		for (WorldPoint location : locations)
		{
			int baseX = location.getX();
			int baseY = location.getY();
			int plane = location.getPlane();

			for (int dx = -maxDistance; dx <= maxDistance; dx++)
			{
				int dxSq = dx * dx;

				for (int dy = -maxDistance; dy <= maxDistance; dy++)
				{
					if (dx == 0 && dy == 0)
					{
						continue; // skip the location tile itself
					}

					int distSq = dxSq + dy * dy;
					if (distSq > maxDistSq)
					{
						continue;
					}

					WorldPoint tile = new WorldPoint(baseX + dx, baseY + dy, plane);

					// Don't consider tiles that are themselves location tiles
					if (!locations.contains(tile))
					{
						proximityTiles.add(tile);
					}
				}
			}
		}

		return proximityTiles;
	}

	/**
	 * Precomputes all tiles within cloud danger zones for O(1) lookup
	 */
	private Set<WorldPoint> precomputeCloudDangerZones(Set<WorldPoint> cloudLocations)
	{
		Set<WorldPoint> dangerZones = new HashSet<>();

		for (WorldPoint cloudLoc : cloudLocations)
		{
			int plane = cloudLoc.getPlane();

			// Add all tiles within distance 3 of cloud
			for (int dx = -3; dx <= 3; dx++)
			{
				for (int dy = -3; dy <= 3; dy++)
				{
					WorldPoint tile = new WorldPoint(cloudLoc.getX() + dx, cloudLoc.getY() + dy, plane);
					double dist = Math.sqrt(dx * dx + dy * dy);
					if (dist <= 3)
					{
						dangerZones.add(tile);
					}
				}
			}
		}

		return dangerZones;
	}
}
