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
	{ROUTES.put(Difficulty.SWORDFISH, List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2254, 3469, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2272, 3475, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2273, 3487, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2260, 3497, 0)), // ithell
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2088, 3232, 0)), // ithell
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2104, 3229, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2118, 3230, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3254, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2134, 3263, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2132, 3269, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3277, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2121, 3289, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2129, 3297, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2147, 3295, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2158, 3293, 0)), // ithell
			new RouteWaypoint(2, WaypointType.PORTAL_EXIT, new WorldPoint(2260, 3504, 0)), // ithell
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2264, 3516, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2265, 3532, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2248, 3541, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2253, 3559, 0)),
			new RouteWaypoint(2, WaypointType.PORTAL_ENTER, new WorldPoint(2241, 3574, 0)), // amlodd
			new RouteWaypoint(2, WaypointType.PORTAL_EXIT, new WorldPoint(2080, 3215, 0)), // amlodd
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2110, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2119, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2122, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2125, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2128, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2132, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2134, 3214, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2141, 3216, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2140, 3230, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2132, 3233, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2150, 3243, 0)),
			new RouteWaypoint(2, WaypointType.PORTAL_ENTER, new WorldPoint(2154, 3247, 0)), // amlodd
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2208, 3574, 0)), // amlodd
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2190, 3569, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2195, 3544, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2201, 3521, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2197, 3512, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2107, 3140, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2092, 3144, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2069, 3160, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2057, 3186, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2074, 3208, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2100, 3205, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2117, 3189, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2133, 3192, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2128, 3171, 0)) // cadarn
		));

		ROUTES.put(Difficulty.SHARK, List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2254, 3469, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2272, 3475, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2273, 3487, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2260, 3497, 0)), // ithell
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2088, 3232, 0)), // ithell
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2104, 3229, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2118, 3230, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2132, 3233, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3254, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2134, 3263, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3277, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2121, 3289, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2129, 3297, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2147, 3295, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2158, 3293, 0)), // ithell
			new RouteWaypoint(2, WaypointType.PORTAL_EXIT, new WorldPoint(2260, 3504, 0)), // ithell
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2264, 3516, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2265, 3532, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2248, 3541, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2253, 3559, 0)),
			new RouteWaypoint(2, WaypointType.PORTAL_ENTER, new WorldPoint(2241, 3574, 0)), // amlodd
			new RouteWaypoint(2, WaypointType.PORTAL_EXIT, new WorldPoint(2080, 3215, 0)), // amlodd
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2110, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2119, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2122, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2125, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2128, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2132, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2134, 3214, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2141, 3216, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2140, 3230, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2150, 3243, 0)),
			new RouteWaypoint(2, WaypointType.PORTAL_ENTER, new WorldPoint(2154, 3247, 0)), // amlodd
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2208, 3574, 0)), // amlodd
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2190, 3569, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2195, 3544, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2201, 3521, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2197, 3512, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2008, 3574, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2092, 3144, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2069, 3160, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2057, 3186, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2074, 3208, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2100, 3205, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2117, 3189, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2133, 3192, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2128, 3171, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2197, 3502, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2197, 3492, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2185, 3478, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2175, 3476, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2173, 3469, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2157, 3464, 0)), // cryws
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2120, 3373, 0)), // cryws
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2086, 3376, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2079, 3389, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2094, 3396, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2105, 3406, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2085, 3413, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2081, 3426, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2093, 3436, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2110, 3439, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2118, 3439, 0)), //cryws
			new RouteWaypoint(4, WaypointType.PORTAL_EXIT, new WorldPoint(2148, 3464, 0)), // cryws
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2132, 3461, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2108, 3468, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2103, 3483, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2105, 3495, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2125, 3496, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2152, 3492, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2148, 3504, 0)),
			new RouteWaypoint(4, WaypointType.PORTAL_ENTER, new WorldPoint(2162, 3508, 0)), // hefin
			new RouteWaypoint(5, WaypointType.PORTAL_EXIT, new WorldPoint(2253, 3634, 0)), // hefin
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2240, 3629, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2231, 3618, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2228, 3609, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2224, 3596, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2220, 3592, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2190, 3598, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2186, 3596, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2183, 3594, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2180, 3592, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2176, 3591, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2173, 3590, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2169, 3589, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2165, 3588, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2166, 3589, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2161, 3591, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2162, 3590, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2163, 3589, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2164, 3588, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2165, 3589, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2166, 3588, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2160, 3592, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2155, 3594, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2152, 3595, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2148, 3596, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2144, 3596, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2140, 3597, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2135, 3597, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2131, 3598, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2123, 3599, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2112, 3591, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2098, 3583, 0)),
			new RouteWaypoint(5, WaypointType.PORTAL_ENTER, new WorldPoint(2106, 3574, 0)) // hefin
		));

		ROUTES.put(Difficulty.MARLIN, List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2254, 3469, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2272, 3475, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2273, 3487, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2260, 3497, 0)), // ithell
			new RouteWaypoint(WaypointType.PORTAL_EXIT, new WorldPoint(2088, 3232, 0)), // ithell
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2104, 3229, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2118, 3230, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2132, 3233, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2128, 3254, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2134, 3263, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2147, 3295, 0)),
			new RouteWaypoint(WaypointType.PORTAL_ENTER, new WorldPoint(2158, 3293, 0)), // ithell
			new RouteWaypoint(2, WaypointType.PORTAL_EXIT, new WorldPoint(2260, 3504, 0)), // ithell
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2264, 3516, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2265, 3532, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2248, 3541, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2253, 3559, 0)),
			new RouteWaypoint(2, WaypointType.PORTAL_ENTER, new WorldPoint(2241, 3574, 0)), // amlodd
			new RouteWaypoint(2, WaypointType.PORTAL_EXIT, new WorldPoint(2080, 3215, 0)), // amlodd
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2110, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2119, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2122, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2125, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2128, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2132, 3214, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2134, 3214, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2141, 3216, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2140, 3230, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2150, 3243, 0)),
			new RouteWaypoint(2, WaypointType.PORTAL_ENTER, new WorldPoint(2154, 3247, 0)), // amlodd
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2208, 3574, 0)), // amlodd
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2190, 3569, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2195, 3544, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2201, 3521, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2197, 3512, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2008, 3574, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2092, 3144, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2069, 3160, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2057, 3186, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2074, 3208, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2100, 3205, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2117, 3189, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2133, 3192, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2128, 3171, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2197, 3502, 0)), // cadarn
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2197, 3492, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2185, 3478, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2175, 3476, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2173, 3469, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2157, 3464, 0)), // cryws
			new RouteWaypoint(3, WaypointType.PORTAL_EXIT, new WorldPoint(2120, 3373, 0)), // cryws
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2086, 3376, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2079, 3389, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2094, 3396, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2105, 3406, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2108, 3409, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2111, 3413, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2113, 3416, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2116, 3419, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2116, 3426, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2112, 3432, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2110, 3439, 0)),
			new RouteWaypoint(3, WaypointType.PORTAL_ENTER, new WorldPoint(2118, 3439, 0)), //cryws
			new RouteWaypoint(4, WaypointType.PORTAL_EXIT, new WorldPoint(2148, 3464, 0)), // cryws
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2132, 3461, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2108, 3468, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2103, 3483, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2105, 3495, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2125, 3496, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2152, 3492, 0)),
			new RouteWaypoint(4, WaypointType.SHIPMENT, new WorldPoint(2148, 3504, 0)),
			new RouteWaypoint(4, WaypointType.PORTAL_ENTER, new WorldPoint(2162, 3508, 0)), // hefin
			new RouteWaypoint(5, WaypointType.PORTAL_EXIT, new WorldPoint(2253, 3634, 0)), // hefin
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2240, 3629, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2231, 3618, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2228, 3609, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2224, 3596, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2220, 3592, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2190, 3598, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2186, 3596, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2183, 3594, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2180, 3592, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2176, 3591, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2173, 3590, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2169, 3589, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2165, 3588, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2166, 3589, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2161, 3591, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2162, 3590, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2163, 3589, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2164, 3588, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2165, 3589, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2166, 3588, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2160, 3592, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2155, 3594, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2152, 3595, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2148, 3596, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2144, 3596, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2140, 3597, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2135, 3597, 0)),
			new RouteWaypoint(5, WaypointType.PATHFINDING_HINT, new WorldPoint(2131, 3598, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2123, 3599, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2112, 3591, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2098, 3583, 0)),
			new RouteWaypoint(5, WaypointType.PORTAL_ENTER, new WorldPoint(2106, 3574, 0)), // hefin
			new RouteWaypoint(5, WaypointType.PORTAL_EXIT, new WorldPoint(2171, 3508, 0)), // hefin
			new RouteWaypoint(5, WaypointType.PORTAL_ENTER, new WorldPoint(2190, 3508, 0)), // cadarn
			new RouteWaypoint(5, WaypointType.PORTAL_EXIT, new WorldPoint(2205, 3508, 0)), // cadarn
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2222, 3515, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2214, 3538, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2224, 3546, 0)),
			new RouteWaypoint(5, WaypointType.SHIPMENT, new WorldPoint(2222, 3570, 0)),
			new RouteWaypoint(5, WaypointType.PORTAL_ENTER, new WorldPoint(2207, 3584, 0)),
			new RouteWaypoint(6, WaypointType.PORTAL_EXIT, new WorldPoint(2107, 3560, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2096, 3559, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2078, 3553, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2070, 3538, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2084, 3527, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2081, 3504, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2084, 3487, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2089, 3464, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2097, 3444, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2093, 3436, 0)),
			new RouteWaypoint(6, WaypointType.SHIPMENT, new WorldPoint(2104, 3431, 0)),
			new RouteWaypoint(6, WaypointType.PORTAL_ENTER, new WorldPoint(2105, 3424, 0)),
			new RouteWaypoint(7, WaypointType.PORTAL_EXIT, new WorldPoint(2198, 3584, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2176, 3580, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2174, 3562, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2181, 3543, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2163, 3543, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2155, 3568, 0)),
			new RouteWaypoint(7, WaypointType.PORTAL_ENTER, new WorldPoint(2142, 3582, 0)),
			new RouteWaypoint(7, WaypointType.PORTAL_EXIT, new WorldPoint(2137, 3253, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2132, 3269, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2128, 3277, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2132, 3284, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2121, 3289, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2129, 3297, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2131, 3318, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2142, 3336, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2154, 3346, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2147, 3368, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2128, 3381, 0)),
			new RouteWaypoint(7, WaypointType.SHIPMENT, new WorldPoint(2120, 3368, 0)),
			new RouteWaypoint(7, WaypointType.PORTAL_ENTER, new WorldPoint(2126, 3357, 0)),
			new RouteWaypoint(8, WaypointType.PORTAL_EXIT, new WorldPoint(2133, 3582, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2118, 3568, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2126, 3539, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2123, 3538, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2121, 3534, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2119, 3530, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2119, 3526, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2119, 3522, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2121, 3517, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2124, 3514, 0)),
			new RouteWaypoint(8, WaypointType.PATHFINDING_HINT, new WorldPoint(2128, 3513, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2131, 3512, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2140, 3518, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2162, 3520, 0)),
			new RouteWaypoint(8, WaypointType.PORTAL_ENTER, new WorldPoint(2172, 3523, 0)),
			new RouteWaypoint(8, WaypointType.PORTAL_EXIT, new WorldPoint(2107, 3413, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2094, 3419, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2085, 3413, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2081, 3426, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2080, 3439, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2080, 3454, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2096, 3504, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2107, 3520, 0)),
			new RouteWaypoint(8, WaypointType.SHIPMENT, new WorldPoint(2091, 3541, 0)),
			new RouteWaypoint(8, WaypointType.PORTAL_ENTER, new WorldPoint(2106, 3543, 0))
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
