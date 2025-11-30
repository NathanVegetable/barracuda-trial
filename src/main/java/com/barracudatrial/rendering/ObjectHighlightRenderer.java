package com.barracudatrial.rendering;

import com.barracudatrial.BarracudaTrialPlugin;
import com.barracudatrial.CachedConfig;
import com.barracudatrial.game.ObjectTracker;
import com.barracudatrial.game.route.RouteWaypoint;
import com.barracudatrial.game.route.RouteWaypointFilter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Handles rendering of objective highlights (shipments, toads, portals, rum)
 */
@RequiredArgsConstructor
public class ObjectHighlightRenderer
{
	private final Client client;
	private final BarracudaTrialPlugin plugin;
	private final ModelOutlineRenderer modelOutlineRenderer;
	private final BoatZoneRenderer boatZoneRenderer;

	public void renderLostSupplies(Graphics2D graphics)
	{
		var cachedConfig = plugin.getCachedConfig();
		var gameState = plugin.getGameState();
		var lostSupplies = gameState.getLostSupplies();
		var route = gameState.getCurrentStaticRoute();
		var currentLap = gameState.getCurrentLap();
		var completedWaypointIndices = gameState.getCompletedWaypointIndices();

		Set<WorldPoint> allRouteLocations = Collections.emptySet();
		Set<WorldPoint> laterLapLocations = Collections.emptySet();
		WorldPoint currentWaypointLocation = null;

		if (route != null && !route.isEmpty())
		{
			allRouteLocations = new HashSet<>(route.size());
			laterLapLocations = new HashSet<>(route.size());

			for (int i = 0; i < route.size(); i++)
			{
				var waypoint = route.get(i);
				var location = waypoint.getLocation();

				allRouteLocations.add(location);

				if (currentLap != waypoint.getLap())
				{
					laterLapLocations.add(location);
				}
				else if (currentWaypointLocation == null && !completedWaypointIndices.contains(i) && !waypoint.getType().isNonNavigatableHelper())
				{
					currentWaypointLocation = location;
				}
			}
		}

		for (var lostSupplyObject : lostSupplies)
		{
			var worldLocation = lostSupplyObject.getWorldLocation();

			Color renderColor;
			if (currentWaypointLocation != null && currentWaypointLocation.equals(worldLocation))
			{
				renderColor = cachedConfig.getObjectivesColorCurrentWaypoint();
			}
			else if (laterLapLocations.contains(worldLocation))
			{
				renderColor = cachedConfig.getObjectivesColorLaterLaps();
			}
			else
			{
				renderColor = cachedConfig.getObjectivesColorCurrentLap();
			}

			renderGameObjectWithHighlight(graphics, lostSupplyObject, renderColor, false);
		}
	}

	public void renderSpeedBoosts(Graphics2D graphics)
	{
		CachedConfig cachedConfig = plugin.getCachedConfig();
		var color = cachedConfig.getSpeedBoostColor();

		for (GameObject speedBoostObject : plugin.getGameState().getSpeedBoosts())
		{
			renderGameObjectWithHighlight(graphics, speedBoostObject, color, true);
		}
	}

	public void renderLightningClouds(Graphics2D graphics)
	{
		CachedConfig cachedConfig = plugin.getCachedConfig();
		Color color = cachedConfig.getCloudColor();
		for (NPC cloudNpc : plugin.getGameState().getLightningClouds())
		{
			int currentAnimation = cloudNpc.getAnimation();

			boolean isCloudSafe = ObjectTracker.IsCloudSafe(currentAnimation);
			if (isCloudSafe)
			{
				continue;
			}

			renderCloudDangerAreaOnGround(graphics, cloudNpc, color);

			renderNpcWithHighlight(graphics, cloudNpc, color);
		}
	}

