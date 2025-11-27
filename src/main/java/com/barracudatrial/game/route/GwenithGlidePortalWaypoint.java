package com.barracudatrial.game.route;

import lombok.Getter;

import static com.barracudatrial.game.route.RouteWaypoint.WaypointType.PORTAL;

@Getter
public class GwenithGlidePortalWaypoint extends RouteWaypoint
{
	GwenithGlidePortal portal;

	public GwenithGlidePortalWaypoint(GwenithGlidePortal portal)
	{
		super(PORTAL, portal.getLocation());
		this.portal = portal;
	}

	public GwenithGlidePortalWaypoint(int lap, GwenithGlidePortal portal)
	{
		super(lap, PORTAL, portal.getLocation());
		this.portal = portal;
	}
}
