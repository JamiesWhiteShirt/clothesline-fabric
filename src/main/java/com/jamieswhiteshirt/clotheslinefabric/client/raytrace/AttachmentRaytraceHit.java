package com.jamieswhiteshirt.clotheslinefabric.client.raytrace;

import com.jamieswhiteshirt.clotheslinefabric.api.Network;
import com.jamieswhiteshirt.clotheslinefabric.api.NetworkEdge;
import com.jamieswhiteshirt.clotheslinefabric.client.Transformation;
import com.jamieswhiteshirt.clotheslinefabric.client.render.ClotheslineRenderer;
import com.jamieswhiteshirt.clotheslinefabric.common.network.MessageChannels;
import com.jamieswhiteshirt.clotheslinefabric.common.network.message.HitAttachmentMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class AttachmentRaytraceHit extends NetworkRaytraceHit {
    public final int attachmentKey;
    private final Transformation l2wTransformation;

    public AttachmentRaytraceHit(double distanceSq, NetworkEdge edge, int attachmentKey, Transformation l2wTransformation) {
        super(distanceSq, edge);
        this.attachmentKey = attachmentKey;
        this.l2wTransformation = l2wTransformation;
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
    public ActionResult useItem(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public void renderHighlight(ClotheslineRenderer clotheslineRenderer, MatrixStack matrices, VertexConsumer vertices, float r, float g, float b, float a) {
        matrices.push();
        l2wTransformation.apply(matrices);
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
