package com.barracudatrial.game;

import com.barracudatrial.game.route.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

/**
 * Handles tracking of game objects in the Barracuda Trial minigame
 * Tracks clouds, rocks, speed boosts, lost supplies, boat location, toad pillars, etc
 */
@Slf4j
@RequiredArgsConstructor
public class ObjectTracker
{
	private final Client client;
	private final State state;

	private static final Set<Integer> ROCK_IDS = Set.of(
		ObjectID.SAILING_CHARTING_GENERIC_DESERT_TROUT,
		ObjectID.SAILING_CHARTING_GENERIC_LIGHTNING_ROD,
		ObjectID.SAILING_BARRACUDA_SHIPWRECK,
		ObjectID.OCEAN_OUTCROP_ROCK02,
		ObjectID.OCEAN_OUTCROP_ROCK03,
		ObjectID.OCEAN_OUTCROP_ROCK05,
		ObjectID.OCEAN_OUTCROP_ROCK06,
		ObjectID.OCEAN_OUTCROP_ROCK07,
		ObjectID.OCEAN_OUTCROP_ROCK08,
		ObjectID.OCEAN_OUTCROP_ROCK09,
		ObjectID.BOATS_CRYSTAL01_HULL01,
		ObjectID.BOATS_CRYSTAL01_HULL01_BROKEN01,
		ObjectID.BOATS_CRYSTAL01_HULL01_BROKEN01_M,
		ObjectID.BOATS_CRYSTAL01_HULL01_BROKEN02,
		ObjectID.BOATS_CRYSTAL01_HULL01_BROKEN02_M,
		ObjectID.BOATS_CRYSTAL01_HULL02,
		ObjectID.BOATS_CRYSTAL01_HULL02_MIRROR,
		ObjectID.BOATS_CRYSTAL01_SUPPORT01,
		ObjectID.BOATS_CRYSTAL01_SUPPORT01_M,
		ObjectID.BOATS_CRYSTAL01_SUPPORT02,
		ObjectID.BOATS_CRYSTAL01_SUPPORT02_M,
		ObjectID.BOATS_CRYSTAL01_MAST01_BROKEN01,
		ObjectID.BOATS_CRYSTAL01_MAST01_BROKEN02,
		ObjectID.BOATS_CRYSTAL01_MAST01_BROKEN03,
		ObjectID.BOATS_CRYSTAL01_WRECK01,
		ObjectID.BOATS_CRYSTAL01_WRECK02,
		ObjectID.BOATS_CRYSTAL01_WRECK03,
		ObjectID.BOATS_CRYSTAL01_WRECK04,
		ObjectID.BOATS_CRYSTAL01_WRECK05,
		ObjectID.BOATS_CRYSTAL01_BARREL01,
		ObjectID.BOATS_CRYSTAL01_CRATE01,
		ObjectID.BOATS_CRYSTAL01_LARGE01,
		ObjectID.BOATS_CRYSTAL01_OUTCROP01,
		ObjectID.BOATS_CRYSTAL01_OUTCROP02,
		ObjectID.BOATS_CRYSTAL01_OUTCROP03,
		ObjectID.BOATS_CRYSTAL01_OUTCROP04,
		ObjectID.BOATS_CRYSTAL01_OUTCROP05,
		ObjectID.ROCK_CRYSTAL02_FLECKED01,
		ObjectID.ROCK_CRYSTAL01_FLECKED01,
		ObjectID.ROCK_CRYSTAL01_FLECKED02,
		ObjectID.ROCK_CRYSTAL01_FLECKED03,
		ObjectID.ROCK_CRYSTAL01_FLECKED04,
		ObjectID.ROCK_CRYSTAL01_FLECKED05
	);

	private static final Set<Integer> SPEED_BOOST_IDS = Set.of(
		ObjectID.SAILING_RAPIDS, ObjectID.SAILING_RAPIDS_STRONG,
		ObjectID.SAILING_RAPIDS_POWERFUL, ObjectID.SAILING_RAPIDS_DEADLY
	);

	private static final Set<Integer> BOAT_NPC_IDS = Set.of(
			NpcID.BOAT_HP_NPC_TINY,
			NpcID.BOAT_HP_NPC_SMALL,
			NpcID.BOAT_HP_NPC_MEDIUM,
			NpcID.BOAT_HP_NPC_LARGE,
			NpcID.BOAT_HP_NPC_COLOSSAL
	);

