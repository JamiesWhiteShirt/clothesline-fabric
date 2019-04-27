package com.jamieswhiteshirt.clotheslinefabric.client;

import com.jamieswhiteshirt.clotheslinefabric.client.gui.container.SpinnerScreen;
import com.jamieswhiteshirt.clotheslinefabric.client.network.ClientMessageHandling;
import com.jamieswhiteshirt.clotheslinefabric.client.render.BakedModels;
import com.jamieswhiteshirt.clotheslinefabric.common.block.entity.SpinnerBlockEntity;
import com.jamieswhiteshirt.clotheslinefabric.common.container.SpinnerContainer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.ContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ClotheslineClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientMessageHandling.init();
        BakedModels.init();

        ScreenProviderRegistry.INSTANCE.registerFactory(SpinnerContainer.GUI_ID, (syncId, identifier, player, buf) -> {
            BlockPos pos = buf.readBlockPos();
            BlockEntity blockEntity = player.world.getBlockEntity(pos);
            if (blockEntity instanceof SpinnerBlockEntity) {
                SpinnerBlockEntity spinner = (SpinnerBlockEntity) blockEntity;
                SpinnerContainer container = (SpinnerContainer) spinner.createMenu(syncId, player.inventory, player);
                return new SpinnerScreen(container, player.inventory, spinner.getDisplayName());
            }
            return null;
        });
    }
}
