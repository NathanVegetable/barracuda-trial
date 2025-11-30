package com.barracudatrial.rendering;

import com.barracudatrial.BarracudaTrialPlugin;
import com.barracudatrial.game.route.JubblyJiveConfig;
import com.barracudatrial.game.route.TemporTantrumConfig;
import com.barracudatrial.game.route.TrialType;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;

/**
 * Handles rendering of boat exclusion zones (rectangles around objectives)
 */
@RequiredArgsConstructor
public class BoatZoneRenderer
{
	private final Client client;
	private final BarracudaTrialPlugin plugin;

	public void renderBoatZoneRectangle(Graphics2D graphics, WorldPoint center, Color baseColor)
	{
		WorldView topLevelWorldView = client.getTopLevelWorldView();
		if (topLevelWorldView == null)
		{
			return;
		}

		var trial = plugin.getGameState().getCurrentTrial();
		if (trial == null)
		{
			return;
		}

		int width;
		int height;
		if (trial.getTrialType() == TrialType.TEMPOR_TANTRUM)
		{
			width = TemporTantrumConfig.BOAT_EXCLUSION_WIDTH;
			height = TemporTantrumConfig.BOAT_EXCLUSION_HEIGHT;
		}
		else if (trial.getTrialType() == TrialType.JUBBLY_JIVE)
		{
			width = JubblyJiveConfig.BOAT_EXCLUSION_WIDTH;
			height = JubblyJiveConfig.BOAT_EXCLUSION_HEIGHT;
		}
		else
		{
			return;
		}

		int halfWidth = width / 2;
		int halfHeight = height / 2;

		int minX = center.getX() - halfWidth;
		int maxX = center.getX() + halfWidth;
		int minY = center.getY() - halfHeight;
		int maxY = center.getY() + halfHeight;

		Color fillColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 150);

		Polygon rectangleBoundary = buildPerimeterPolygon(topLevelWorldView, minX, maxX, minY, maxY);

		if (rectangleBoundary.npoints > 0)
		{
			OverlayUtil.renderPolygon(graphics, rectangleBoundary, fillColor);
		}
	}

	private Polygon buildPerimeterPolygon(WorldView worldView, int minX, int maxX, int minY, int maxY)
	{
		Polygon boundary = new Polygon();

		// Bottom edge (south)
		for (int x = minX; x <= maxX; x++)
		{
			WorldPoint tile = new WorldPoint(x, minY, 0);
			LocalPoint local = RenderingUtils.localPointFromWorldIncludingExtended(worldView, tile);
			if (local != null)
			{
				Polygon tilePoly = Perspective.getCanvasTilePoly(client, local);
				if (tilePoly != null && tilePoly.npoints >= 4)
				{
					if (x == minX)
					{
						boundary.addPoint(tilePoly.xpoints[0], tilePoly.ypoints[0]); // SW corner
					}
					if (x == maxX)
					{
						boundary.addPoint(tilePoly.xpoints[1], tilePoly.ypoints[1]); // SE corner
					}
				}
			}
		}

		// Right edge (east)
		for (int y = minY; y <= maxY; y++)
		{
			WorldPoint tile = new WorldPoint(maxX, y, 0);
			LocalPoint local = RenderingUtils.localPointFromWorldIncludingExtended(worldView, tile);
			if (local != null)
			{
				Polygon tilePoly = Perspective.getCanvasTilePoly(client, local);
				if (tilePoly != null && tilePoly.npoints >= 4)
				{
					if (y == maxY)
					{
						boundary.addPoint(tilePoly.xpoints[2], tilePoly.ypoints[2]); // NE corner
					}
				}
			}
		}

		// Top edge (north)
		for (int x = maxX; x >= minX; x--)
		{
			WorldPoint tile = new WorldPoint(x, maxY, 0);
			LocalPoint local = RenderingUtils.localPointFromWorldIncludingExtended(worldView, tile);
			if (local != null)
			{
				Polygon tilePoly = Perspective.getCanvasTilePoly(client, local);
				if (tilePoly != null && tilePoly.npoints >= 4)
				{
					if (x == minX)
					{
						boundary.addPoint(tilePoly.xpoints[3], tilePoly.ypoints[3]); // NW corner
					}
				}
			}
		}

		return boundary;
	}
}
