package com.barracudatrial.game;

import com.barracudatrial.game.route.Difficulty;
import com.barracudatrial.game.route.RouteWaypoint;
import com.barracudatrial.game.route.TrialConfig;
import com.barracudatrial.game.route.TrialType;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;

import java.util.*;

/**
 * Holds all game state for Barracuda Trial
 */
@Getter
public class State
{
	@Setter
	private TrialConfig currentTrial = null;

	public static final int CLOUD_ANIM_HARMLESS = -1;
	public static final int CLOUD_ANIM_HARMLESS_ALT = 8879;

	private static final int FETID_POOL_IMMUNITY_TICKS = 7;
	private static final int TILES_PER_BOOSTED_TICK = 2;

	@Setter
	private boolean inTrial = false;

	private final Set<NPC> lightningClouds = new HashSet<>();

	private final Set<NPC> dangerousClouds = new HashSet<>();

	private final Set<GameObject> speedBoosts = new HashSet<>();

	@Setter
	private WorldPoint rumPickupLocation = null;

	@Setter
	private WorldPoint rumReturnLocation = null;

	@Setter
	private int rumsCollected = 0;

	@Setter
	private int lostSuppliesCollected = 0;

	@Setter
	private int lostSuppliesTotal = 0;

	@Setter
	private boolean hasThrowableObjective = false;

	@Setter
	private int lastKnownDifficulty = 0;

	@Setter
	private WorldPoint boatLocation = null;

	@Setter
	private WorldPoint frontBoatTileEstimatedActual = null;

	@Setter
	private LocalPoint frontBoatTileLocal = null;

	@Setter
	private int currentLap = 1;

	@Setter
	private List<WorldPoint> path = new ArrayList<>();

	@Setter
	private String lastPathRecalcCaller = "none";

	private final Set<WorldPoint> knownRockLocations = new HashSet<>();

	private final Map<WorldPoint, List<WorldPoint>> knownSpeedBoostLocations = new HashMap<>();

	private final Set<WorldPoint> knownFetidPoolLocations = new HashSet<>();

	private final Set<WorldPoint> knownToadPillarLocations = new HashSet<>();

	private final Set<WorldPoint> knownLandTiles = new HashSet<>();

	private TrialType learnedTerrainTrialType = null;

	// True if interacted with
	private final Map<WorldPoint, Boolean> knownToadPillars = new HashMap<>();

	@Setter
	private int ticksSinceLastPathRecalc = 0;

	private int fetidPoolImmunityTicksRemaining = 0;

	@Setter
	private int exclusionZoneMinX = 0;

	@Setter
	private int exclusionZoneMaxX = 0;

	@Setter
	private int exclusionZoneMinY = 0;

	@Setter
	private int exclusionZoneMaxY = 0;

	@Setter
	private List<RouteWaypoint> currentStaticRoute = null;

	private final Set<Integer> completedWaypointIndices = new HashSet<>();

	public void startFetidPoolImmunity()
	{
		fetidPoolImmunityTicksRemaining = FETID_POOL_IMMUNITY_TICKS;
	}

	public void decayFetidPoolImmunity()
	{
		if (fetidPoolImmunityTicksRemaining > 0)
		{
			fetidPoolImmunityTicksRemaining--;
		}
	}

	public int getFetidPoolImmuneTileRange()
	{
		return fetidPoolImmunityTicksRemaining * TILES_PER_BOOSTED_TICK;
	}

	/**
	 * Clears all temporary state (called when leaving trial area)
	 */
	public void resetAllTemporaryState()
	{
		currentTrial = null;
		inTrial = false;
		lightningClouds.clear();
		dangerousClouds.clear();
		knownToadPillars.clear();
		rumPickupLocation = null;
		rumReturnLocation = null;
		rumsCollected = 0;
		lostSuppliesCollected = 0;
		lostSuppliesTotal = 0;
		hasThrowableObjective = false;
		boatLocation = null;
		currentLap = 1;
		path = new ArrayList<>();
		ticksSinceLastPathRecalc = 0;
		fetidPoolImmunityTicksRemaining = 0;
		exclusionZoneMinX = 0;
		exclusionZoneMaxX = 0;
		exclusionZoneMinY = 0;
		exclusionZoneMaxY = 0;
		currentStaticRoute = null;
		completedWaypointIndices.clear();
	}

	public void discardLearnedTerrainIfTrialTypeChanged(TrialType trialType)
	{
		if (learnedTerrainTrialType == trialType)
		{
			return;
		}

		clearLearnedTerrain();
		learnedTerrainTrialType = trialType;
	}

	public void clearLearnedTerrain()
	{
		knownLandTiles.clear();
		knownRockLocations.clear();
		knownSpeedBoostLocations.clear();
		knownFetidPoolLocations.clear();
		knownToadPillarLocations.clear();
		learnedTerrainTrialType = null;
	}

	public void clearLightningClouds()
	{
		lightningClouds.clear();
	}

	public void addLightningCloud(NPC npc)
	{
		lightningClouds.add(npc);
	}

	public Set<NPC> getLightningClouds()
	{
		return Collections.unmodifiableSet(lightningClouds);
	}

	public void clearDangerousClouds()
	{
		dangerousClouds.clear();
	}

	public void addDangerousCloud(NPC npc)
	{
		dangerousClouds.add(npc);
	}

