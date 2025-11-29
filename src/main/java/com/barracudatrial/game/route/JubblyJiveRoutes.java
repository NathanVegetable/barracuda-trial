package com.barracudatrial.game.route;

import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.barracudatrial.game.route.JubblyJiveConfig.TOAD_PICKUP_LOCATION;
import static com.barracudatrial.game.route.JubblyJiveConfig.TOAD_PILLARS;
import static com.barracudatrial.game.route.RouteWaypoint.WaypointType;

// S 931.10 SailingBtHud.BT_PARTIAL_TEXT text = holding toad count
public class JubblyJiveRoutes
{
	private static final Map<Difficulty, List<RouteWaypoint>> ROUTES = new HashMap<>();

	static
	{
		// SWORDFISH difficulty - 1 lap, 20 shipments + 4 toads
		// Captured 2025-11-23
		ROUTES.put(Difficulty.SWORDFISH, List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2413, 3016, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2396, 3010, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2378, 3008, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2362, 2998, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2353, 2977, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2345, 2974, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2320, 2976, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2285, 2978, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2280, 2978, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2278, 2979, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2276, 2981, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2273, 2984, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2269, 2987, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2265, 2990, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2260, 2992, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2255, 2992, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2250, 2992, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2246, 2994, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2243, 2997, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2241, 3000, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2239, 3004, 0)),
			new RouteWaypoint(WaypointType.TOAD_PICKUP, TOAD_PICKUP_LOCATION), //baseid 59169 imposter 59170
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2248, 3023, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2311, 3021, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2330, 3016, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2353, 3005, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2358, 2964, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[4]), // ID : 59148 IMPOSTER: 59150. with toad imposter = 59149
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2367, 2948, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2386, 2940, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2421, 2938, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[5]), // ID : 59154 IMPOSTER: 59156. with toad imposter = 59155
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2434, 2949, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2432, 2977, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2979, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2983, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2987, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2433, 2982, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2991, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2992, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2439, 2990, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2435, 2985, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2988, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[6]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[7]) // ID : 59166 IMPOSTER: 59168. with toad imposter = 59167
		));

		
		// SHARK difficulty - 2 lap, 38 shipments + 12 toads
		// Captured 2025-11-23
		ROUTES.put(Difficulty.SHARK, List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2413, 3016, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2396, 3010, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2378, 3008, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2362, 2998, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2353, 2977, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2345, 2974, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2332, 2972, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2320, 2976, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2297, 2978, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2285, 2978, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2280, 2978, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2278, 2979, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2276, 2981, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2273, 2984, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2269, 2987, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2265, 2990, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2260, 2992, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2255, 2992, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2250, 2992, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2243, 2997, 0)),
			new RouteWaypoint(WaypointType.TOAD_PICKUP, TOAD_PICKUP_LOCATION), //baseid 59169 imposter 59170
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2239, 3004, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2238, 3012, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2239, 3008, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2248, 3023, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2273, 3006, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[0]), // ID : 59124
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2290, 2998, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[1]), // ID : 59130
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2301, 3018, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[2]), // ID : 59136
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2311, 3021, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2330, 3016, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2345, 2991, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2358, 2964, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[4]), // ID : 59148 IMPOSTER: 59150. with toad imposter = 59149
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2367, 2948, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2386, 2940, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2421, 2938, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[5]), // ID : 59154 IMPOSTER: 59156. with toad imposter = 59155
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2434, 2949, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2432, 2977, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2979, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2983, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2987, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2433, 2982, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2991, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2992, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2439, 2990, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2435, 2985, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2988, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[6]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161

			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[7]), // 59166 IMPOSTER: 59168. with toad imposter = 59167
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2431, 3014, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2428, 3019, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2425, 3022, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2420, 3026, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2417, 3026, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2412, 3026, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2407, 3024, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2393, 3020, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2371, 3022, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2341, 3031, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2338, 3029, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2335, 3026, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2333, 3022, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2332, 3017, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2333, 3011, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2353, 3005, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[3]), // ID : 59142
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2379, 2993, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2382, 2970, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[4]), // ID : 59148 IMPOSTER: 59150. with toad imposter = 59149
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2390, 2956, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2420, 2959, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[5]), // ID : 59154 IMPOSTER: 59156. with toad imposter = 59155
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2422, 2975, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2414, 2992, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[6]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2416, 3001, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[7]) // ID : 59166 IMPOSTER: 59168. with toad imposter = 59167
		));

		// MARLIN difficulty - 3 laps, 56 shipments + 12 toads
		ROUTES.put(Difficulty.MARLIN, 
			List.of(
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2412, 3026, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2396, 3010, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2378, 3008, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2362, 2998, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2345, 2974, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2320, 2976, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2297, 2978, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2280, 2978, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2278, 2979, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2276, 2981, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2273, 2984, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2269, 2987, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2265, 2990, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2260, 2992, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2255, 2992, 0)),
			new RouteWaypoint(1, WaypointType.TOAD_PICKUP, TOAD_PICKUP_LOCATION,
				List.of(
					new WorldPoint(2270, 2987, 0),
					new WorldPoint(2290, 2980, 0)
				)
			), //baseid 59169 imposter 59170
			new RouteWaypoint(WaypointType.USE_WIND_CATCHER, new WorldPoint(2250, 3005, 0)),
			new RouteWaypoint(WaypointType.USE_WIND_CATCHER, new WorldPoint(2253, 3008, 0)),
			new RouteWaypoint(WaypointType.USE_WIND_CATCHER, new WorldPoint(2259, 3011, 0)),
			new RouteWaypoint(WaypointType.USE_WIND_CATCHER, new WorldPoint(2265, 3010, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2273, 3006, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2290, 2998, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[1]), // ID : 59130
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2301, 3018, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[2]), // ID : 59136,
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2311, 3021, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2330, 3016, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2345, 2991, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2353, 2977, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2358, 2964, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[4]), // ID : 59148 IMPOSTER: 59150. with toad imposter = 59149
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2367, 2948, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2386, 2940, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2421, 2938, 0)),
			new JubblyJiveToadPillarWaypoint(TOAD_PILLARS[5]), // ID : 59154 IMPOSTER: 59156. with toad imposter = 59155
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2434, 2949, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2979, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2983, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2987, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2433, 2982, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2991, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2992, 0)),
			new RouteWaypoint(WaypointType.SHIPMENT, new WorldPoint(2439, 2990, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2435, 2985, 0)),
			new RouteWaypoint(WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2988, 0)),
			
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[7]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2433, 3016, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2429, 3015, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2426, 3012, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2423, 3009, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2420, 3006, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2416, 3001, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[6]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2414, 2992, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2422, 2975, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2420, 2959, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[5]), // ID : 59154 IMPOSTER: 59156. with toad imposter = 59155
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2409, 2948, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2390, 2956, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[4]), // ID : 59148 IMPOSTER: 59150. with toad imposter = 59149
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2382, 2970, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2376, 2976, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2373, 2979, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2369, 2982, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2365, 2985, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[3]), // ID : 59142
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2332, 2972, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2352, 2985, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2353, 2994, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2351, 2994, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2349, 2994, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2347, 2994, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2344, 2993, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2344, 2990, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2346, 2987, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2348, 2985, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2350, 2983, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2349, 2979, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2347, 2978, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2345, 2976, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2339, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2335, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2331, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2327, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2323, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2318, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2315, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2313, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2310, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2308, 2974, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2303, 2978, 0)),
			// add fake waypoint? too far
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2289, 2980, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2286, 2981, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2282, 2982, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2278, 2984, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2273, 2987, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2267, 2989, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2260, 2992, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2256, 2994, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2250, 2992, 0),
				List.of(
					new WorldPoint(2266, 2990, 0),
					new WorldPoint(2279, 2984, 0),
					new WorldPoint(2298, 2978, 0),
					new WorldPoint(2316, 2974, 0),
					new WorldPoint(2330, 2974, 0),
					new WorldPoint(2351, 2983, 0)
				)
			),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2243, 2997, 0)),
			new RouteWaypoint(2, WaypointType.TOAD_PICKUP, TOAD_PICKUP_LOCATION), //baseid 59169 imposter 59170
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2239, 3004, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2239, 3008, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[1]), // ID : 59130
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2248, 3023, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2297, 2997, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2298, 2996, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2299, 2995, 0)),
			new RouteWaypoint(2, WaypointType.USE_WIND_CATCHER, new WorldPoint(2300, 2993, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2301, 2992, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2326, 2982, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2333, 2956, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2335, 2946, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2341, 2937, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2355, 2930, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2370, 2929, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2366, 2933, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2367, 2936, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2368, 2940, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2370, 2945, 0)),
			new JubblyJiveToadPillarWaypoint(2, TOAD_PILLARS[4]), // ID : 59148 IMPOSTER: 59150. with toad imposter = 59149
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2379, 2941, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2383, 2941, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2389, 2940, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2394, 2938, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2396, 2932, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2400, 2925, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2416, 2923, 0)),
			new RouteWaypoint(2, WaypointType.PATHFINDING_HINT, new WorldPoint(2423, 2923, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2439, 2926, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2463, 2944, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2464, 2961, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2449, 2971, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2447, 2989, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2447, 2992, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2447, 2995, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2447, 2998, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2447, 3004, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2446, 3008, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2446, 3013, 0)),
			new RouteWaypoint(2, WaypointType.SHIPMENT, new WorldPoint(2447, 3001, 0)),

			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[7]), // ID : 59166 IMPOSTER: 59168. with toad imposter = 59167
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2438, 3011, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 3011, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2428, 3011, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2419, 3014, 0)),
			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[6]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2413, 3016, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2393, 3020, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2371, 3022, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2353, 3028, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2351, 3029, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2348, 3029, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2345, 3030, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2341, 3031, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2323, 3037, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2321, 3038, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2319, 3040, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2317, 3042, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2314, 3042, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2309, 3041, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2307, 3039, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2307, 3036, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2307, 3033, 0)),
			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[2]), // ID : 59136
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2353, 3005, 0)),
			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[3]), // ID : 59142
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2379, 2993, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2389, 2986, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2392, 2984, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2394, 2982, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2396, 2980, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2402, 2973, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2403, 2972, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2406, 2970, 0)),
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2408, 2967, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2412, 2968, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2415, 2965, 0)),
			new RouteWaypoint(3, WaypointType.USE_WIND_CATCHER, new WorldPoint(2419, 2961, 0)),
			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[5]), // ID : 59154 IMPOSTER: 59156. with toad imposter = 59155
			new RouteWaypoint(3, WaypointType.SHIPMENT, new WorldPoint(2432, 2977, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2979, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2983, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2987, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2433, 2982, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2991, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2434, 2992, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2435, 2985, 0)),
			new RouteWaypoint(3, WaypointType.PATHFINDING_HINT, new WorldPoint(2436, 2988, 0)),
			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[6]), // ID : 59160 IMPOSTER: 59162. with toad imposter = 59161
			new JubblyJiveToadPillarWaypoint(3, TOAD_PILLARS[7]) // ID : 59166 IMPOSTER: 59168. with toad imposter = 59167
		)
	);
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
