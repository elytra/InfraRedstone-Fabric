package com.elytradev.infraredstone.logic.impl;

import com.elytradev.infraredstone.api.EncoderScannable;
import com.elytradev.infraredstone.api.InfraRedstoneSignal;
import net.minecraft.util.math.Direction;

public class InfraRedstoneHandler implements InfraRedstoneSignal, EncoderScannable {
	public static final InfraRedstoneSignal ALWAYS_OFF = () -> 0;
	
	public static final InfraRedstoneSignal ALWAYS_MAX = () -> 63;
	
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
	public int getEncoderValue(Direction dir) {
		return signalValue;
	}
	
	public void onChanged() {
		if (onChanged!=null) onChanged.run();
	}
}
