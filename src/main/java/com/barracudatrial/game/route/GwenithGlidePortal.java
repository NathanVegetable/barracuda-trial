package com.barracudatrial.game.route;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public class GwenithGlidePortal
{
	private final WorldPoint location;
	private final int objectId;
	private final String clanName;

	public GwenithGlidePortal(
		WorldPoint location,
		int objectId,
		String clanName
	) {
		this.location = location;
		this.objectId = objectId;
		this.clanName = clanName;
	}

	public boolean matchesObjectId(int id)
	{
		return id == objectId;
	}
}
