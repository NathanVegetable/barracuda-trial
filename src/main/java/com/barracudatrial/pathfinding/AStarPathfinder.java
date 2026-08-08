package com.barracudatrial.pathfinding;

import com.barracudatrial.RouteOptimization;
import net.runelite.api.coords.WorldPoint;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * A* pathfinding algorithm for finding optimal routes between points
 * considering variable tile costs (speed boosts, clouds, rocks, etc.)
 */
@Slf4j
public class AStarPathfinder
{
	private static final long TIMEOUT_MS = 3000;
	private static final long TIMEOUT_NANOS = TIMEOUT_MS * 1_000_000L;

	private static final int[] HEADING_DELTAS = {-1, 0, 1};

	public PathResult findPath(BarracudaTileCostCalculator costCalculator, RouteOptimization routeOptimization, WorldPoint start, List<PathObjective> objectives, int maxSearchDistance, int minSpatialDistance, int boatDirectionDx, int boatDirectionDy)
	{
		if (objectives.isEmpty())
		{
			return new PathResult(new ArrayList<>(), Double.POSITIVE_INFINITY, false);
		}

		Search search = new Search(costCalculator, routeOptimization, start, objectives, maxSearchDistance, minSpatialDistance, boatDirectionDx, boatDirectionDy);
		return search.run();
	}

	private static final class Search
	{
		private final BarracudaTileCostCalculator costCalculator;
		private final RouteOptimization routeOptimization;
		private final WorldPoint start;
		private final List<PathObjective> objectives;
		private final int maxSearchDistance;
		private final int minSpatialDistance;
		private final int boatDirectionDx;
		private final int boatDirectionDy;

		private final PriorityQueue<Node> openSet = new PriorityQueue<>(
			Comparator.comparingDouble((Node n) -> n.fScore)
		);
		private final Map<StateKey, Node> allNodes = new HashMap<>();
		private final Set<StateKey> closedSet = new HashSet<>();

		Search(BarracudaTileCostCalculator costCalculator, RouteOptimization routeOptimization, WorldPoint start, List<PathObjective> objectives, int maxSearchDistance, int minSpatialDistance, int boatDirectionDx, int boatDirectionDy)
		{
			this.costCalculator = costCalculator;
			this.routeOptimization = routeOptimization;
			this.start = start;
			this.objectives = objectives;
			this.maxSearchDistance = maxSearchDistance;
			this.minSpatialDistance = minSpatialDistance;
			this.boatDirectionDx = boatDirectionDx;
			this.boatDirectionDy = boatDirectionDy;
		}

		PathResult run()
		{
			long startTime = System.nanoTime();

			Node startNode = new Node(start);
			startNode.gScore = 0;
			startNode.hScore = heuristic(start);
			startNode.fScore = startNode.hScore;
			startNode.headingIdx = initialHeadingIndex();
			startNode.objectiveIndex = advanceObjectiveIndex(start, 0);

			if (hasClearedAllObjectives(startNode))
			{
				return new PathResult(reconstructPath(startNode), 0, true);
			}

			openSet.add(startNode);
			allNodes.put(stateKeyOf(startNode), startNode);

			int nodesExplored = 0;
			Node bestNodeSoFar = startNode;
			int bestObjectiveIndex = startNode.objectiveIndex;
			int bestDistanceToObjective = Integer.MAX_VALUE;
			int maxSpatialDistanceReached = 0;

			while (!openSet.isEmpty())
			{
				Node current = openSet.poll();

				StateKey currentKey = stateKeyOf(current);
				if (closedSet.contains(currentKey))
				{
					continue;
				}

				if (hasClearedAllObjectives(current))
				{
					return new PathResult(reconstructPath(current), current.gScore, true);
				}

				int distanceToObjective = chebyshevDistance(current.position, objectives.get(current.objectiveIndex).getLocation());
				boolean madeProgress = current.objectiveIndex > bestObjectiveIndex
					|| (current.objectiveIndex == bestObjectiveIndex && distanceToObjective < bestDistanceToObjective);

				if (madeProgress)
				{
					bestObjectiveIndex = current.objectiveIndex;
					bestDistanceToObjective = distanceToObjective;
					bestNodeSoFar = current;
				}

				closedSet.add(currentKey);
				nodesExplored++;

				maxSpatialDistanceReached = Math.max(maxSpatialDistanceReached, chebyshevDistance(current.position, start));

				// Ensure we search minimum distance forward even when obstacles consume node budget
				if (nodesExplored > maxSearchDistance * maxSearchDistance && maxSpatialDistanceReached >= minSpatialDistance)
				{
					break;
				}

				if ((nodesExplored & 0xFF) == 0 && (System.nanoTime() - startTime) > TIMEOUT_NANOS)
				{
					log.warn("Pathfinding timeout after {}ms ({} nodes explored, cleared {} of {} objectives, best distance to next: {})",
						TIMEOUT_MS, nodesExplored, bestObjectiveIndex, objectives.size(), bestDistanceToObjective);
					break;
				}

				expandNeighborsOf(current);
			}

			// If we failed to clear every objective, return the furthest progress we found
			if (bestNodeSoFar != startNode)
			{
				return new PathResult(reconstructPath(bestNodeSoFar), bestNodeSoFar.gScore, false);
			}

			return new PathResult(new ArrayList<>(), Double.POSITIVE_INFINITY, false);
		}

