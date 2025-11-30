package com.barracudatrial.rendering;

import com.barracudatrial.BarracudaTrialPlugin;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import java.awt.*;

/**
 * Coordinates rendering of objects and objectives in Barracuda Trials
 * Delegates to specialized renderers for different types of objects
 */
public class ObjectRenderer
{
	private final ObjectHighlightRenderer highlightRenderer;
	private final BoatZoneRenderer boatZoneRenderer;

	public ObjectRenderer(Client client, BarracudaTrialPlugin plugin, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.boatZoneRenderer = new BoatZoneRenderer(client, plugin);
		this.highlightRenderer = new ObjectHighlightRenderer(client, plugin, modelOutlineRenderer, boatZoneRenderer);
	}

	public void renderLostSupplies(Graphics2D graphics)
	{
		highlightRenderer.renderLostSupplies(graphics);
	}

	public void renderSpeedBoosts(Graphics2D graphics)
	{
		highlightRenderer.renderSpeedBoosts(graphics);
	}

	public void renderLightningClouds(Graphics2D graphics)
	{
		highlightRenderer.renderLightningClouds(graphics);
	}

	public void renderToadPickup(Graphics2D graphics)
	{
		highlightRenderer.renderToadPickup(graphics);
	}

	public void renderToadPillars(Graphics2D graphics)
	{
		highlightRenderer.renderToadPillars(graphics);
	}

	public void renderPortals(Graphics2D graphics)
	{
		highlightRenderer.renderPortals(graphics);
	}

	public void renderRumLocations(Graphics2D graphics)
	{
		highlightRenderer.renderRumLocations(graphics);
	}
}
