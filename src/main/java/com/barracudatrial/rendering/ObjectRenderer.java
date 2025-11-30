package com.barracudatrial.rendering;

import com.barracudatrial.BarracudaTrialPlugin;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Coordinates rendering of objects and objectives in Barracuda Trials
 * Delegates to specialized renderers for different types of objects
 */
public class ObjectRenderer
{
	private final ObjectHighlightRenderer highlightRenderer;
	private final BoatZoneRenderer boatZoneRenderer;

	@Setter
	private Map<Point, Integer> labelCountsByCanvasPosition;

	public ObjectRenderer(Client client, BarracudaTrialPlugin plugin, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.boatZoneRenderer = new BoatZoneRenderer(client, plugin);
		this.highlightRenderer = new ObjectHighlightRenderer(client, plugin, modelOutlineRenderer, boatZoneRenderer);
		this.labelCountsByCanvasPosition = new HashMap<>();
	}

	public void renderLostSupplies(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderLostSupplies(graphics);
	}

	public void renderSpeedBoosts(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderSpeedBoosts(graphics);
	}

	public void renderLightningClouds(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderLightningClouds(graphics);
	}

	public void renderToadPickup(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderToadPickup(graphics);
	}

	public void renderToadPillars(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderToadPillars(graphics);
	}

	public void renderPortals(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderPortals(graphics);
	}

	public void renderRumLocations(Graphics2D graphics)
	{
		highlightRenderer.setLabelCountsByCanvasPosition(labelCountsByCanvasPosition);
		highlightRenderer.renderRumLocations(graphics);
	}
}
