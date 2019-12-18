package com.jamieswhiteshirt.clotheslinefabric.client.raytrace;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;
import com.jamieswhiteshirt.clotheslinefabric.client.LineProjection;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.HitNetworkMessage;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.TryUseItemOnNetworkMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EdgeRaytraceHit extends NetworkRaytraceHit {
    public final double offset;

    public EdgeRaytraceHit(double distanceSq, NetworkEdge edge, double offset) {
        super(distanceSq, edge);
        this.offset = offset;
    }

    @Override
    public boolean hitByEntity(PlayerEntity player) {
        int offset = (int) Math.round(this.offset);
        Network network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        Vec3d pos = edge.getPathEdge().getPositionForOffset(offset);
        player.world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(MessageChannels.HIT_NETWORK.createServerboundPacket(
            new HitNetworkMessage(network.getId(), attachmentKey, offset)
        ));
        return true;
    }

    @Override
    public boolean useItem(PlayerEntity player, Hand hand) {
        int offset = (int) Math.round(this.offset);
        Network network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(MessageChannels.TRY_USE_ITEM_ON_NETWORK.createServerboundPacket(
            new TryUseItemOnNetworkMessage(hand, network.getId(), attachmentKey)
        ));
        return network.useItem(player, hand, attachmentKey);
    }

    @Override
    public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, MatrixStack matrices, VertexConsumer vertices, float tickDelta, double x, double y, double z, float r, float g, float b, float a) {
        renderClotheslineNetwork.renderOutline(matrices, vertices, LineProjection.create(edge), x, y, z, r, g, b, a);
    }

    @Override
    public ItemStack getPickedResult() {
        return new ItemStack(ClotheslineItems.CLOTHESLINE);
    }

    @Override
    public String getDebugString() {
        return "Position: " + Math.round(offset);
    }
}
