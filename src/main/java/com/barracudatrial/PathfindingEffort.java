package com.barracudatrial;

import lombok.Getter;

@Getter
public enum PathfindingEffort
{
	LOW(125, 15),
	MEDIUM(160, 20),
	HIGH(240, 30);

	private final int maxSearchNodes;
	private final int minSpatialDistance;

	PathfindingEffort(int maxSearchNodes, int minSpatialDistance)
	{
		this.maxSearchNodes = maxSearchNodes;
		this.minSpatialDistance = minSpatialDistance;
	}
}
