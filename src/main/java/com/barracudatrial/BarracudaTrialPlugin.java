package com.barracudatrial;

import com.barracudatrial.game.*;
import com.barracudatrial.game.route.Difficulty;
import com.barracudatrial.game.route.RouteWaypoint;
import com.barracudatrial.game.route.RouteWaypoint.WaypointType;
import com.barracudatrial.game.route.TrialType;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@SuppressWarnings("unused")
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
	private ClientThread clientThread;

	@Getter
	private final State gameState = new State();

	@Getter
	private CachedConfig cachedConfig;

	private ObjectTracker objectTracker;
	private LocationManager locationManager;
	private ProgressTracker progressTracker;
	private PathPlanner pathPlanner;

	@Override
	@SuppressWarnings("RedundantThrows")
	protected void startUp() throws Exception
	{
		log.info("Barracuda Trial plugin started!");
		overlayManager.add(overlay);

		cachedConfig = new CachedConfig(config);

		objectTracker = new ObjectTracker(client, gameState);
		locationManager = new LocationManager(client, gameState);
		progressTracker = new ProgressTracker(client, gameState);
		pathPlanner = new PathPlanner(client, gameState, cachedConfig, clientThread);
	}

	@Override
	@SuppressWarnings("RedundantThrows")
	protected void shutDown() throws Exception
	{
		log.info("Barracuda Trial plugin stopped!");
		overlayManager.remove(overlay);
		gameState.resetAllTemporaryState();
		pathPlanner.shutdown();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		boolean trialAreaStateChanged = progressTracker.checkIfPlayerIsInTrial();
		if (trialAreaStateChanged && !gameState.isInTrial())
		{
			pathPlanner.reset();
		}
		if (!gameState.isInTrial())
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

		if (cachedConfig.isShowOptimalPath())
		{
			objectTracker.updatePlayerBoatLocation();

			objectTracker.updateFrontBoatTile();

			boolean shipmentsCollected = objectTracker.updateRouteWaypointShipmentTracking();
			if (shipmentsCollected)
			{
				pathPlanner.recalculateOptimalPathFromCurrentState("shipment collected");
			}

			checkPortalExitProximity();
		}

		if (cachedConfig.isShowOptimalPath())
		{
			int ticksSinceLastPathRecalculation = gameState.getTicksSinceLastPathRecalc() + 1;
			gameState.setTicksSinceLastPathRecalc(ticksSinceLastPathRecalculation);

			int recalcInterval = cachedConfig.getRouteOptimization().getPathRecalcIntervalTicks();
			if (ticksSinceLastPathRecalculation >= recalcInterval)
			{
				gameState.setTicksSinceLastPathRecalc(0);
				pathPlanner.recalculateOptimalPathFromCurrentState("periodic (game tick)");
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (!gameState.isInTrial())
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
						var lap = waypoint.getLap();
						gameState.setCurrentLap(lap + 1);
						log.info("Marked RUM_DROPOFF waypoint as completed at index {}: {}", i, waypoint.getLocation());
						pathPlanner.recalculateOptimalPathFromCurrentState("chat: rum delivered");
						break;
					}
				}
			}
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

						pathPlanner.recalculateOptimalPathFromCurrentState("chat: portal entered");

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
		if (!event.getGroup().equals("barracudatrial"))
		{
			return;
		}

		cachedConfig.updateCache();

		if (event.getKey().equals("routeOptimization") && gameState.isInTrial())
		{
			pathPlanner.recalculateOptimalPathFromCurrentState("config: route optimization changed");
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

			if (waypoint.getType() == WaypointType.PORTAL_ENTER
				&& gameState.isWaypointCompleted(i)
				&& nextWaypoint.getType() == WaypointType.PORTAL_EXIT
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
