package com.elytradev.infraredstone.util.enums;

import net.minecraft.util.StringRepresentable;

public enum InactiveSelection implements StringRepresentable {
	NONE("none"), LEFT("left"), BACK("back"), RIGHT("right");

	private final String name;

	InactiveSelection(String name) {
		this.name = name;
	}

	public static InactiveSelection forName(String s) {
		for (InactiveSelection value : InactiveSelection.values()) {
			if (s.equals(value.asString())) {
				return value;
			}
		}
		return InactiveSelection.NONE;
	}

	@Override
	public String asString() {
		return name;
	}
}