		private boolean hasClearedAllObjectives(Node node)
		{
			return node.objectiveIndex >= objectives.size();
		}

		private int advanceObjectiveIndex(WorldPoint position, int objectiveIndex)
		{
			int index = objectiveIndex;

			while (index < objectives.size() && objectives.get(index).isSatisfiedAt(position))
			{
				index++;
			}

			return index;
		}

		// Steering neighbors: delta heading -1,0,+1 (±15°) and move one tile in the heading's dominant 8-way direction
		private void expandNeighborsOf(Node current)
		{
			// If unknown heading, treat each 8-way direction as a possible base heading
			if (current.headingIdx == -1)
			{
				for (int baseDir8 = 0; baseDir8 < 8; baseDir8++)
				{
					relaxNeighbor(current, DIR8_TO_HEADING24[baseDir8], 0);
				}

				return;
			}

			for (int deltaH : HEADING_DELTAS)
			{
				relaxNeighbor(current, (current.headingIdx + deltaH + 24) % 24, Math.abs(deltaH));
			}
		}

		private void relaxNeighbor(Node current, int nextHeading, int headingDelta)
		{
			int moveDir = headingToDir8(nextHeading);
			int dx = DIRS[moveDir][0];
			int dy = DIRS[moveDir][1];
			WorldPoint neighbor = new WorldPoint(current.position.getX() + dx, current.position.getY() + dy, current.position.getPlane());

			int neighborObjectiveIndex = advanceObjectiveIndex(neighbor, current.objectiveIndex);
			StateKey neighborKey = new StateKey(neighbor, nextHeading, neighborObjectiveIndex);
			if (closedSet.contains(neighborKey))
			{
				return;
			}

			double tileCost = costCalculator.getTileCost(current.position, neighbor, current.objectiveIndex);
			if (tileCost > 50000)
			{
				return;
			}

			boolean isDiagonal = dx != 0 && dy != 0;
			double geometricDistance = isDiagonal ? Math.sqrt(2) : 1.0;
			double turningCost = calculateTurningCost(routeOptimization, headingDelta);

			double tentativeGScore = current.gScore + (tileCost * geometricDistance) + turningCost;

			Node neighborNode = allNodes.get(neighborKey);
			if (neighborNode == null)
			{
				neighborNode = new Node(neighbor);
				neighborNode.headingIdx = nextHeading;
				neighborNode.objectiveIndex = neighborObjectiveIndex;
				allNodes.put(neighborKey, neighborNode);
			}

			if (tentativeGScore < neighborNode.gScore)
			{
				neighborNode.parent = current;
				neighborNode.gScore = tentativeGScore;
				neighborNode.hScore = heuristic(neighbor);
				neighborNode.fScore = neighborNode.gScore + neighborNode.hScore;

				openSet.add(neighborNode);
			}
		}

		// Map the provided 8-way boat direction into a 24-heading index (15° steps)
		private int initialHeadingIndex()
		{
			if (boatDirectionDx == 0 && boatDirectionDy == 0)
			{
				return -1;
			}

			int baseDir8 = dirIndex(Integer.signum(boatDirectionDx), Integer.signum(boatDirectionDy));
			return baseDir8 == -1 ? -1 : DIR8_TO_HEADING24[baseDir8];
		}

