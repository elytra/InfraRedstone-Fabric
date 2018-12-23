package com.elytradev.infraredstone.item;

import com.elytradev.infraredstone.InfraRedstone;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ModItems {

	public static final Item PCB = register(new ItemBase("pcb", ItemBase.DEFAULT_SETTINGS));
	public static final Item MULTIMETER = register(new MultimeterItem());

	public static void init() {

	}

	public static Item register(String name, Item item) {
		Registry.register(Registry.ITEM, "infraredstone:" + name, item);
		return item;
	}

	public static Item register(ItemBase item) {
		Registry.register(Registry.ITEM, "infraredstone:" + item.getName(), item);
		return item;
	}
}
