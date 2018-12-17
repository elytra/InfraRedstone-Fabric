package com.elytradev.infraredstone.util.enums;

import net.minecraft.util.StringRepresentable;

public enum CableConnection implements StringRepresentable {
	DISCONNECTED("disconnected"), CONNECTED("connected"), CONNECTED_UP("connected_up");

	private final String name;

	CableConnection(String name) {
		this.name=name;
	}

	@Override
	public String asString() {
		return name;
	}
}
