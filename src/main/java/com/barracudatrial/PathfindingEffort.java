package com.barracudatrial;

import lombok.Getter;

@Getter
public enum PathfindingEffort
{
	LOW(160, 20),
	HIGH(420, 53);

	private final int maxSearchNodes;
	private final int minSpatialDistance;

	PathfindingEffort(int maxSearchNodes, int minSpatialDistance)
	{
		this.maxSearchNodes = maxSearchNodes;
		this.minSpatialDistance = minSpatialDistance;
	}
}
