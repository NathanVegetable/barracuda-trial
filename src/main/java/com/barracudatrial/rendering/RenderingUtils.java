package com.barracudatrial.rendering;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;

/**
 * Static utility methods for rendering and world/scene coordinate conversion
 */
public class RenderingUtils
{
	public static GameObject findGameObjectAtWorldPoint(Client client, WorldPoint worldPoint)
	{
		return findGameObjectAtWorldPoint(client, worldPoint, null);
	}

	public static GameObject findGameObjectAtWorldPoint(Client client, WorldPoint worldPoint, Integer objectId)
	{
		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return null;
		}

		Scene scene = topLevelWorldView.getScene();
		if (scene == null)
		{
			return null;
		}

		LocalPoint localPoint = localPointFromWorldIncludingExtended(topLevelWorldView, worldPoint);
		if (localPoint == null)
		{
			return null;
		}

		Tile tile = getTileFromSceneOrExtended(worldPoint, localPoint, scene);

		if (tile == null)
		{
			return null;
		}

		for (GameObject gameObject : tile.getGameObjects())
		{
			if (gameObject != null)
			{
				if (objectId != null && gameObject.getId() != objectId)
				{
					continue;
				}
				return gameObject;
			}
		}

		return null;
	}

	public static Tile getTileFromSceneOrExtended(WorldPoint worldPoint, LocalPoint localPoint, Scene scene)
	{
		int sceneX = localPoint.getSceneX();
		int sceneY = localPoint.getSceneY();
		var plane = worldPoint.getPlane();
		var tiles = scene.getTiles();

		if (tiles != null &&
				plane >= 0 && plane < tiles.length &&
				sceneX >= 0 && sceneX < tiles[plane].length &&
				sceneY >= 0 && sceneY < tiles[plane][sceneX].length)
		{
			return tiles[plane][sceneX][sceneY];
		}
		else
		{
			var ext = scene.getExtendedTiles();
			if (ext != null &&
					plane >= 0 && plane < ext.length &&
					sceneX >= 0 && sceneX < ext[plane].length &&
					sceneY >= 0 && sceneY < ext[plane][sceneX].length)
			{
				return ext[plane][sceneX][sceneY];
			}
		}

		return null;
	}

	/**
	 * Creates a LocalPoint from a WorldPoint, including support for extended tiles.
	 * LocalPoint.fromWorld() only works for the normal scene, not extended tiles.
	 * This manually creates LocalPoints for extended tiles by calculating scene coordinates.
	 */
	public static LocalPoint localPointFromWorldIncludingExtended(WorldView view, WorldPoint point)
	{
		if (view == null || point == null)
		{
			return null;
		}

		if (view.getPlane() != point.getPlane())
		{
			return null;
		}

		// Try normal method first (works for regular scene)
		LocalPoint normalPoint = LocalPoint.fromWorld(view, point);
		if (normalPoint != null)
		{
			return normalPoint;
		}

		// For extended tiles, manually create LocalPoint from scene coordinates
		int baseX = view.getBaseX();
		int baseY = view.getBaseY();
		int sceneX = point.getX() - baseX;
		int sceneY = point.getY() - baseY;

		// Extended tiles go up to around 192x192, check if within reasonable bounds
		if (sceneX >= -50 && sceneX < 200 && sceneY >= -50 && sceneY < 200)
		{
			return LocalPoint.fromScene(sceneX, sceneY, view);
		}

		return null;
	}

	/**
	 * Renders a tile highlight at a world point with optional label
	 */
	public static void renderTileHighlightAtWorldPoint(Client client, Graphics2D graphics, WorldPoint worldPoint, Color highlightColor, String label)
	{
		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return;
		}

		LocalPoint tileLocalPoint = localPointFromWorldIncludingExtended(topLevelWorldView, worldPoint);
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
			Point labelPoint = Perspective.getCanvasTextLocation(client, graphics, tileLocalPoint, "", 30);
			if (labelPoint != null)
			{
				graphics.setColor(highlightColor);
				graphics.drawString(label, labelPoint.getX(), labelPoint.getY());
			}
		}
	}
}