		private StateKey stateKeyOf(Node node)
		{
			return new StateKey(node.position, node.headingIdx, node.objectiveIndex);
		}

		/**
		 * Heuristic: returns 0 (Dijkstra mode).
		 * Rationale: tile costs may be negative (speed boosts), so admissible heuristics like
		 * Manhattan/Chebyshev are not safe; Dijkstra guarantees optimality.
		 */
		private double heuristic(WorldPoint from)
		{
			return 0;
		}
	}

	private static double calculateTurningCost(RouteOptimization routeOptimization, int absDelta)
	{
		// absDelta is the absolute heading step change (in 24-heading units: 0 or 1 here)
		if (absDelta == 0)
		{
			return 0.0;
		}

		var angle = absDelta * 15;
		var baseCost = routeOptimization.getTurnPenaltyBase();

		return angle > 105 ? baseCost * 4 : baseCost;
	}

	private static int chebyshevDistance(WorldPoint from, WorldPoint to)
	{
		return Math.max(Math.abs(from.getX() - to.getX()), Math.abs(from.getY() - to.getY()));
	}

	private static int dirIndex(int dx, int dy)
	{
		for (int i = 0; i < DIRS.length; i++)
		{
			if (DIRS[i][0] == dx && DIRS[i][1] == dy)
			{
				return i;
			}
		}
		return -1;
	}

	// Map 24 headings (15° each) to the dominant 8-way movement direction
	private static final int[] HEADING_TO_DIR8 = {
		0, 0,    // 0°, 15° -> E
		1, 1, 1, // 30°,45°,60° -> NE
		2, 2, 2, // 75°,90°,105° -> N
		3, 3, 3, // 120°,135°,150° -> NW
		4, 4, 4, // 165°,180°,195° -> W
		5, 5, 5, // 210°,225°,240° -> SW
		6, 6, 6, // 255°,270°,285° -> S
		7, 7, 7, // 300°,315°,330° -> SE
		0        // 345° -> E
	};

	private static final int[] DIR8_TO_HEADING24 = {0, 2, 5, 8, 12, 15, 18, 21};

	private static int headingToDir8(int headingIdx)
	{
		int idx = (headingIdx % 24 + 24) % 24;
		return HEADING_TO_DIR8[idx];
	}

	private static List<PathNode> reconstructPath(Node goalNode)
	{
		List<PathNode> pathNodes = new ArrayList<>();
		Node current = goalNode;

		while (current != null)
		{
			pathNodes.add(new PathNode(current.position, current.gScore));
			current = current.parent;
		}

		// Reverse to get path from start to goal
		Collections.reverse(pathNodes);
		return pathNodes;
	}

	private static final int[][] DIRS = {
		{1, 0},   // 0: E
		{1, 1},   // 1: NE
		{0, 1},   // 2: N
		{-1, 1},  // 3: NW
		{-1, 0},  // 4: W
		{-1, -1}, // 5: SW
		{0, -1},  // 6: S
		{1, -1}   // 7: SE
	};

	private static final class StateKey
	{
		private final WorldPoint pos;
		private final int headingIdx;
		private final int objectiveIndex;

		StateKey(WorldPoint pos, int headingIdx, int objectiveIndex)
		{
			this.pos = pos;
			this.headingIdx = headingIdx;
			this.objectiveIndex = objectiveIndex;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			StateKey stateKey = (StateKey) o;
			return headingIdx == stateKey.headingIdx && objectiveIndex == stateKey.objectiveIndex && pos.equals(stateKey.pos);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(pos, headingIdx, objectiveIndex);
		}
	}

	private static class Node
	{
		WorldPoint position;
		Node parent;
		int headingIdx = -1; // 0..23 or -1 for unknown
		int objectiveIndex = 0; // how many ordered objectives this node has already cleared
		double gScore = Double.POSITIVE_INFINITY; // Cost from start to this node
		double hScore = 0; // Heuristic cost from this node to goal
		double fScore = Double.POSITIVE_INFINITY; // Total cost (g + h)

		Node(WorldPoint position)
		{
			this.position = position;
		}
	}
}
