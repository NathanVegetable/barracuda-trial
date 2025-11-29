package com.barracudatrial.pathfinding;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

public class PathResult
{
	@Getter
	private final List<PathNode> pathNodes;
	private final double totalCost;
	@Getter
	private final boolean reachedGoal;

	public PathResult(List<PathNode> pathNodes, double totalCost, boolean reachedGoal)
	{
		this.pathNodes = pathNodes;
		this.totalCost = totalCost;
		this.reachedGoal = reachedGoal;
	}

	public List<WorldPoint> getPath()
	{
		List<WorldPoint> path = new ArrayList<>();
		for (PathNode node : pathNodes)
		{
			path.add(node.getPosition());
		}
		return path;
	}

	public double getCost()
	{
		return totalCost;
	}

	public double getCostFromIndex(int index)
	{
		if (index < 0 || index >= pathNodes.size())
		{
			return totalCost;
		}
		return totalCost - pathNodes.get(index).getCumulativeCost();
	}
}