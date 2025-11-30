package com.barracudatrial;

import com.barracudatrial.game.route.TrialType;
import com.barracudatrial.rendering.BoatZoneRenderer;
import com.barracudatrial.rendering.ObjectHighlightRenderer;
import com.barracudatrial.rendering.PathRenderer;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class BarracudaTrialOverlay extends Overlay
{
	private final BarracudaTrialPlugin plugin;
	private final PathRenderer pathRenderer;
	private final ObjectHighlightRenderer highlightRenderer;

	@Inject
	public BarracudaTrialOverlay(Client client, BarracudaTrialPlugin plugin, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.plugin = plugin;
		var boatZoneRenderer = new BoatZoneRenderer(client, plugin);
		this.highlightRenderer = new ObjectHighlightRenderer(client, plugin, modelOutlineRenderer, boatZoneRenderer);
		this.pathRenderer = new PathRenderer(client, plugin);

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(PRIORITY_MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.getGameState().isInTrialArea())
		{
			return null;
		}

		CachedConfig cachedConfig = plugin.getCachedConfig();

		if (cachedConfig.isShowOptimalPath())
		{
			pathRenderer.renderOptimalPath(graphics);
		}

		if (cachedConfig.isHighlightObjectives())
		{
			highlightRenderer.renderLostSupplies(graphics);
		}

		var trial = plugin.getGameState().getCurrentTrial();
		if (cachedConfig.isHighlightClouds() && trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM)
		{
			highlightRenderer.renderLightningClouds(graphics);
		}

		if (cachedConfig.isHighlightObjectives() && trial != null && trial.getTrialType() == TrialType.JUBBLY_JIVE)
		{
			highlightRenderer.renderToadPillars(graphics);
			highlightRenderer.renderToadPickup(graphics);
		}

		if (cachedConfig.isHighlightObjectives() && trial != null && trial.getTrialType() == TrialType.GWENITH_GLIDE)
		{
			highlightRenderer.renderPortals(graphics);
		}

		if (cachedConfig.isHighlightSpeedBoosts())
		{
			highlightRenderer.renderSpeedBoosts(graphics);
		}

		if (cachedConfig.isHighlightObjectives() && trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM)
		{
			highlightRenderer.renderRumLocations(graphics);
		}

		return null;
	}
}
