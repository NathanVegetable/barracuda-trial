package com.barracudatrial.game;

import com.barracudatrial.game.route.GwenithGlideConfig;
import com.barracudatrial.game.route.JubblyJiveConfig;
import com.barracudatrial.game.route.TemporTantrumConfig;
import com.barracudatrial.game.route.TrialConfig;
import com.barracudatrial.game.route.TrialType;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;

/**
 * Handles progress tracking for Barracuda Trial using varbits
 * Detects trial entry/exit and initializes trial-specific configurations
 */
@Slf4j
public class ProgressTracker
{
	private final Client client;
	private final State state;

	public ProgressTracker(Client client, State state)
	{
		this.client = client;
		this.state = state;
	}

	/**
	 * Gets the currently active trial type based on varbits
	 * @return The active trial type, or null if not in a trial
	 */
	public TrialType getCurrentActiveTrialType()
	{
		if (client.getVarbitValue(VarbitID.SAILING_BT_TEMPOR_TANTRUM_MASTER_STATE) == 2)
		{
			return TrialType.TEMPOR_TANTRUM;
		}
		else if (client.getVarbitValue(VarbitID.SAILING_BT_JUBBLY_JIVE_MASTER_STATE) == 2)
		{
			return TrialType.JUBBLY_JIVE;
		}
		else if (client.getVarbitValue(VarbitID.SAILING_BT_GWENITH_GLIDE_MASTER_STATE) == 2)
		{
			return TrialType.GWENITH_GLIDE;
		}
		return null;
	}

	/**
	 * @return true if trial area state changed
	 */
	public boolean checkIfPlayerIsInTrial()
	{
		boolean wasinTrialBefore = state.isInTrial();

		TrialType activeTrialType = getCurrentActiveTrialType();
		boolean isInTrialNow = activeTrialType != null;

		state.setInTrial(isInTrialNow);

		if (!wasinTrialBefore && isInTrialNow)
		{
			log.info("Entered Barracuda Trial: {}", activeTrialType);
			TrialConfig trialConfig = createTrialConfig(activeTrialType);
			state.setCurrentTrial(trialConfig);
			return true;
		}
		else if (wasinTrialBefore && !isInTrialNow)
		{
			log.debug("Left Barracuda Trial");
			state.resetAllTemporaryState();
			return true;
		}

		return false;
	}

	private TrialConfig createTrialConfig(TrialType trialType)
	{
		switch (trialType)
		{
			case TEMPOR_TANTRUM:
				return new TemporTantrumConfig();
			case JUBBLY_JIVE:
				return new JubblyJiveConfig();
			case GWENITH_GLIDE:
				return new GwenithGlideConfig();
			default:
				log.warn("Unknown trial type: {}, using Tempor Tantrum as fallback", trialType);
				return new TemporTantrumConfig();
		}
	}
}
