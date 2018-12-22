package com.elytradev.infraredstone.util.enums;

import net.minecraft.util.StringRepresentable;

public enum ShifterSelection implements StringRepresentable {
	LEFT("left"), RIGHT("right");

	private final String name;

	ShifterSelection(String name) {
		this.name = name;
	}

	public static ShifterSelection forName(String s) {
		for (ShifterSelection value : ShifterSelection.values()) {
			if (s.equals(value.asString())) {
				return value;
			}
		}
		return ShifterSelection.LEFT;
	}

	@Override
	public String asString() {
		return name;
	}
}
