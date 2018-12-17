package com.elytradev.infraredstone.logic.impl;

import com.elytradev.infraredstone.api.IEncoderScannable;
import com.elytradev.infraredstone.api.IInfraRedstone;
import net.minecraft.util.math.Direction;

public class InfraRedstoneHandler implements IInfraRedstone, IEncoderScannable {
	public static final IInfraRedstone ALWAYS_OFF = () -> 0;
	
	public static final IInfraRedstone ALWAYS_MAX = () -> 63;
	
	private int signalValue;
	private int nextSignalValue;
	private Runnable onChanged;
	
	public void listen(Runnable r) {
		this.onChanged = r;
	}
	
	@Override
	public int getSignalValue() {
		return signalValue;
	}

	public void setSignalValue(int val) {
		signalValue = val;
		onChanged();
	}
	
	public int getNextSignalValue() {
		return nextSignalValue;
	}
	
	public void setNextSignalValue(int val) {
		nextSignalValue = val;
		onChanged();
	}

	@Override
	public int getComparatorValue(Direction dir) {
		return signalValue;
	}
	
	public void onChanged() {
		if (onChanged!=null) onChanged.run();
	}
}
