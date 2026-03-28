package com.barracudatrial;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("barracudatrial")
public interface BarracudaTrialConfig extends Config
{
	@ConfigSection(
		name = "Path Display",
		description = "Settings for the optimal path overlay",
		position = 0
	)
	String pathSection = "pathSection";

	@ConfigSection(
		name = "Objectives",
		description = "Settings for objective highlighting",
		position = 1
	)
	String objectivesSection = "objectivesSection";

	@ConfigSection(
		name = "Object Highlighting",
		description = "Settings for object highlighting",
		position = 2
	)
	String objectHighlightingSection = "objectHighlightingSection";

	@ConfigSection(
		name = "Debug",
		description = "Debug and diagnostic settings",
		position = 3
	)
	String debugSection = "debugSection";

	@ConfigItem(
		keyName = "showOptimalPath",
		name = "Show Optimal Path",
		description = "Display the optimal path to collect all lost supplies",
		section = pathSection,
		position = 0
	)
	default boolean showOptimalPath()
	{
		return true;
	}

	@ConfigItem(
		keyName = "routeOptimization",
		name = "Route Optimization",
		description = "Relaxed: smoother path with fewer turns and updates.<br>Efficient: dynamic routing with frequent updates, actively seeks speed boosts.",
		section = pathSection,
		position = 1
	)
	default RouteOptimization routeOptimization()
	{
		return RouteOptimization.RELAXED;
	}


	@ConfigItem(
		keyName = "pathColor",
		name = "Path",
		description = "Color for optimal path",
		section = pathSection,
		position = 4
	)
	@Alpha
	default Color pathColor()
	{
		return new Color(0, 255, 0, 180);
	}

	@ConfigItem(
		keyName = "pathWidth",
		name = "Path Width",
		description = "Width of optimal path",
		section = pathSection,
		position = 5
	)
	@Range(min = 1, max = 10)
	default int pathWidth()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "showPathTiles",
		name = "Show Path Tiles",
		description = "Highlight individual tiles along the calculated path",
		section = pathSection,
		position = 6
	)
	default boolean showPathTiles()
	{
		return false;
	}

	@ConfigItem(
		keyName = "highlightObjectives",
		name = "Highlight Objectives",
		description = "Highlight objectives in the trial area",
		section = objectivesSection,
		position = 0
	)
	default boolean highlightObjectives()
	{
		return true;
	}

	@ConfigItem(
		keyName = "objectivesColorCurrentWaypoint",
		name = "Current Waypoint",
		description = "Color for current waypoint",
		section = objectivesSection,
		position = 1
	)
	@Alpha
	default Color objectivesColorCurrentWaypoint()
	{
		return new Color(0, 255, 0, 180);
	}

	@ConfigItem(
		keyName = "objectivesColorCurrentLap",
		name = "Current Lap",
		description = "Color for objectives on current lap",
		section = objectivesSection,
		position = 2
	)
	@Alpha
	default Color objectivesColorCurrentLap()
	{
		return new Color(255, 215, 0, 180);
	}

	@ConfigItem(
		keyName = "objectivesColorLaterLaps",
		name = "Later Lap",
		description = "Color for objectives on later laps",
		section = objectivesSection,
		position = 3
	)
	@Alpha
	default Color objectivesColorLaterLaps()
	{
		return new Color(255, 40, 0, 120);
	}

	@ConfigItem(
		keyName = "windCatcherColor",
		name = "Wind Catcher",
		description = "Color for path where wind catcher should be used",
		section = objectivesSection,
		position = 4
	)
	@Alpha
	default Color windCatcherColor()
	{
		return new Color(173, 216, 230, 180); // Light blue
	}

	@ConfigItem(
		keyName = "highlightSpeedBoosts",
		name = "Highlight Speed Boosts",
		description = "Highlight speed boosts",
		section = objectHighlightingSection,
		position = 0
	)
	default boolean highlightSpeedBoosts()
	{
		return false;
	}

	@ConfigItem(
		keyName = "speedBoostColor",
		name = "Speed Boost",
		description = "Color for speed boosts",
		section = objectHighlightingSection,
		position = 1
	)
	@Alpha
	default Color speedBoostColor()
	{
		return new Color(0, 255, 0, 150); // Bright green for speed!
	}

	@ConfigItem(
		keyName = "highlightClouds",
		name = "Highlight Lightning Clouds",
		description = "Highlight dangerous lightning clouds",
		section = objectHighlightingSection,
		position = 2
	)
	default boolean highlightClouds()
	{
		return false;
	}

	@ConfigItem(
		keyName = "cloudColor",
		name = "Lightning Cloud",
		description = "Color for lightning clouds",
		section = objectHighlightingSection,
		position = 3
	)
	@Alpha
	default Color cloudColor()
	{
		return new Color(255, 0, 0, 120);
	}

	@ConfigItem(
		keyName = "cloudDangerRadius",
		name = "Cloud Danger Radius",
		description = "Radius in tiles for the cloud danger area",
		section = objectHighlightingSection,
		position = 4
	)
	@Range(max = 5)
	default int cloudDangerRadius()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "pathLookahead",
		name = "Path Lookahead",
		description = "Number of shipments to path ahead. Higher values increase CPU usage.",
		section = debugSection,
		position = 0
	)
	@Range(min = 1, max = 10)
	default int pathLookahead()
	{
		return 3;
	}

	@ConfigItem(
		keyName = "maxPathfindingDistance",
		name = "Max Pathfinding Distance",
		description = "Maximum search distance for pathfinding. Higher values allow pathing to further targets but increase CPU usage.",
		section = debugSection,
		position = 1
	)
	@Range(min = 50, max = 200)
	default int maxPathfindingDistance()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "showWaypointDetails",
		name = "Show Waypoint Details",
		description = "Show detailed waypoint information (type, status, coordinates)",
		section = debugSection,
		position = 2
	)
	default boolean showWaypointDetails()
	{
		return false;
	}
}
