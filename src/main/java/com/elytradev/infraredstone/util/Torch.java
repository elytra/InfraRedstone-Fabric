package com.elytradev.infraredstone.util;

public class Torch {
	//the leftmost/eastmost corner of the torch to display, in bits (sixteenths of a block)
	public double cornerX;
	//the topmost/northmost corner of the torch to display, in bits (sixteenths of a block)
	public double cornerZ;
	//if true, the torch will be two bits tall; if false, one bit tall
	public boolean isFullHeight;
	//if true, torch will have glowing, lit texture
	public boolean isLit;

	public Torch(double cornerX, double cornerZ, boolean isFullHeight, boolean isLit) {
		this.cornerX = cornerX;
		this.cornerZ = cornerZ;
		this.isFullHeight = isFullHeight;
		this.isLit = isLit;
	}
}
