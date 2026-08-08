package com.barracudatrial.pathfinding;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PathObjective
{
	private final WorldPoint location;
	private final int toleranceTiles;
	private final Set<WorldPoint> approachHints;

	public PathObjective(WorldPoint location, int toleranceTiles)
	{
		this(location, toleranceTiles, null);
	}

	public PathObjective(WorldPoint location, int toleranceTiles, Set<WorldPoint> approachHints)
	{
		this.location = location;
		this.toleranceTiles = toleranceTiles;
		this.approachHints = approachHints == null || approachHints.isEmpty()
			? Collections.emptySet()
			: new HashSet<>(approachHints);
	}

	public boolean isSatisfiedAt(WorldPoint position)
	{
		int dx = Math.abs(position.getX() - location.getX());
		int dy = Math.abs(position.getY() - location.getY());
		return Math.max(dx, dy) <= toleranceTiles;
	}

	@Override
	public String toString()
	{
		return String.format("%s within %d", location, toleranceTiles);
	}
}
