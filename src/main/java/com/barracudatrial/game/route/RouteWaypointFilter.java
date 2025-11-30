package com.barracudatrial.game.route;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility for common route waypoint filtering and searching operations
 * Eliminates repeated patterns across rendering and path planning code
 */
public class RouteWaypointFilter
{
	/**
	 * Finds the next N navigable waypoints starting from a given index (wrapping around)
	 * Returns a list of waypoint locations
	 */
	public static List<WorldPoint> findNextNavigableWaypoints(
			List<RouteWaypoint> route,
			int startIndex,
			Set<Integer> completedIndices,
			int count)
	{
		List<WorldPoint> locations = new ArrayList<>(count);

		if (route == null || route.isEmpty() || startIndex < 0)
		{
			return locations;
		}

		int foundCount = 0;
		for (int offset = 0; offset < route.size() && foundCount < count; offset++)
		{
			int checkIndex = (startIndex + offset) % route.size();
			RouteWaypoint waypoint = route.get(checkIndex);

			if (!completedIndices.contains(checkIndex) && !waypoint.getType().isNonNavigableHelper())
			{
				locations.add(waypoint.getLocation());
				foundCount++;
			}
		}

		return locations;
	}

	/**
	 * Extracts all waypoint locations of a specific type and lap
	 */
	public static Set<WorldPoint> getLocationsByTypeAndLap(
			List<RouteWaypoint> route,
			RouteWaypoint.WaypointType type,
			int lap,
			Set<Integer> completedIndices)
	{
		Set<WorldPoint> locations = new java.util.HashSet<>();

		for (int i = 0; i < route.size(); i++)
		{
			if (completedIndices.contains(i))
			{
				continue;
			}

			RouteWaypoint waypoint = route.get(i);
			if (waypoint.getType() == type && waypoint.getLap() == lap)
			{
				locations.add(waypoint.getLocation());
			}
		}

		return locations;
	}
}