	public void renderToadPickup(Graphics2D graphics)
	{
		var cached = plugin.getCachedConfig();
		var state = plugin.getGameState();
		var route = state.getCurrentStaticRoute();
		if (route == null || route.isEmpty())
			return;

		int currentLap = state.getCurrentLap();
		var completed = state.getCompletedWaypointIndices();
		int nextWaypointIndex = state.getNextNavigatableWaypointIndex();

		for (int i = 0; i < route.size(); i++)
		{
			var waypoint = route.get(i);
			if (waypoint.getType() != RouteWaypoint.WaypointType.TOAD_PICKUP)
				continue;

			if (completed.contains(i))
				continue;

			var loc = waypoint.getLocation();

			Color color;
			if (i == nextWaypointIndex)
			{
				color = cached.getObjectivesColorCurrentWaypoint();
			}
			else if (waypoint.getLap() != currentLap)
			{
				color = cached.getObjectivesColorLaterLaps();
			}
			else
			{
				color = cached.getObjectivesColorCurrentLap();
			}

			var toadObject = RenderingUtils.findGameObjectAtWorldPoint(client, loc);
			if (toadObject == null)
				continue;

			boatZoneRenderer.renderBoatZoneRectangle(graphics, loc, color);
			renderGameObjectWithHighlight(graphics, toadObject, color, true);
		}
	}

	public void renderToadPillars(Graphics2D graphics)
	{
		var cached = plugin.getCachedConfig();
		var state = plugin.getGameState();
		var route = state.getCurrentStaticRoute();
		if (route == null)
			return;

		if (!state.isHasThrowableObjective())
			return;

		int currentLap = state.getCurrentLap();
		var completed = state.getCompletedWaypointIndices();
		int currentWaypointIndex = state.getNextNavigatableWaypointIndex();

		var currentLapLocations = RouteWaypointFilter.getLocationsByTypeAndLap(
				route, RouteWaypoint.WaypointType.TOAD_PILLAR, currentLap, completed);

		var laterLapLocations = new HashSet<WorldPoint>();
		for (int i = 0; i < route.size(); i++)
		{
			if (completed.contains(i))
				continue;

			var wp = route.get(i);
			if (wp.getType() == RouteWaypoint.WaypointType.TOAD_PILLAR && wp.getLap() != currentLap)
			{
				laterLapLocations.add(wp.getLocation());
			}
		}

		List<WorldPoint> currentWaypointLocations = RouteWaypointFilter.findNextNavigatableWaypoints(
				route, currentWaypointIndex, completed, 2);

		state.getKnownToadPillars().entrySet().stream()
				.filter(e -> !e.getValue())
				.map(Map.Entry::getKey)
				.map(p -> RenderingUtils.findGameObjectAtWorldPoint(client, p))
				.filter(Objects::nonNull)
				.forEach(pillar -> {
					var loc = pillar.getWorldLocation();

					Color color;
					if (currentWaypointLocations.contains(loc))
					{
						color = cached.getObjectivesColorCurrentWaypoint();
					}
					else if (currentLapLocations.contains(loc))
					{
						color = cached.getObjectivesColorCurrentLap();
					}
					else if (laterLapLocations.contains(loc))
					{
						return;
					}
					else
					{
						return;
					}

					renderGameObjectWithHighlight(graphics, pillar, color, false);
				});
	}

	public void renderPortals(Graphics2D graphics)
	{
		var cached = plugin.getCachedConfig();
		var state = plugin.getGameState();
		var route = state.getCurrentStaticRoute();
		if (route == null)
			return;

		int currentLap = state.getCurrentLap();
		var completed = state.getCompletedWaypointIndices();
		int currentWaypointIndex = state.getNextNavigatableWaypointIndex();

		var currentLapPortalLocations = RouteWaypointFilter.getLocationsByTypeAndLap(
				route, RouteWaypoint.WaypointType.PORTAL_ENTER, currentLap, completed);

		List<WorldPoint> next2WaypointLocations = RouteWaypointFilter.findNextNavigatableWaypoints(
				route, currentWaypointIndex, completed, 2);

		for (WorldPoint portalLocation : currentLapPortalLocations)
		{
			var portalObject = RenderingUtils.findGameObjectAtWorldPoint(client, portalLocation);
			if (portalObject == null)
				continue;

			Color color;
			if (next2WaypointLocations.contains(portalLocation))
			{
				color = cached.getObjectivesColorCurrentWaypoint();
			}
			else
			{
				color = cached.getObjectivesColorCurrentLap();
			}

			renderGameObjectWithHighlight(graphics, portalObject, color, true);
		}
	}

