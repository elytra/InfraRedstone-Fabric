package com.elytradev.infraredstone.item;

import com.elytradev.infraredstone.InfraRedstone;
import net.minecraft.item.Item;

public class ItemBase extends Item {

	protected String name;

	public static Settings DEFAULT_SETTINGS = new Item.Settings().itemGroup(InfraRedstone.inRedGroup);

	public ItemBase(String name, Settings settings) {
		super(settings);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
