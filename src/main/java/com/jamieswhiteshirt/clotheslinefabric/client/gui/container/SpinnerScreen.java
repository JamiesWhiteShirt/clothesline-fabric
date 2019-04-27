package com.jamieswhiteshirt.clotheslinefabric.client.gui.container;

import com.jamieswhiteshirt.clotheslinefabric.common.container.SpinnerContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

public class SpinnerScreen extends ContainerScreen<SpinnerContainer> {
    private static final Identifier BG_TEX = new Identifier("clothesline-fabric", "textures/gui/container/spinner.png");

    public SpinnerScreen(SpinnerContainer container, PlayerInventory playerInventory, TextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(int x, int y, float delta) {
        renderBackground();
        super.render(x, y, delta);
        drawMouseoverTooltip(x, y);
    }

    @Override
    protected void drawForeground(int x, int y) {
        String title = this.title.getFormattedText();
        font.draw(title, containerWidth / 2 - font.getStringWidth(title) / 2, 6.0F, 0x404040);
        font.draw(playerInventory.getDisplayName().getFormattedText(), 8.0F, containerHeight - 96 + 2, 0x404040);
    }

    @Override
    protected void drawBackground(float delta, int x, int y) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(BG_TEX);
        blit(left, top, 0, 0, containerWidth, containerHeight);

        /* int_6 = container.getCookProgress();
        blit(int_3 + 79, int_4 + 34, 176, 14, int_6 + 1, 16); */
    }
}
