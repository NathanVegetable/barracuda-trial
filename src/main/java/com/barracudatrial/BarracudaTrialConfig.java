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
		name = "Object Hightlighting",
		description = "Settings for object highlighting",
		position = 2
	)
	String objectHighlightingSection = "objectHighlightingSection";

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
		description = "Relaxed: fewer turns overall (smoother). Efficient: grab nearby boosts (more dynamic).",
		section = pathSection,
		position = 1
	)
	default RouteOptimization routeOptimization()
	{
		return RouteOptimization.RELAXED;
	}

	@ConfigItem(
		keyName = "pathLookahead",
		name = "Path Lookahead",
		description = "Number of waypoints to calculate ahead. Lower values improve performance and reduce visual clutter.",
		section = pathSection,
		position = 2
	)
	@Range(min = 1, max = 10)
	default int pathLookahead()
	{
		return 3;
	}

	@ConfigItem(
		keyName = "pathColor",
		name = "Path Color",
		description = "Color of the optimal path line",
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
		description = "Width of the path line",
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
		description = "Display detailed information for each waypoint and path tile (type, completion status, coordinates)",
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
		description = "Color for objective highlights on current lap",
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
		description = "Color for objective highlights on later laps",
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
		name = "Wind Catcher Color",
		description = "Color for wind catcher path segments and tile highlights",
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
		description = "Highlight speed boost areas",
		section = objectHighlightingSection,
		position = 0
	)
	default boolean highlightSpeedBoosts()
	{
		return false;
	}

	@ConfigItem(
		keyName = "speedBoostColor",
		name = "Speed Boost Color",
		description = "Color for speed boost highlights",
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
		return true;
	}

	@ConfigItem(
		keyName = "cloudColor",
		name = "Cloud Color",
		description = "Color for lightning cloud highlights",
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
}
