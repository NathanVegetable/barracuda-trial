package com.barracudatrial;

import com.barracudatrial.game.*;
import com.barracudatrial.game.route.Difficulty;
import com.barracudatrial.game.route.RouteWaypoint;
import com.barracudatrial.game.route.TrialType;
import com.barracudatrial.ui.EditableWaypoint;
import com.barracudatrial.ui.RouteEditorPanel;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Menu;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Barracuda Trials Pathfinder",
	description = "Displays optimal paths and highlights for Sailing Barracuda Trials training",
	tags = {"sailing", "tempor", "tantrum", "jubbly", "jive", "gwenith", "glide", "rum", "toads", "supply"}
)
public class BarracudaTrialPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BarracudaTrialConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BarracudaTrialOverlay overlay;

	@Inject
	private ClientToolbar clientToolbar;

	@Getter
	private final State gameState = new State();

	@Getter
	private CachedConfig cachedConfig;

	private ObjectTracker objectTracker;
	private LocationManager locationManager;
	private ProgressTracker progressTracker;
	private PathPlanner pathPlanner;

	@Getter
	private RouteCapture routeCapture;

	// Route Editor UI
	private RouteEditorPanel routeEditorPanel;
	private NavigationButton navButton;
	private com.barracudatrial.ui.RouteEditorOverlay routeEditorOverlay;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Barracuda Trial plugin started!");
		overlayManager.add(overlay);

		cachedConfig = new CachedConfig(config);

		objectTracker = new ObjectTracker(client, gameState);
		locationManager = new LocationManager(client, gameState);
		progressTracker = new ProgressTracker(client, gameState);
		pathPlanner = new PathPlanner(client, gameState, cachedConfig);
		routeCapture = new RouteCapture(gameState);

		// Setup Route Editor Panel
		routeEditorPanel = new RouteEditorPanel();
		setupRouteEditorIntegration();
		routeEditorOverlay = new com.barracudatrial.ui.RouteEditorOverlay(client, routeEditorPanel);
		overlayManager.add(routeEditorOverlay);

		try {
			final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");
			navButton = NavigationButton.builder()
				.tooltip("Barracuda Route Editor")
				.icon(icon)
				.priority(5)
				.panel(routeEditorPanel)
				.build();
			clientToolbar.addNavigation(navButton);
		} catch (Exception e) {
			log.warn("Could not load route editor icon, creating simple panel", e);
			// Create icon if resource not found - simple colored square
			BufferedImage icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			navButton = NavigationButton.builder()
				.tooltip("Barracuda Route Editor")
				.icon(icon)
				.priority(5)
				.panel(routeEditorPanel)
				.build();
			clientToolbar.addNavigation(navButton);
		}
		log.info("Route Editor panel registered - right-click tiles in-game to add waypoints");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Barracuda Trial plugin stopped!");
		overlayManager.remove(overlay);
		gameState.resetAllTemporaryState();
		pathPlanner.reset();
		gameState.clearPersistentStorage();

		// Remove Route Editor Panel and Overlay
		if (navButton != null) {
			clientToolbar.removeNavigation(navButton);
		}
		if (routeEditorOverlay != null) {
			overlayManager.remove(routeEditorOverlay);
		}
		routeEditorPanel = null;
		routeEditorOverlay = null;
		navButton = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		boolean trialAreaStateChanged = progressTracker.checkIfPlayerIsInTrialArea();
		if (trialAreaStateChanged && !gameState.isInTrialArea())
		{
			pathPlanner.reset();
		}
		if (!gameState.isInTrialArea())
		{
			return;
		}

		var trial = gameState.getCurrentTrial();
		if (trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM
			&& (cachedConfig.isShowOptimalPath() || cachedConfig.isHighlightClouds()))
		{
			objectTracker.updateLightningCloudTracking();
		}

		if (trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM
			&& (cachedConfig.isShowOptimalPath() || cachedConfig.isHighlightObjectives()))
		{
			locationManager.updateTemporRumLocations();
		}

		if (cachedConfig.isShowOptimalPath()
			|| cachedConfig.isHighlightSpeedBoosts()
			|| cachedConfig.isHighlightObjectives())
		{
			objectTracker.updateHazardsSpeedBoostsAndToadPillars();
		}

		progressTracker.updateTrialProgressFromWidgets();
		
		if (cachedConfig.isShowOptimalPath())
		{
			objectTracker.updatePlayerBoatLocation();

			objectTracker.updateFrontBoatTile();

			boolean shipmentsCollected = objectTracker.updateRouteWaypointShipmentTracking();
			if (shipmentsCollected)
			{
				if (routeEditorPanel != null)
				{
					SwingUtilities.invokeLater(() -> routeEditorPanel.refreshDisplay());
				}
				pathPlanner.recalculateOptimalPathFromCurrentState("shipment collected");
			}

			checkPortalExitProximity();
		}

		if (cachedConfig.isShowOptimalPath())
		{
			objectTracker.updateLostSuppliesTracking();
		}

		if (cachedConfig.isShowOptimalPath())
		{
			// Recalculate path periodically to account for moving clouds
			int ticksSinceLastPathRecalculation = gameState.getTicksSinceLastPathRecalc() + 1;
			gameState.setTicksSinceLastPathRecalc(ticksSinceLastPathRecalculation);

			if (ticksSinceLastPathRecalculation >= State.PATH_RECALC_INTERVAL)
			{
				gameState.setTicksSinceLastPathRecalc(0);
				pathPlanner.recalculateOptimalPathFromCurrentState("periodic (game tick)");
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (!gameState.isInTrialArea())
		{
			return;
		}

		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String chatMessage = event.getMessage();

		if (chatMessage.contains("You collect the rum"))
		{
			log.debug("Rum collected! Message: {}", chatMessage);
			gameState.setHasThrowableObjective(true);

			var route = gameState.getCurrentStaticRoute();

			if (route != null)
			{
				for (int i = 0, n = route.size(); i < n; i++)
				{
					var waypoint = route.get(i);

					if (waypoint.getType() == RouteWaypoint.WaypointType.RUM_PICKUP
						&& !gameState.isWaypointCompleted(i))
					{
						gameState.markWaypointCompleted(i);
						log.info("Marked RUM_PICKUP waypoint as completed at index {}: {}", i, waypoint.getLocation());
						if (routeEditorPanel != null)
						{
							SwingUtilities.invokeLater(() -> routeEditorPanel.refreshDisplay());
						}
						break;
					}
				}
			}

			pathPlanner.recalculateOptimalPathFromCurrentState("chat: rum collected");
		}
		else if (chatMessage.contains("You deliver the rum"))
		{
			log.debug("Rum delivered! Message: {}", chatMessage);
			gameState.setHasThrowableObjective(false);

			var route = gameState.getCurrentStaticRoute();

			if (route != null)
			{
				for (int i = 0; i < route.size(); i++)
				{
					RouteWaypoint waypoint = route.get(i);
					if (waypoint.getType() == RouteWaypoint.WaypointType.RUM_DROPOFF
						&& !gameState.isWaypointCompleted(i))
					{
						gameState.markWaypointCompleted(i);
						log.info("Marked RUM_DROPOFF waypoint as completed at index {}: {}", i, waypoint.getLocation());
						if (routeEditorPanel != null)
						{
							SwingUtilities.invokeLater(() -> routeEditorPanel.refreshDisplay());
						}
						break;
					}
				}
			}

			var currentDifficulty = State.getCurrentDifficulty(client);
			var lapsRequired = 
				currentDifficulty == Difficulty.SWORDFISH
					? 1
					: (currentDifficulty == Difficulty.SHARK
						? 2
						: 3);

			var nextLapNumber = gameState.getCurrentLap() + 1;
			var isCompletingFinalLap = lapsRequired == nextLapNumber;

			if (isCompletingFinalLap)
			{
				// Reset will be handled by game, no need to reset here
				log.info("Completed all {} laps!", lapsRequired);
			}
			else
			{
				gameState.setCurrentLap(nextLapNumber);
				log.info("Advanced to lap {}/{}", nextLapNumber, lapsRequired);
			}

			pathPlanner.recalculateOptimalPathFromCurrentState("chat: rum delivered");
		}
		else if (chatMessage.contains("balloon toads. Time to lure"))
		{
			log.debug("Toads collected! Message: {}", chatMessage);

			gameState.setHasThrowableObjective(true);

			var route = gameState.getCurrentStaticRoute();

			if (route != null)
			{
				for (int i = 0, n = route.size(); i < n; i++)
				{
					var waypoint = route.get(i);

					if (waypoint.getType() == RouteWaypoint.WaypointType.TOAD_PICKUP
						&& !gameState.isWaypointCompleted(i))
					{
						gameState.markWaypointCompleted(i);
						log.info("Marked TOAD_PICKUP waypoint as completed at index {}: {}", i, waypoint.getLocation());
						if (routeEditorPanel != null)
						{
							SwingUtilities.invokeLater(() -> routeEditorPanel.refreshDisplay());
						}
						break;
					}
				}
			}

			pathPlanner.recalculateOptimalPathFromCurrentState("chat: toads collected");
		}
		else if (chatMessage.contains("through the portal"))
		{
			log.debug("Portal traversed! Message: {}", chatMessage);

			var route = gameState.getCurrentStaticRoute();

			if (route != null)
			{
				for (int i = 0, n = route.size(); i < n; i++)
				{
					var waypoint = route.get(i);

					if (waypoint.getType() == RouteWaypoint.WaypointType.PORTAL_ENTER
						&& !gameState.isWaypointCompleted(i))
					{
						gameState.markWaypointCompleted(i);
						log.info("Marked PORTAL_ENTER waypoint as completed at index {}: {}", i, waypoint.getLocation());

						if (waypoint.getLap() > gameState.getCurrentLap())
						{
							gameState.setCurrentLap(waypoint.getLap());
							log.info("Advanced to lap {} (portal enter)", waypoint.getLap());
						}

						if (routeEditorPanel != null)
						{
							SwingUtilities.invokeLater(() -> routeEditorPanel.refreshDisplay());
						}

						gameState.getOptimalPath().clear();
						gameState.getCurrentSegmentPath().clear();
						gameState.getNextSegmentPath().clear();
						log.debug("Cleared path during portal transition");

						break;
					}
				}
			}
		}
	}
	
	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{
		// TODO: remove once fade out is fixed in Sailing helper
		var FADE_OUT_TRANSITION_SCRIPT_ID = 948;

        if (event.getScriptId() == FADE_OUT_TRANSITION_SCRIPT_ID)
        {
            event.getScriptEvent().getArguments()[4] = 255;
            event.getScriptEvent().getArguments()[5] = 0;
        }
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!gameState.isInTrialArea())
		{
			return;
		}
		
		if (!event.getGroup().equals("barracudatrial"))
		{
			return;
		}

		cachedConfig.updateCache();

		if (event.getKey().equals("routeOptimization") && gameState.isInTrialArea())
		{
			pathPlanner.recalculateOptimalPathFromCurrentState("config: route optimization changed");
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!cachedConfig.isDebugMode() || !gameState.isInTrialArea())
		{
			return;
		}

		if (!event.getMenuOption().equals("Examine"))
		{
			return;
		}

		int sceneX = event.getParam0();
		int sceneY = event.getParam1();
		if (sceneX < 0 || sceneY < 0)
		{
			return;
		}

		int plane = client.getPlane();
		WorldPoint worldPoint = WorldPoint.fromScene(client, sceneX, sceneY, plane);

		Scene scene = client.getScene();
		int sceneBaseX = scene != null ? scene.getBaseX() : -1;
		int sceneBaseY = scene != null ? scene.getBaseY() : -1;

		WorldPoint boatWorldLocation = getBoatWorldLocationForSceneTile(sceneX, sceneY, plane);

		int objectId = event.getId();

		String impostorInfo = "none";
		try
		{
			ObjectComposition objectComposition = client.getObjectDefinition(objectId);
			if (objectComposition != null)
			{
				impostorInfo = "none (with object comp)";

				var impostorIds = objectComposition.getImpostorIds();
				if (impostorIds == null)
				{
					impostorInfo = "none (null ids)";
				}
				else
				{
					ObjectComposition activeImpostor = objectComposition.getImpostor();
					if (activeImpostor != null) {
						impostorInfo = String.valueOf(activeImpostor.getId());
					}
				}
			}
		}
		catch (Exception e)
		{
			impostorInfo += "[error: " + e + "]";
		}

		log.info("[EXAMINE] ObjectID: {}, ImpostorID: {}, SceneXY: ({}, {}), SceneBase: ({}, {}), Plane: {}, WorldPoint: {}, BoatWorldLoc: {}",
			objectId, impostorInfo, sceneX, sceneY, sceneBaseX, sceneBaseY, plane, worldPoint,
			boatWorldLocation != null ? boatWorldLocation : "null");

		if (scene != null)
		{
			var tiles = scene.getTiles();
			var tile = tiles[plane][sceneX][sceneY];

			for (GameObject gameObject : tile.getGameObjects()) {
				if (gameObject == null)
					continue;

				var id = gameObject.getId();
				if (id == objectId)
					continue;

				log.info("[EXAMINE] Found another Object on this tile: {}", id);
			}

			if (objectId == State.RUM_RETURN_BASE_OBJECT_ID || objectId == State.RUM_RETURN_IMPOSTOR_ID) {
				WorldPoint rumLocation = boatWorldLocation != null ? boatWorldLocation : worldPoint;
				routeCapture.onExamineRumDropoff(rumLocation, sceneX, sceneY, sceneBaseX, sceneBaseY, objectId, impostorInfo);
			}
			// else if (JubblyJiveConfig.TOAD_PILLAR_IDS.contains(objectId))
			// {
			// 	routeCapture.onExamineToadPillar(worldPoint, objectId);
			// }
		}
	}

	public boolean isPointInExclusionZone(WorldPoint point)
	{
		return locationManager.isPointInsideExclusionZone(point);
	}

	private int getImpostorId(int objectId)
	{
		try
		{
			ObjectComposition objectComposition = client.getObjectDefinition(objectId);
			if (objectComposition == null)
			{
				return -1;
			}

			int[] impostorIds = objectComposition.getImpostorIds();
			if (impostorIds == null)
			{
				return -1;
			}

			ObjectComposition activeImpostor = objectComposition.getImpostor();
			if (activeImpostor != null)
			{
				return activeImpostor.getId();
			}

			return -1;
		}
		catch (Exception e)
		{
			return -1;
		}
	}

	@Subscribe
	public void onMenuEntryAdded(final MenuEntryAdded event)
	{
		// Only add route editor menu when panel exists
		if (routeEditorPanel == null) {
			return;
		}

		final MenuEntry menuEntry = event.getMenuEntry();
		if (!Objects.equals(menuEntry.getOption(), "Walk here") && !Objects.equals(menuEntry.getOption(), "Set heading")) {
			return;
		}

		final Menu menu = client.getMenu();
		Point mousePos = client.getMouseCanvasPosition();
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();

		int z = client.getPlane();
		WorldPoint wp = null;

		for (int x = 0; x < Constants.SCENE_SIZE; ++x)
		{
			for (int y = 0; y < Constants.SCENE_SIZE; ++y)
			{
				Tile tile = tiles[z][x][y];
				if (tile == null) {
					continue;
				}

				Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation());
				if (poly != null && poly.contains(mousePos.getX(), mousePos.getY()))
				{
					wp = tile.getWorldLocation();
					break;
				}
			}
		}

		if (wp != null)
		{
			WorldPoint finalWp = wp;

			// Add "Add Wind Catcher" menu entry
			menu.createMenuEntry(-1)
				.setOption("Add Wind Catcher")
				.setTarget("")
				.setType(MenuAction.RUNELITE)
				.onClick(e -> {
					log.info("Adding wind catcher waypoint at {}", finalWp);
					int defaultLap = routeEditorPanel.getDefaultLapForNewWaypoint();
					EditableWaypoint waypoint = new EditableWaypoint(finalWp, RouteWaypoint.WaypointType.USE_WIND_CATCHER, defaultLap, null);
					SwingUtilities.invokeLater(() -> routeEditorPanel.addWaypoint(waypoint));
				});

			// Add "Add Pathfinding Hint" menu entry
			menu.createMenuEntry(-1)
				.setOption("Add Pathfinding Hint")
				.setTarget("")
				.setType(MenuAction.RUNELITE)
				.onClick(e -> {
					log.info("Adding pathfinding hint at {}", finalWp);
					int defaultLap = routeEditorPanel.getDefaultLapForNewWaypoint();
					EditableWaypoint waypoint = new EditableWaypoint(finalWp, RouteWaypoint.WaypointType.PATHFINDING_HINT, defaultLap, null);
					SwingUtilities.invokeLater(() -> routeEditorPanel.addWaypoint(waypoint));
				});

			// Add "Add Waypoint" menu entry (with smart detection)
			menu.createMenuEntry(-1)
				.setOption("Add Waypoint")
				.setTarget("")
				.setType(MenuAction.RUNELITE)
				.onClick(e -> {
					log.info("Adding waypoint at {}", finalWp);

					// Smart detection: check for objects on this tile
					RouteEditorPanel.DetectionResult detected = new RouteEditorPanel.DetectionResult(RouteWaypoint.WaypointType.SHIPMENT);
					int defaultLap = routeEditorPanel.getDefaultLapForNewWaypoint();

					Scene scene2 = client.getScene();
					if (scene2 != null)
					{
						Tile[][][] tiles2 = scene2.getTiles();
						int sceneX = finalWp.getX() - scene2.getBaseX();
						int sceneY = finalWp.getY() - scene2.getBaseY();
						int plane = finalWp.getPlane();

						if (sceneX >= 0 && sceneX < 104 && sceneY >= 0 && sceneY < 104)
						{
							Tile tile = tiles2[plane][sceneX][sceneY];
							if (tile != null)
							{
								for (GameObject obj : tile.getGameObjects())
								{
									if (obj != null)
									{
										int objectId = obj.getId();
										int impostorId = getImpostorId(objectId);
										detected = RouteEditorPanel.detectWaypointType(objectId, impostorId);
										log.info("Detected object ID {} (impostor: {}) -> type: {} note: {}",
											objectId, impostorId, detected.type, detected.note);
										break; // Use first object found
									}
								}
							}
						}
					}

					RouteEditorPanel.DetectionResult finalDetected = detected;
					EditableWaypoint waypoint = new EditableWaypoint(finalWp, finalDetected.type, defaultLap, finalDetected.note);
					SwingUtilities.invokeLater(() -> routeEditorPanel.addWaypoint(waypoint));
				});
		}
	}

	/**
	 * Sets up the integration between the route editor and the pathfinding system
	 */
	private void setupRouteEditorIntegration()
	{
		// Pass game state reference to the panel for completion tracking
		routeEditorPanel.setGameState(gameState);

		// Set up the callback for the "Load Static Route" button
		routeEditorPanel.setLoadRouteCallback(this::loadCurrentRouteIntoEditor);

		// Set up the listener for when the route is edited
		routeEditorPanel.setRouteChangeListener(newRoute -> {
			log.info("Route editor changed - updating static route with {} waypoints", newRoute.size());

			// Update the game state with the new route
			gameState.setCurrentStaticRoute(newRoute);

			// Note: Completion indices are managed by the RouteEditorPanel when
			// waypoints are added/deleted/reordered. We don't reset them here.

			// Force a path recalculation through the path planner
			if (gameState.isInTrialArea() && cachedConfig.isShowOptimalPath())
			{
				pathPlanner.recalculateOptimalPathFromCurrentState("route editor: route modified");
			}
		});
	}

	/**
	 * Loads the current static route into the route editor
	 */
	private void loadCurrentRouteIntoEditor()
	{
		var route = gameState.getCurrentStaticRoute();
		if (route == null || route.isEmpty())
		{
			log.warn("No static route loaded in game state - cannot load into editor");
			javax.swing.JOptionPane.showMessageDialog(
				routeEditorPanel,
				"No static route is currently loaded!\nEnter a trial area first.",
				"No Route Loaded",
				javax.swing.JOptionPane.WARNING_MESSAGE
			);
			return;
		}

		routeEditorPanel.loadRoute(route);
		log.info("Loaded current static route into editor ({} waypoints)", route.size());
	}

	/**
	 * Gets the boat/entity world location for an object at the given scene coordinates.
	 * Objects on boats have scene-local coordinates that shift as the boat moves.
	 */
	private WorldPoint getBoatWorldLocationForSceneTile(int sceneX, int sceneY, int plane)
	{
		try
		{
			Scene scene = client.getScene();
			if (scene == null || sceneX < 0 || sceneY < 0 || sceneX >= 104 || sceneY >= 104)
			{
				return null;
			}

			Tile[][][] tiles = scene.getTiles();
			if (tiles == null || tiles[plane] == null)
			{
				return null;
			}

			Tile tile = tiles[plane][sceneX][sceneY];
			if (tile == null)
			{
				return null;
			}

			GameObject rumObject = null;
			for (GameObject gameObject : tile.getGameObjects())
			{
				if (gameObject == null)
				{
					continue;
				}

				int objectId = gameObject.getId();
				if (objectId == State.RUM_RETURN_BASE_OBJECT_ID || objectId == State.RUM_RETURN_IMPOSTOR_ID ||
					objectId == State.RUM_PICKUP_BASE_OBJECT_ID || objectId == State.RUM_PICKUP_IMPOSTOR_ID)
				{
					rumObject = gameObject;
					break;
				}
			}

			if (rumObject == null)
			{
				return null;
			}

			WorldPoint rumWorldLocation = rumObject.getWorldLocation();

			WorldView topLevelWorldView = client.getTopLevelWorldView();
			if (topLevelWorldView == null)
			{
				return null;
			}

			for (WorldEntity worldEntity : topLevelWorldView.worldEntities())
			{
				if (worldEntity == null)
				{
					continue;
				}

				WorldView entityWorldView = worldEntity.getWorldView();
				if (entityWorldView == null)
				{
					continue;
				}

				Scene entityScene = entityWorldView.getScene();
				if (entityScene == null)
				{
					continue;
				}

				int entitySceneX = rumWorldLocation.getX() - entityScene.getBaseX();
				int entitySceneY = rumWorldLocation.getY() - entityScene.getBaseY();

				if (entitySceneX >= 0 && entitySceneX < 104 && entitySceneY >= 0 && entitySceneY < 104)
				{
					Tile[][][] entityTiles = entityScene.getTiles();
					if (entityTiles == null || plane >= entityTiles.length || entityTiles[plane] == null)
					{
						continue;
					}

					Tile entityTile = entityTiles[plane][entitySceneX][entitySceneY];
					if (entityTile != null)
					{
						boolean foundRumOnThisTile = false;
						for (GameObject gameObject : entityTile.getGameObjects())
						{
							if (gameObject == null)
							{
								continue;
							}

							int objectId = gameObject.getId();
							if (objectId == State.RUM_RETURN_BASE_OBJECT_ID || objectId == State.RUM_RETURN_IMPOSTOR_ID ||
								objectId == State.RUM_PICKUP_BASE_OBJECT_ID || objectId == State.RUM_PICKUP_IMPOSTOR_ID)
							{
								foundRumOnThisTile = true;
								break;
							}
						}

						if (foundRumOnThisTile)
						{
							var boatLocalLocation = worldEntity.getLocalLocation();
							if (boatLocalLocation != null)
							{
								return WorldPoint.fromLocalInstance(client, boatLocalLocation);
							}
						}
					}
				}
			}

			return null;
		}
		catch (Exception e)
		{
			log.debug("Error getting boat world location: {}", e.getMessage());
			return null;
		}
	}

	private void checkPortalExitProximity()
	{
		var route = gameState.getCurrentStaticRoute();
		if (route == null || route.isEmpty())
		{
			return;
		}

		var boatLocation = gameState.getBoatLocation();
		if (boatLocation == null)
		{
			return;
		}

		for (int i = 0; i < route.size() - 1; i++)
		{
			var waypoint = route.get(i);
			var nextWaypoint = route.get(i + 1);

			if (waypoint.getType() == RouteWaypoint.WaypointType.PORTAL_ENTER
				&& gameState.isWaypointCompleted(i)
				&& nextWaypoint.getType() == RouteWaypoint.WaypointType.PORTAL_EXIT
				&& !gameState.isWaypointCompleted(i + 1))
			{
				int distance = boatLocation.distanceTo(nextWaypoint.getLocation());
				if (distance <= 10)
				{
					gameState.markWaypointCompleted(i + 1);
					log.info("Marked PORTAL_EXIT waypoint as completed at index {} (distance: {}): {}", i + 1, distance, nextWaypoint.getLocation());

					if (nextWaypoint.getLap() > gameState.getCurrentLap())
					{
						gameState.setCurrentLap(nextWaypoint.getLap());
						log.info("Advanced to lap {} (portal exit)", nextWaypoint.getLap());
					}

					if (routeEditorPanel != null)
					{
						SwingUtilities.invokeLater(() -> routeEditorPanel.refreshDisplay());
					}

					pathPlanner.recalculateOptimalPathFromCurrentState("portal exit proximity");
					return;
				}
			}
		}
	}

	@Provides
	BarracudaTrialConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BarracudaTrialConfig.class);
	}
}
