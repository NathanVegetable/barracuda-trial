package com.barracudatrial;

import lombok.Getter;

@Getter
public enum PathfindingEffort
{
	LOW(100, 10),
	MEDIUM(125, 15),
	HIGH(190, 25);

	private final int maxSearchNodes;
	private final int minSpatialDistance;

	PathfindingEffort(int maxSearchNodes, int minSpatialDistance)
	{
		this.maxSearchNodes = maxSearchNodes;
		this.minSpatialDistance = minSpatialDistance;
	}
}