	public void renderRumLocations(Graphics2D graphics)
	{
		CachedConfig cachedConfig = plugin.getCachedConfig();
		Color rumHighlightColor = cachedConfig.getObjectivesColorCurrentLap();

		var isCarryingRum = plugin.getGameState().isHasThrowableObjective();
		WorldPoint targetRumLocation;

		if (isCarryingRum)
		{
			targetRumLocation = plugin.getGameState().getRumReturnLocation();
		}
		else
		{
			targetRumLocation = plugin.getGameState().getRumPickupLocation();
		}

		if (targetRumLocation != null)
		{
			boolean isNextWaypoint = isRumLocationNextWaypoint(targetRumLocation);

			if (isNextWaypoint)
			{
				boatZoneRenderer.renderBoatZoneRectangle(graphics, targetRumLocation, rumHighlightColor);
				renderRumLocationHighlight(graphics, targetRumLocation, rumHighlightColor);
			}
		}
	}

	private void renderRumLocationHighlight(Graphics2D graphics, WorldPoint rumLocationPoint, Color highlightColor)
	{
		GameObject rumObjectAtLocation = RenderingUtils.findGameObjectAtWorldPoint(client, rumLocationPoint);
		if (rumObjectAtLocation != null)
		{
			renderGameObjectWithHighlight(graphics, rumObjectAtLocation, highlightColor, true);
		}
		else
		{
			renderTileHighlightAtWorldPoint(graphics, rumLocationPoint, highlightColor);
		}
	}

	private void renderGameObjectWithHighlight(Graphics2D graphics, TileObject tileObject, Color highlightColor, boolean shouldHighlightTile)
	{
		LocalPoint objectLocalPoint = tileObject.getLocalLocation();

		if (shouldHighlightTile)
		{
			Polygon tilePolygon = Perspective.getCanvasTilePoly(client, objectLocalPoint);
			if (tilePolygon != null)
			{
				OverlayUtil.renderPolygon(graphics, tilePolygon, highlightColor);
			}
		}

		try
		{
			drawTileObjectHull(graphics, tileObject, highlightColor);
		}
		catch (Exception e)
		{
			renderTileHighlightAtWorldPoint(graphics, tileObject.getWorldLocation(), highlightColor);
		}
	}

	private void renderNpcWithHighlight(Graphics2D graphics, NPC npc, Color highlightColor)
	{
		LocalPoint npcLocalPoint = npc.getLocalLocation();
		if (npcLocalPoint == null)
		{
			return;
		}

		Polygon tilePolygon = Perspective.getCanvasTilePoly(client, npcLocalPoint);
		if (tilePolygon != null)
		{
			OverlayUtil.renderPolygon(graphics, tilePolygon, highlightColor);
		}

		modelOutlineRenderer.drawOutline(npc, 2, highlightColor, 4);
	}

	private void drawTileObjectHull(Graphics2D g, TileObject object, Color borderColor)
	{
		Stroke stroke = new BasicStroke(2f);
		Shape poly = null;
		Shape poly2 = null;

		if (object instanceof GameObject)
		{
			poly = ((GameObject) object).getConvexHull();
		}
		else if (object instanceof WallObject)
		{
			poly = ((WallObject) object).getConvexHull();
			poly2 = ((WallObject) object).getConvexHull2();
		}
		else if (object instanceof DecorativeObject)
		{
			poly = ((DecorativeObject) object).getConvexHull();
			poly2 = ((DecorativeObject) object).getConvexHull2();
		}
		else if (object instanceof GroundObject)
		{
			poly = ((GroundObject) object).getConvexHull();
		}

		if (poly == null)
		{
			poly = object.getCanvasTilePoly();
		}

		Color fillColor = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 50);

