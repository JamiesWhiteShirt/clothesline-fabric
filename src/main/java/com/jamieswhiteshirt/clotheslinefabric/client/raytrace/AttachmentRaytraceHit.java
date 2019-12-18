package com.jamieswhiteshirt.clotheslinefabric.client.raytrace;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;
import com.jamieswhiteshirt.clotheslinefabric.client.Mat4f;
import com.jamieswhiteshirt.clotheslinefabric.client.render.RenderClotheslineNetwork;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.HitAttachmentMessage;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.nio.FloatBuffer;

@Environment(EnvType.CLIENT)
public class AttachmentRaytraceHit extends NetworkRaytraceHit {
    private static final VoxelShape attachmentBox = VoxelShapes.cuboid(new Box(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D));
    private static final FloatBuffer l2wBuffer = GlAllocationUtils.allocateFloatBuffer(16);
    public final int attachmentKey;
    private final Mat4f l2w;

    public AttachmentRaytraceHit(double distanceSq, NetworkEdge edge, int attachmentKey, Mat4f l2w) {
        super(distanceSq, edge);
        this.attachmentKey = attachmentKey;
        this.l2w = l2w;
    }

    @Override
    public boolean hitByEntity(PlayerEntity player) {
        Network network = edge.getNetwork();
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(MessageChannels.HIT_ATTACHMENT.createServerboundPacket(
            new HitAttachmentMessage(network.getId(), attachmentKey)
        ));
        network.hitAttachment(player, attachmentKey);
        return true;
    }

    @Override
    public boolean useItem(PlayerEntity player, Hand hand) {
        return false;
    }

    @Override
    public void renderHighlight(RenderClotheslineNetwork renderClotheslineNetwork, MatrixStack matrices, VertexConsumer vertices, float tickDelta, double x, double y, double z, float r, float g, float b, float a) {
        l2w.putIntoBuffer(l2wBuffer); // store in buffer
        l2wBuffer.flip();

        matrices.push();
        matrices.translate(-x, -y, -z);
        // TODO: How?
        GlStateManager.multMatrix(l2wBuffer);
        WorldRenderer.drawBox(matrices, vertices, -0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D, r, g, b, a);
        matrices.pop();
    }

    @Override
    public ItemStack getPickedResult() {
        return edge.getNetwork().getAttachment(attachmentKey);
    }

    @Override
    public String getDebugString() {
        return "Attachment: " + attachmentKey;
    }
}
