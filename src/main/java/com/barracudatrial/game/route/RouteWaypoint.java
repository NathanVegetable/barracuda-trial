package com.barracudatrial.game.route;

import java.util.List;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public class RouteWaypoint
{
	protected final WaypointType type;
	private final int lap;
	private final WorldPoint location;

	// If the location is not visible, we fall back to these locations (in order of preference)
	private final List<WorldPoint> fallbackLocations;

	public RouteWaypoint(WaypointType type, WorldPoint location)
	{
		this.lap = 1;
		this.type = type;
		this.location = location;
		this.fallbackLocations = List.of();
	}

	public RouteWaypoint(int lap, WaypointType type, WorldPoint location)
	{
		this.lap = lap;
		this.type = type;
		this.location = location;
		this.fallbackLocations = List.of();
	}

	public RouteWaypoint(int lap, WaypointType type, WorldPoint location, List<WorldPoint> fallbackLocations)
	{
		this.lap = lap;
		this.type = type;
		this.location = location;
		this.fallbackLocations = fallbackLocations;
	}

	@Getter
	public enum WaypointType
	{
		SHIPMENT(2),
		RUM_PICKUP(9),
		RUM_DROPOFF(9),
		TOAD_PICKUP(9),
		TOAD_PILLAR(10),
		PORTAL_ENTER(1),
		PORTAL_EXIT(0),
		PATHFINDING_HINT(0),
		USE_WIND_CATCHER(0);

		private final int toleranceTiles;

		WaypointType(int toleranceTiles)
		{
			this.toleranceTiles = toleranceTiles;
		}

		public boolean isNonNavigableHelper()
		{
			return this == PATHFINDING_HINT || this == USE_WIND_CATCHER || this == PORTAL_EXIT;
		}
	}

	@Override
	public String toString()
	{
		WorldPoint loc = getLocation();
		return String.format("%s at %s", type, loc != null ? loc : "unknown");
	}
}
