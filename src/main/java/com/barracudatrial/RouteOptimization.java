package com.barracudatrial;

import lombok.Getter;

/**
 * Route optimization strategies for pathfinding.
 * Determines how aggressively the pathfinder penalizes turns and encourages picking up speed boosts.
 */
@Getter
public enum RouteOptimization
{
	RELAXED(
		4,      // pathRecalcIntervalTicks - recalculate less often for smoother paths
		0.70,   // switchCostRatio - harder to switch paths (30% better required)
		2.0,    // turnPenaltyBase - higher penalty for turns (smoother routes)
		-4.0    // speedBoostCost - less incentive to grab boosts (fewer detours)
	),
	EFFICIENT(
		2,      // pathRecalcIntervalTicks - recalculate more often for dynamic routing
		0.85,   // switchCostRatio - easier to switch paths (15% better required)
		1.0,    // turnPenaltyBase - lower penalty for turns (more dynamic routes)
		-6.0    // speedBoostCost - more incentive to grab boosts (more detours)
	);

	private final int pathRecalcIntervalTicks;
	private final double switchCostRatio;
	private final double turnPenaltyBase;
	private final double speedBoostCost;

	RouteOptimization(int pathRecalcIntervalTicks, double switchCostRatio, double turnPenaltyBase, double speedBoostCost)
	{
		this.pathRecalcIntervalTicks = pathRecalcIntervalTicks;
		this.switchCostRatio = switchCostRatio;
		this.turnPenaltyBase = turnPenaltyBase;
		this.speedBoostCost = speedBoostCost;
	}
}
