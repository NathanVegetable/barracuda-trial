package com.barracudatrial;

import lombok.Getter;

@Getter
public enum PathfindingEffort
{
	LOW(80, 8),
	MEDIUM(100, 12),
	HIGH(150, 20);

	private final int maxSearchNodes;
	private final int minSpatialDistance;

	PathfindingEffort(int maxSearchNodes, int minSpatialDistance)
	{
		this.maxSearchNodes = maxSearchNodes;
		this.minSpatialDistance = minSpatialDistance;
	}
}
