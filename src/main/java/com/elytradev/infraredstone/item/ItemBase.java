package com.elytradev.infraredstone.item;

import com.elytradev.infraredstone.InfraRedstone;
import net.minecraft.item.Item;

public class ItemBase extends Item {

	protected String name;

	public ItemBase(String name) {
		super(new Item.Settings().itemGroup(InfraRedstone.inRedGroup));
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
