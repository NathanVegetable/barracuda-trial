package com.barracudatrial.game.route;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barracudatrial.game.route.RouteWaypoint.WaypointType;

// Gwenith Glide - Crystal waters navigation with portals and motes
public class GwenithGlideRoutes
{
	private static final Map<Difficulty, List<RouteWaypoint>> ROUTES = new HashMap<>();

	static
	{
		// TODO: SWORDFISH difficulty - capture route data
		ROUTES.put(Difficulty.SWORDFISH, List.of(
			// Route waypoints to be added once captured
		));

		// TODO: SHARK difficulty - capture route data
		ROUTES.put(Difficulty.SHARK, List.of(
			// Route waypoints to be added once captured
		));

		// TODO: MARLIN difficulty - capture route data
		ROUTES.put(Difficulty.MARLIN, List.of(
			// Route waypoints to be added once captured
		));
	}

	/**
	 * Get the static route for a given difficulty.
	 * @param difficulty The difficulty level
	 * @return List of RouteWaypoints representing the optimal waypoint sequence,
	 *         or empty list if no route is defined for this difficulty
	 */
	public static List<RouteWaypoint> getRoute(Difficulty difficulty)
	{
		return ROUTES.getOrDefault(difficulty, new ArrayList<>());
	}
}
