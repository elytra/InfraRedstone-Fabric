package com.elytradev.infraredstone.logic.impl;

import com.elytradev.infraredstone.api.InfraRedstoneSignal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Direction;

public class InfraRedstoneSerializer{

	public static Tag serialize(InfraRedstoneSignal instance, Direction side) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("SignalValue", instance.getSignalValue());
		tag.putInt("NextSignalValue", ((InfraRedstoneHandler)instance).getNextSignalValue());

		
		return tag;
	}

	public static void deserialize(InfraRedstoneSignal instance, Direction side, Tag nbt) {
		if(instance instanceof InfraRedstoneHandler) {
			if (!(nbt instanceof CompoundTag)) return;
			CompoundTag tag = (CompoundTag)nbt;
			
			((InfraRedstoneHandler)instance).setSignalValue(tag.getInt("SignalValue"));
			((InfraRedstoneHandler)instance).setNextSignalValue(tag.getInt("NextSignalValue"));
		}
	}

}