		if (poly != null)
		{
			OverlayUtil.renderPolygon(g, poly, borderColor, fillColor, stroke);
		}
		if (poly2 != null)
		{
			OverlayUtil.renderPolygon(g, poly2, borderColor, fillColor, stroke);
		}
	}

	private void renderCloudDangerAreaOnGround(Graphics2D graphics, NPC cloudNpc, Color dangerAreaColor)
	{
		CachedConfig cachedConfig = plugin.getCachedConfig();
		LocalPoint cloudCenterPoint = cloudNpc.getLocalLocation();
		if (cloudCenterPoint == null)
		{
			return;
		}

		int dangerRadiusInTiles = cachedConfig.getCloudDangerRadius();

		for (int dx = -dangerRadiusInTiles; dx <= dangerRadiusInTiles; dx++)
		{
			for (int dy = -dangerRadiusInTiles; dy <= dangerRadiusInTiles; dy++)
			{
				boolean isTileWithinCircle = (dx * dx + dy * dy <= dangerRadiusInTiles * dangerRadiusInTiles);
				if (isTileWithinCircle)
				{
					LocalPoint tilePoint = new LocalPoint(
							cloudCenterPoint.getX() + dx * Perspective.LOCAL_TILE_SIZE,
							cloudCenterPoint.getY() + dy * Perspective.LOCAL_TILE_SIZE
					);

					Polygon tilePolygon = Perspective.getCanvasTilePoly(client, tilePoint);
					if (tilePolygon != null)
					{
						Color transparentFillColor = new Color(dangerAreaColor.getRed(), dangerAreaColor.getGreen(), dangerAreaColor.getBlue(), 30);
						graphics.setColor(transparentFillColor);
						graphics.fill(tilePolygon);
						graphics.setColor(dangerAreaColor);
						graphics.draw(tilePolygon);
					}
				}
			}
		}
	}

	private void renderTileHighlightAtWorldPoint(Graphics2D graphics, WorldPoint worldPoint, Color highlightColor)
	{
		renderTileHighlightAtWorldPoint(graphics, worldPoint, highlightColor, null);
	}

	private void renderTileHighlightAtWorldPoint(Graphics2D graphics, WorldPoint worldPoint, Color highlightColor, String label)
	{
		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return;
		}

		LocalPoint tileLocalPoint = RenderingUtils.localPointFromWorldIncludingExtended(topLevelWorldView, worldPoint);
		if (tileLocalPoint == null)
		{
			return;
		}

		Polygon tilePolygon = Perspective.getCanvasTilePoly(client, tileLocalPoint);
		if (tilePolygon != null)
		{
			OverlayUtil.renderPolygon(graphics, tilePolygon, highlightColor);
		}

		if (label != null)
		{
			var labelPoint = Perspective.getCanvasTextLocation(client, graphics, tileLocalPoint, "", 30);
			if (labelPoint != null)
			{
				graphics.setColor(highlightColor);
				graphics.drawString(label, labelPoint.getX(), labelPoint.getY());
			}
		}
	}

	private boolean isRumLocationNextWaypoint(WorldPoint rumLocation)
	{
		List<RouteWaypoint> staticRoute = plugin.getGameState().getCurrentStaticRoute();
		if (staticRoute == null || staticRoute.isEmpty())
		{
			return false;
		}

		int nextWaypointIndex = plugin.getGameState().getNextNavigatableWaypointIndex();
		if (nextWaypointIndex >= staticRoute.size())
		{
			return false;
		}

		RouteWaypoint nextWaypoint = staticRoute.get(nextWaypointIndex);
		WorldPoint nextWaypointLocation = nextWaypoint.getLocation();

		return nextWaypointLocation != null && nextWaypointLocation.equals(rumLocation);
	}
}