	/**
	 * Updates hazard NPC tracking (e.g., lightning clouds for Tempor Tantrum)
	 */
	public void updateLightningCloudTracking()
	{
		if (!state.isInTrial())
		{
			state.clearLightningClouds();
			state.clearDangerousClouds();
			return;
		}

		state.clearLightningClouds();
		state.clearDangerousClouds();

		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return;
		}

		for (NPC npc : topLevelWorldView.npcs())
		{
			if (npc == null)
			{
				continue;
			}

			int npcId = npc.getId();
			if (TemporTantrumConfig.LIGHTNING_CLOUD_NPC_IDS.contains(npcId))
			{
				state.addLightningCloud(npc);

				if (!isCloudSafe(npc.getAnimation()))
				{
					state.addDangerousCloud(npc);
				}
			}
		}
	}

	public static boolean isCloudSafe(int animationId)
	{
		return animationId == State.CLOUD_ANIM_HARMLESS || animationId == State.CLOUD_ANIM_HARMLESS_ALT;
	}

	public void updateHazardsSpeedBoostsAndToadPillars()
	{
		if (!state.isInTrial())
		{
			return;
		}

		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return;
		}

		Scene scene = topLevelWorldView.getScene();
		if (scene == null)
		{
			return;
		}

		Tile[][][] regularTiles = scene.getTiles();
		if (regularTiles != null)
		{
			scanTileArrayForHazardsSpeedBoostsAndToadPillars(regularTiles);
		}

		// Skipping for performance - probably don't need to read extended for this
		// Tile[][][] extendedTiles = scene.getExtendedTiles();
		// if (extendedTiles != null)
		// {
		// 	scanTileArrayForHazardsSpeedBoostsAndToadPillars(extendedTiles);
		// }
	}

	private void scanTileArrayForHazardsSpeedBoostsAndToadPillars(Tile[][][] tileArray)
	{
		var trial = state.getCurrentTrial();
		if (trial == null)
		{
			return;
		}
		var fetidPoolIds = JubblyJiveConfig.FETID_POOL_IDS;

		var knownRockTiles = new HashSet<>(state.getKnownRockLocations());

		var knownBoosts = new HashSet<>(state.getSpeedBoosts());
		var knownBoostTiles = new HashMap<>(state.getKnownSpeedBoostLocations());

		var knownFetidPoolTiles = new HashSet<>(state.getKnownFetidPoolLocations());

		var knownToadPillarTiles = new HashSet<>(state.getKnownToadPillarLocations());

		for (var plane : tileArray)
		{
			if (plane == null) continue;

			for (var column : plane)
			{
				if (column == null) continue;

				for (var tile : column)
				{
					if (tile == null) continue;

					WorldPoint tileWp = tile.getWorldLocation();
					for (var obj : tile.getGameObjects())
					{
						if (obj == null) continue;

						int id = obj.getId();
						var objTile = obj.getWorldLocation();
						if (!objTile.equals(tileWp))
						{
							// Don't want to re-process multi-tile objects
							continue;
						}

						if (!knownRockTiles.contains(tileWp) && ROCK_IDS.contains(id))
						{
							knownRockTiles.addAll(ObjectTracker.getObjectTiles(client, obj));

							continue;
						}

						if (!knownBoostTiles.containsKey(tileWp) && SPEED_BOOST_IDS.contains(id))
						{
							knownBoosts.add(obj);

							// getObjectTiles is 5x5, but we want 3x3 to encourage getting closer
							var speedTilesWithOneTolerance = ObjectTracker.getTilesWithTolerance(objTile, 1);
							knownBoostTiles.put(objTile, speedTilesWithOneTolerance);
							continue;
						}

						if (!knownFetidPoolTiles.contains(tileWp) && fetidPoolIds.contains(id))
						{
							knownFetidPoolTiles.addAll(ObjectTracker.getObjectTiles(client, obj));

							continue;
						}

						var matchingToadPillarByParentId =
								Arrays.stream(JubblyJiveConfig.TOAD_PILLARS)
										.filter(v -> v.getClickboxParentObjectId() == id)
										.findFirst()
										.orElse(null);

						if (matchingToadPillarByParentId != null)
						{
							if (!knownToadPillarTiles.contains(tileWp))
							{
								knownToadPillarTiles.addAll(ObjectTracker.getObjectTiles(client, obj));
							}

							onToadPillarTick(obj, matchingToadPillarByParentId);
							continue;
						}
					}
				}
			}
		}

		state.updateKnownRockLocations(knownRockTiles);
		state.updateSpeedBoosts(knownBoosts);
		state.updateKnownSpeedBoostLocations(knownBoostTiles);
		state.updateKnownFetidPoolLocations(knownFetidPoolTiles);
		state.updateKnownToadPillarLocations(knownToadPillarTiles);
	}

	public void onToadPillarTick(GameObject newToadPillarObj, JubblyJiveToadPillar toadPillar)
	{
		var objectComposition = client.getObjectDefinition(newToadPillarObj.getId());
		if (objectComposition == null)
			return;

		var isInteractedWith = false;

		var impostorIds = objectComposition.getImpostorIds();
		if (impostorIds != null)
		{
			var impostor = objectComposition.getImpostor();
			isInteractedWith = impostor.getId() == toadPillar.getClickboxNoopObjectId();
		}

		var previousIsInteractedWith = state.updateKnownToadPillar(newToadPillarObj.getWorldLocation(), isInteractedWith);

		if (previousIsInteractedWith == null) return; // first time
		if (previousIsInteractedWith == isInteractedWith) return; // no change
		if (previousIsInteractedWith && !isInteractedWith) return; // true -> false (reset)

		var route = state.getCurrentStaticRoute();
		if (route == null || route.isEmpty())
		{
			return;
		}

		var objectId = newToadPillarObj.getId();
		log.info("Detected change in pillar. Trying to find id {} in list of waypoints", objectId);

		for (int index = 0; index < route.size(); index++)
		{
			var waypoint = route.get(index);

			if (!(waypoint instanceof JubblyJiveToadPillarWaypoint))
			{
				continue;
			}

			var pillarWaypoint = (JubblyJiveToadPillarWaypoint) waypoint;

			if (!pillarWaypoint.getPillar().matchesAnyObjectId(objectId))
			{
				continue;
			}

			if (state.isWaypointCompleted(index))
			{
				log.info("Found match but it was already completed, seeing if there's more...");
				continue;
			}

			log.info("Found match! Completing it in our waypoint list.");
			state.markWaypointCompleted(index);

			var waypointLap = waypoint.getLap();
			if (state.getCurrentLap() < waypointLap)
			{
				log.info("Advanced to lap {}", waypointLap);
				state.setCurrentLap(waypointLap);
			}

			return;
		}

		log.warn("Couldn't find a match to update! That seems wrong - how did we update the impostor without it being in the list?");
	}

	/**
	 * Checks shipment waypoints for collection and marks them completed.
	 * Detection: base shipment object exists but impostor ID is missing = collected.
	 * Only checks waypoints within 7 tiles (impostor ID visible range).
	 *
	 * @return true if any shipments were collected this tick
	 */
	public boolean updateRouteWaypointShipmentTracking()
	{
		var route = state.getCurrentStaticRoute();
		if (!state.isInTrial() || route == null)
		{
			return false;
		}

		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return false;
		}

		Scene scene = worldView.getScene();
		WorldPoint boatLocation = state.getBoatLocation();
		if (scene == null || boatLocation == null)
		{
			return false;
		}

		boolean anyCollected = false;

		for (int i = 0; i < route.size(); i++)
		{
			RouteWaypoint waypoint = route.get(i);

			if (waypoint.getType() != RouteWaypoint.WaypointType.SHIPMENT)
			{
				continue;
			}

			if (state.isWaypointCompleted(i))
			{
				continue;
			}

			WorldPoint location = waypoint.getLocation();

			// Only check if within range (impostor ID only visible within 7 tiles)
			int dx = Math.abs(location.getX() - boatLocation.getX());
			int dy = Math.abs(location.getY() - boatLocation.getY());
			if (Math.max(dx, dy) > 7)
			{
				continue;
			}

			if (hasBaseShipmentButNoImpostor(scene, location))
			{
				state.markWaypointCompleted(i);
				anyCollected = true;
				log.info("Shipment collected at route waypoint index {}: {}", i, location);
			}
		}

		return anyCollected;
	}

	/**
	 * Checks if a base shipment object exists at a location BUT the impostor ID does not.
	 * This indicates the shipment has been collected (base remains, impostor disappears).
	 */
	private boolean hasBaseShipmentButNoImpostor(Scene scene, WorldPoint worldLocation)
	{
		var trial = state.getCurrentTrial();
		if (trial == null)
		{
			return false;
		}

		var shipmentIds = trial.getShipmentBaseIds();
		int shipmentImpostorId = trial.getShipmentImpostorId();

		int plane = worldLocation.getPlane();
		int sceneX = worldLocation.getX() - scene.getBaseX();
		int sceneY = worldLocation.getY() - scene.getBaseY();

		if (sceneX < 0 || sceneX >= 104 || sceneY < 0 || sceneY >= 104)
		{
			return false;
		}

		Tile[][][] tiles = scene.getTiles();
		if (tiles == null || tiles[plane] == null)
		{
			return false;
		}

		Tile tile = tiles[plane][sceneX][sceneY];
		if (tile == null)
		{
			return false;
		}

		boolean hasBaseShipment = false;
		boolean hasImpostor = false;

		for (GameObject gameObject : tile.getGameObjects())
		{
			if (gameObject == null)
			{
				continue;
			}

			int objectId = gameObject.getId();

			if (!shipmentIds.contains(objectId))
			{
				continue;
			}

			hasBaseShipment = true;

			var objectComposition = client.getObjectDefinition(objectId);
			if (objectComposition == null)
			{
				continue;
			}

			var impostorIds = objectComposition.getImpostorIds();
			if (impostorIds == null)
			{
				continue;
			}

			var impostor = objectComposition.getImpostor();
			if (impostor == null)
			{
				continue;
			}

			var impostorId = impostor.getId();
			if (impostorId == shipmentImpostorId)
			{
				hasImpostor = true;
			}
		}

		return hasBaseShipment && !hasImpostor;
	}

	/**
	 * Updates the boat location (player's boat WorldEntity)
	 * Falls back to player location if boat cannot be found
	 */
	public void updatePlayerBoatLocation()
	{
		if (!state.isInTrial())
		{
			state.setBoatLocation(null);
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			state.setBoatLocation(null);
			return;
		}

		try
		{
			WorldView playerWorldView = localPlayer.getWorldView();
			if (playerWorldView == null)
			{
				state.setBoatLocation(localPlayer.getWorldLocation());
				return;
			}

			int playerWorldViewId = playerWorldView.getId();

			WorldView topLevelWorldView = client.getTopLevelWorldView();
			if (topLevelWorldView == null)
			{
				state.setBoatLocation(localPlayer.getWorldLocation());
				return;
			}

			WorldEntity boatWorldEntity = topLevelWorldView.worldEntities().byIndex(playerWorldViewId);
			if (boatWorldEntity == null)
			{
				state.setBoatLocation(localPlayer.getWorldLocation());
				return;
			}

			var boatLocalLocation = boatWorldEntity.getLocalLocation();
			if (boatLocalLocation != null)
			{
				state.setBoatLocation(WorldPoint.fromLocalInstance(client, boatLocalLocation));
			}
			else
			{
				state.setBoatLocation(localPlayer.getWorldLocation());
			}
		}
		catch (Exception e)
		{
			state.setBoatLocation(localPlayer.getWorldLocation());
			log.debug("Error getting boat location: {}", e.getMessage());
		}
	}

	/**
	 * Updates the front boat tile position for pathfinding
	 * The front is calculated as 3 tiles ahead of the boat center in the direction of travel
	 */
	public void updateFrontBoatTile()
	{
		if (!state.isInTrial())
		{
			state.setFrontBoatTileEstimatedActual(null);
			state.setFrontBoatTileLocal(null);
			return;
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			log.warn("Local player is null when updating front boat tile");
			state.setFrontBoatTileEstimatedActual(null);
			state.setFrontBoatTileLocal(null);
			return;
		}

		try
		{
			WorldView playerWorldView = localPlayer.getWorldView();
			if (playerWorldView == null)
			{
				log.warn("Player WorldView is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			WorldView topLevelWorldView = client.getTopLevelWorldView();
			if (topLevelWorldView == null)
			{
				log.warn("Top-level WorldView is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			int playerWorldViewId = playerWorldView.getId();
			WorldEntity boatWorldEntity = topLevelWorldView.worldEntities().byIndex(playerWorldViewId);
			if (boatWorldEntity == null)
			{
				log.warn("Boat WorldEntity is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			WorldView boatWorldView = boatWorldEntity.getWorldView();
			if (boatWorldView == null)
			{
				log.warn("Boat WorldView is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			Scene boatScene = boatWorldView.getScene();
			if (boatScene == null)
			{
				log.warn("Boat Scene is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			Player boatPlayer = null;
			for (Player p : boatWorldView.players())
			{
				if (p != null && p.equals(localPlayer))
				{
					boatPlayer = p;
					break;
				}
			}

			if (boatPlayer == null)
			{
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			NPC boatNpc = null;
			for (NPC npc : boatWorldView.npcs())
			{
				if (npc == null || !BOAT_NPC_IDS.contains(npc.getId()))
					continue;
				boatNpc = npc;
				break;
			}

			if (boatNpc == null)
			{
				log.warn("Boat NPC is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			LocalPoint npcLocalPoint = boatNpc.getLocalLocation();
			LocalPoint boatPlayerLocalPoint = boatPlayer.getLocalLocation();

			if (npcLocalPoint == null || boatPlayerLocalPoint == null)
			{
				log.warn("NPC or Boat Player local point is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				state.setFrontBoatTileLocal(null);
				return;
			}

			// Calculate direction from player (back) to NPC (middle) in scene tiles
			int npcSceneX = npcLocalPoint.getSceneX();
			int npcSceneY = npcLocalPoint.getSceneY();
			int playerSceneX = boatPlayerLocalPoint.getSceneX();
			int playerSceneY = boatPlayerLocalPoint.getSceneY();

			int deltaX = npcSceneX - playerSceneX;
			int deltaY = npcSceneY - playerSceneY;

			// Front of boat: extend 3 tiles from NPC
			int frontSceneX = npcSceneX + (deltaX * 3);
			int frontSceneY = npcSceneY + (deltaY * 3);

			// Convert to LocalPoint in boat's coordinate system (for rendering)
			int baseX = boatScene.getBaseX();
			int baseY = boatScene.getBaseY();
			LocalPoint frontLocalPoint = LocalPoint.fromScene(baseX + frontSceneX, baseY + frontSceneY, boatScene);

			// Store the boat-relative LocalPoint (smooth sub-tile positioning, for visual rendering)
			state.setFrontBoatTileLocal(frontLocalPoint);

			// Transform from boat's coordinate system to main world (for tile-based pathfinding)
			LocalPoint frontMainWorldLocal = boatWorldEntity.transformToMainWorld(frontLocalPoint);
			if (frontMainWorldLocal == null)
			{
				log.warn("Front main world LocalPoint is null when updating front boat tile");
				state.setFrontBoatTileEstimatedActual(null);
				return;
			}

			// Convert to WorldPoint (for pathfinding A* algorithm)
			WorldPoint frontWorldPoint = WorldPoint.fromLocalInstance(client, frontMainWorldLocal);
			state.setFrontBoatTileEstimatedActual(frontWorldPoint);
		}
		catch (Exception e)
		{
			log.error("Error calculating front boat tile: {}", e.getMessage());
			state.setFrontBoatTileEstimatedActual(null);
			state.setFrontBoatTileLocal(null);
		}
	}

	public static List<WorldPoint> getObjectTiles(Client client, GameObject obj)
	{
		Point min = obj.getSceneMinLocation();
		Point max = obj.getSceneMaxLocation();

		if (min == null || max == null)
		{
			// Fallback: treat as 1x1 anchored on world location
			return Collections.singletonList(obj.getWorldLocation());
		}

		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return Collections.singletonList(obj.getWorldLocation());
		}

		Scene scene = worldView.getScene();
		if (scene == null)
		{
			return Collections.singletonList(obj.getWorldLocation());
		}

		int baseX = scene.getBaseX();
		int baseY = scene.getBaseY();
		int plane = obj.getPlane();

		int width = max.getX() - min.getX() + 1;
		int height = max.getY() - min.getY() + 1;

		List<WorldPoint> result = new ArrayList<>(width * height);
		for (int sx = min.getX(); sx <= max.getX(); sx++)
		{
			for (int sy = min.getY(); sy <= max.getY(); sy++)
			{
				int worldX = baseX + sx;
				int worldY = baseY + sy;
				result.add(new WorldPoint(worldX, worldY, plane));
			}
		}

		return result;
	}

	/**
	 * Computes all tiles within a given tolerance distance from target locations.
	 * Uses Chebyshev distance (max of dx, dy) for square areas.
	 *
	 * @param tolerance Distance in tiles (1 = 3x3 area, 2 = 5x5 area, etc.)
	 * @return Map from grabbable tile to its center point
	 */
	public static List<WorldPoint> getTilesWithTolerance(WorldPoint center, int tolerance)
	{
		List<WorldPoint> tiles = new ArrayList<>();
		int plane = center.getPlane();

		for (int dx = -tolerance; dx <= tolerance; dx++)
		{
			for (int dy = -tolerance; dy <= tolerance; dy++)
			{
				tiles.add(new WorldPoint(center.getX() + dx, center.getY() + dy, plane));
			}
		}

		return tiles;
	}

}
