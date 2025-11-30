package com.barracudatrial;

import com.barracudatrial.game.route.TrialType;
import com.barracudatrial.rendering.ObjectRenderer;
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
	private final ObjectRenderer objectRenderer;

	@Inject
	public BarracudaTrialOverlay(Client client, BarracudaTrialPlugin plugin, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.plugin = plugin;
		this.objectRenderer = new ObjectRenderer(client, plugin, modelOutlineRenderer);
		this.pathRenderer = new PathRenderer(client, plugin);

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
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
			objectRenderer.renderLostSupplies(graphics);
		}

		var trial = plugin.getGameState().getCurrentTrial();
		if (cachedConfig.isHighlightClouds() && trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM)
		{
			objectRenderer.renderLightningClouds(graphics);
		}

		if (cachedConfig.isHighlightObjectives() && trial != null && trial.getTrialType() == TrialType.JUBBLY_JIVE)
		{
			objectRenderer.renderToadPillars(graphics);
			objectRenderer.renderToadPickup(graphics);
		}

		if (cachedConfig.isHighlightObjectives() && trial != null && trial.getTrialType() == TrialType.GWENITH_GLIDE)
		{
			objectRenderer.renderPortals(graphics);
		}

		if (cachedConfig.isHighlightSpeedBoosts())
		{
			objectRenderer.renderSpeedBoosts(graphics);
		}

		if (cachedConfig.isHighlightObjectives() && trial != null && trial.getTrialType() == TrialType.TEMPOR_TANTRUM)
		{
			objectRenderer.renderRumLocations(graphics);
		}

		return null;
	}
}
