package com.barracudatrial.game.route;

import java.util.List;
import java.util.Set;

public interface TrialConfig
{
	TrialType getTrialType();

	// Shipment/lost supplies (universal across all trials, but different object IDs)
	Set<Integer> getShipmentBaseIds();
	int getShipmentImpostorId();

	// Routes
	List<RouteWaypoint> getRoute(Difficulty difficulty);
}
