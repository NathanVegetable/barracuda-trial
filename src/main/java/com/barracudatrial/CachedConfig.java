package com.barracudatrial;

import lombok.Getter;

import java.awt.Color;

/**
 * Caches config values for performance
 * Config method calls are slow, so we cache values and update on config changes
 */
public class CachedConfig
{
	private final BarracudaTrialConfig config;

	// Path settings
	@Getter private boolean showOptimalPath;
	@Getter private RouteOptimization routeOptimization;
	@Getter private Color pathColor;
	@Getter private int pathWidth;
	@Getter private int pathLookahead;
	@Getter private boolean showPathTiles;

	// Objective settings
	@Getter private boolean highlightObjectives;
	@Getter private Color objectivesColorCurrentWaypoint;
	@Getter private Color objectivesColorCurrentLap;
	@Getter private Color objectivesColorLaterLaps;
	@Getter private Color windCatcherColor;

	// Object highlighting settings
	@Getter private boolean highlightSpeedBoosts;
	@Getter private Color speedBoostColor;
	@Getter private boolean highlightClouds;
	@Getter private Color cloudColor;
	@Getter private int cloudDangerRadius;

	public CachedConfig(BarracudaTrialConfig config)
	{
		this.config = config;
		updateCache();
	}

	/**
	 * Updates all cached values from the config
	 * Should be called on plugin startup and when config changes
	 */
	public void updateCache()
	{
		showOptimalPath = config.showOptimalPath();
		routeOptimization = config.routeOptimization();
		pathColor = config.pathColor();
		pathWidth = config.pathWidth();
		pathLookahead = config.pathLookahead();
		showPathTiles = config.showPathTiles();

		highlightObjectives = config.highlightObjectives();
		objectivesColorCurrentWaypoint = config.objectivesColorCurrentWaypoint();
		objectivesColorCurrentLap = config.objectivesColorCurrentLap();
		objectivesColorLaterLaps = config.objectivesColorLaterLaps();
		windCatcherColor = config.windCatcherColor();

		highlightSpeedBoosts = config.highlightSpeedBoosts();
		speedBoostColor = config.speedBoostColor();
		highlightClouds = config.highlightClouds();
		cloudColor = config.cloudColor();
		cloudDangerRadius = config.cloudDangerRadius();
	}
}
