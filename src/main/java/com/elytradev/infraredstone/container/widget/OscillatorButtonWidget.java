package com.elytradev.infraredstone.container.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class OscillatorButtonWidget extends ButtonWidget {

	public Identifier tex;
	private final int u = 0;
	private final int v = 0;
	private final int hoverVOffset = 16;

	public OscillatorButtonWidget(int id, int x, int y, String name) {
		super(id, x, y, 16, 16, name);
		tex = new Identifier("infraredstone:textures/gui/button_"+name+".png");
		this.visible = true;
	}

	@Override
	public void draw(int mouseX, int mouseY, float float_1) {
		if (this.visible) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			MinecraftClient minecraftClient_1 = MinecraftClient.getInstance();
			minecraftClient_1.getTextureManager().bindTexture(this.tex);
			GlStateManager.disableDepthTest();
			int int_3 = this.v;
			if (this.hovered) {
				int_3 += this.hoverVOffset;
			}

			this.drawTexturedRect(this.x, this.y, this.u, int_3, this.width, this.height);
			GlStateManager.enableDepthTest();
		}
	}
}
