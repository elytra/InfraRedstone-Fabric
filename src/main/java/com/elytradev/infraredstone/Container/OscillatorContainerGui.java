package com.elytradev.infraredstone.Container;

import com.elytradev.infraredstone.Container.widget.OscillatorButtonWidget;
import com.elytradev.infraredstone.block.entity.OscillatorBlockEntity;
import com.elytradev.infraredstone.util.InfraRedstoneNetworking;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class OscillatorContainerGui extends ContainerGui {

	private static final Identifier TEXTURE = new Identifier("infraredstone:textures/gui/gui_oscillator.png");

	BlockPos pos;
	OscillatorBlockEntity be;

	public OscillatorContainerGui(int id, BlockPos pos, PlayerEntity player) {
		super(new OscillatorContainer(id, pos, player), player.inventory, new TranslatableTextComponent("container.infraredstone.oscillator"));
		this.pos = pos;
		this.be = (OscillatorBlockEntity)player.getEntityWorld().getBlockEntity(pos);
		be.fromTag(player.getEntityWorld().getBlockEntity(pos).toTag(new CompoundTag()));
		this.containerWidth = 138;
		this.containerHeight = 74;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		int topPadded = ((this.height - this.containerHeight) / 2 + 5);
		int leftPadded = ((this.width - this.containerWidth) / 2) + 5;
		this.addButton(new OscillatorButtonWidget(1, leftPadded+72, topPadded+32, "tick_up") {
			@Override
			public void onPressed(double double_1, double double_2) {
				InfraRedstoneNetworking.changeOscillator(be, 1);
			}
		});
		this.addButton(new OscillatorButtonWidget(2, leftPadded+40, topPadded+32, "tick_down") {
			@Override
			public void onPressed(double double_1, double double_2) {
				InfraRedstoneNetworking.changeOscillator(be, -1);
			}
		});
		this.addButton(new OscillatorButtonWidget(3, leftPadded+104, topPadded+32, "second_up") {
			@Override
			public void onPressed(double double_1, double double_2) {
				InfraRedstoneNetworking.changeOscillator(be, 10);
			}
		});
		this.addButton(new OscillatorButtonWidget(4, leftPadded+8, topPadded+32, "second_down") {
			@Override
			public void onPressed(double double_1, double double_2) {
				InfraRedstoneNetworking.changeOscillator(be, -10);
			}
		});
	}

	@Override
	protected void drawBackground(float v, int i, int i1) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(TEXTURE);
		int guiX = (this.width - this.containerWidth) / 2;
		int guiY = (this.height - this.containerHeight) / 2;
		this.drawTexturedRect(guiX, guiY, 0, 0, this.containerWidth, this.containerHeight);
		String text = String.format(""+ TextFormat.DARK_GRAY+"Delay: %s second%s", formatSeconds(be.maxRefreshTicks), (be.maxRefreshTicks != 10)? "s": "");
		fontRenderer.draw(text, (width / 2f) - (fontRenderer.getStringWidth(text) / 2f), (height / 2f) - (fontRenderer.fontHeight * 3f), 1);
	}

	private String formatSeconds(int ticks) {
		if (ticks % 10 == 0) {
			int ret = ticks/10;
			return (""+ret);
		} else {
			double ret = (double)ticks/10;
			return (""+ret);
		}
	}

}
