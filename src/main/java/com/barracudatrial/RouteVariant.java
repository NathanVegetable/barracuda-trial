package com.barracudatrial;

/**
 * Which route to follow when a trial has more than one for the current difficulty.
 * Trials without a route for the chosen variant fall back to STANDARD.
 */
public enum RouteVariant
{
	STANDARD,
	FASTER
}