	public Set<NPC> getDangerousClouds()
	{
		return Collections.unmodifiableSet(dangerousClouds);
	}

	public Set<GameObject> getSpeedBoosts()
	{
		return Collections.unmodifiableSet(speedBoosts);
	}

	public Set<WorldPoint> getKnownRockLocations()
	{
		return Collections.unmodifiableSet(knownRockLocations);
	}

	public Map<WorldPoint, List<WorldPoint>> getKnownSpeedBoostLocations()
	{
		return Collections.unmodifiableMap(knownSpeedBoostLocations);
	}

	public Set<WorldPoint> getKnownFetidPoolLocations()
	{
		return Collections.unmodifiableSet(knownFetidPoolLocations);
	}

	public Set<WorldPoint> getKnownToadPillarLocations()
	{
		return Collections.unmodifiableSet(knownToadPillarLocations);
	}

	public Map<WorldPoint, Boolean> getKnownToadPillars()
	{
		return Collections.unmodifiableMap(knownToadPillars);
	}

	public Boolean updateKnownToadPillar(WorldPoint location, boolean isInteractedWith)
	{
		return knownToadPillars.put(location, isInteractedWith);
	}

	public void updateKnownRockLocations(Set<WorldPoint> locations)
	{
		knownRockLocations.clear();
		knownRockLocations.addAll(locations);
	}

	public void updateSpeedBoosts(Set<GameObject> boosts)
	{
		speedBoosts.clear();
		speedBoosts.addAll(boosts);
	}

	public void updateKnownSpeedBoostLocations(Map<WorldPoint, List<WorldPoint>> locations)
	{
		knownSpeedBoostLocations.clear();
		knownSpeedBoostLocations.putAll(locations);
	}

	public void updateKnownFetidPoolLocations(Set<WorldPoint> locations)
	{
		knownFetidPoolLocations.clear();
		knownFetidPoolLocations.addAll(locations);
	}

	public void updateKnownToadPillarLocations(Set<WorldPoint> locations)
	{
		knownToadPillarLocations.clear();
		knownToadPillarLocations.addAll(locations);
	}

	public Set<WorldPoint> getKnownLandTiles()
	{
		return Collections.unmodifiableSet(knownLandTiles);
	}

	public void addKnownLandTiles(Set<WorldPoint> tiles)
	{
		knownLandTiles.addAll(tiles);
	}

	public Set<Integer> getCompletedWaypointIndices()
	{
		return Collections.unmodifiableSet(completedWaypointIndices);
	}

	public void markWaypointCompleted(int waypointIndex)
	{
		completedWaypointIndices.add(waypointIndex);
		
		var route = getCurrentStaticRoute();
		if (route == null || waypointIndex <= 0)
		{
			return;
		}

		for (int i = waypointIndex - 1; i >= 0; i--)
		{
			RouteWaypoint waypoint = route.get(i);
			if (waypoint.getType().isNonNavigableHelper())
			{
				completedWaypointIndices.add(i);
			}
			else
			{
				break;
			}
		}
	}

	public boolean isWaypointCompleted(int waypointIndex)
	{
		return completedWaypointIndices.contains(waypointIndex);
	}

	/**
	 * Calculates the next uncompleted navigable waypoint index by scanning the route.
	 * @return Index of next navigable waypoint, or the route size if none remain
	 */
	public int getNextNavigableWaypointIndex()
	{
		if (currentStaticRoute == null || currentStaticRoute.isEmpty())
		{
			return 0;
		}

		int routeSize = currentStaticRoute.size();
		for (int i = 0; i < routeSize; i++)
		{
			if (!completedWaypointIndices.contains(i))
			{
				RouteWaypoint waypoint = currentStaticRoute.get(i);
				if (!waypoint.getType().isNonNavigableHelper())
				{
					return i;
				}
			}
		}

		return routeSize;
	}

	/**
	 * The current segment ("lap") is derived from the next uncompleted waypoint in route order
	 * rather than tracked as independent mutable state. The segment number only ever exists to
	 * window the markers shown to the player, so anchoring it to the next objective keeps that
	 * window correct even when objectives (e.g. toad pillars) are completed out of order.
	 * @return Segment of the next navigable waypoint, or 1 if route is empty/null
	 */
	public int getCurrentLap()
	{
		if (currentTrial != null && currentTrial.getTrialType() == TrialType.JUBBLY_JIVE)
		{
			if (currentStaticRoute == null || currentStaticRoute.isEmpty())
			{
				return 1;
			}
			int waypointIndex = Math.min(getNextNavigableWaypointIndex(), currentStaticRoute.size() - 1);
			return currentStaticRoute.get(waypointIndex).getLap();
		}

		return currentLap;
	}

	public static Difficulty getCurrentDifficulty(Client client)
	{
		var widget = client.getWidget(InterfaceID.SailingBtHud.BT_RANK_GFX);
		if (widget == null || widget.isHidden())
		{
			return Difficulty.SWORDFISH;
		}

		var spriteId = widget.getSpriteId();
		
		switch (spriteId)
		{
			case 7027:
				return Difficulty.SWORDFISH;
			case 7028:
				return Difficulty.SHARK;
			case 7029:
				return Difficulty.MARLIN;
			default:
				return Difficulty.SWORDFISH; // Default to easiest difficulty
		}
	}
}
