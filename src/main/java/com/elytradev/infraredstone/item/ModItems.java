package com.elytradev.infraredstone.item;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ModItems {
	public static void init() {

	}

	public static Item register(String name, Item item) {
		Registry.register(Registry.ITEM, "infraredstone:" + name, item);
		return item;
	}
}
