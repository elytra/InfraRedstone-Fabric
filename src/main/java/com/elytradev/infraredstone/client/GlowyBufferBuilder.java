package com.elytradev.infraredstone.client;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;

import java.nio.IntBuffer;

public class GlowyBufferBuilder {

	private BufferBuilder buf;

	public GlowyBufferBuilder(BufferBuilder buf) {
		this.buf = buf;
	}

	public BufferBuilder lightmap(BufferBuilder buf, int var1, int var2, int var3, int var4) {
		buf.brightness(var1, var2, var3, var4);
		return buf;
	}
}
