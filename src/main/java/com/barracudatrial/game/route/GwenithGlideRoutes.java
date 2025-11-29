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
		ROUTES.put(Difficulty.SWORDFISH, List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2254, 3469, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2272, 3475, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2273, 3487, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2260, 3497, 0)), // ithell
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2088, 3232, 0)), // ithell
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2104, 3229, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2118, 3230, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3254, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2134, 3263, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3277, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2121, 3289, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2129, 3297, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2147, 3295, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2158, 3293, 0)), // ithell
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2260, 3504, 0)), // ithell
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2264, 3516, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2265, 3532, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2248, 3541, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2253, 3559, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2241, 3574, 0)), // amlodd
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2080, 3215, 0)), // amlodd
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2110, 3214, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2141, 3216, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2140, 3230, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2132, 3233, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2150, 3243, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2155, 3247, 0)), // amlodd
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(3107, 3140, 0)), // amlodd
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2190, 3569, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2195, 3544, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2201, 3521, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2197, 3512, 0)), // cadarn
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2008, 3574, 0)), // cadarn
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2092, 3144, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2069, 3160, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2057, 3186, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2074, 3208, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2083, 3148, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2100, 3205, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2117, 3189, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2133, 3192, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2128, 3171, 0)) // cadarn
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
