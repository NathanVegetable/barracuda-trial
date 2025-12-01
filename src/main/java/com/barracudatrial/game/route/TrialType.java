package com.barracudatrial.game.route;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrialType
{
	TEMPOR_TANTRUM("Tempor Tantrum"),
	JUBBLY_JIVE("Jubbly Jive"),
	GWENITH_GLIDE("Gwenith Glide");

	private final String displayName;
}
