package com.jamieswhiteshirt.clothesline.client.render;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.client.EdgeAttachmentTransformations;
import com.jamieswhiteshirt.clothesline.client.LineProjection;
import com.jamieswhiteshirt.clothesline.client.Transformation;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clothesline.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import com.jamieswhiteshirt.rtree3i.Selection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class ClotheslineRenderer {
    private static final double DEBUG_VISIBLE_RANGE = 10.0F;
    private static final float[] EDGE_VERTEX_X = new float[] { -5.0F / 32.0F, -5.0F / 32.0F, -3.0F / 32.0F, -3.0F / 32.0F };
    private static final float[] EDGE_VERTEX_Y = new float[] { -1.0F / 32.0F, 1.0F / 32.0F, 1.0F / 32.0F, -1.0F / 32.0F };
    private static final float[] EDGE_NORMAL_X = new float[] { -1.0F, 0.0F, 1.0F, 0.0F };
    private static final float[] EDGE_NORMAL_Y = new float[] { 0.0F, 1.0F, 0.0F, -1.0F };

    private final MinecraftClient client;

    public ClotheslineRenderer(MinecraftClient client) {
        this.client = client;
    }

    private static VertexConsumer posNormal(VertexConsumer vertices, Vector4f pos, Vector3f normal) {
        return vertices.vertex(pos.getX(), pos.getY(), pos.getZ()).normal(normal.getX(), normal.getY(), normal.getZ());
    }

    public void renderEdge(MatrixStack.Entry matrices, VertexConsumer vertices, float fromOffset, float toOffset, int lightFrom, int lightTo) {
        float vFrom = fromOffset / AttachmentUnit.UNITS_PER_BLOCK;
        float vTo = toOffset / AttachmentUnit.UNITS_PER_BLOCK;
        float length = vTo - vFrom;

        Vector3f normal = new Vector3f();
        Vector4f pos = new Vector4f();

        for (int side = 0; side < 4; side++) {
            float x1 = EDGE_VERTEX_X[side];
            float x2 = EDGE_VERTEX_X[(side + 1) & 3];
            float y1 = EDGE_VERTEX_Y[side];
            float y2 = EDGE_VERTEX_Y[(side + 1) & 3];
            float nx = EDGE_NORMAL_X[side];
            float ny = EDGE_NORMAL_Y[side];

            float uFrom = (4.0F - side) / 4.0F;
            float uTo = (3.0F - side) / 4.0F;

            normal.set(nx, ny, 0.0F);
            normal.transform(matrices.getNormal());

            pos.set(x1, y1, 0.0F, 1.0F);
            pos.transform(matrices.getModel());
            posNormal(vertices, pos, normal).texture(uFrom, vFrom).light(lightFrom).next();
            pos.set(x2, y2, 0.0F, 1.0F);
            pos.transform(matrices.getModel());
            posNormal(vertices, pos, normal).texture(uTo, vFrom).light(lightFrom).next();
            pos.set(x2, y2, length, 1.0F);
            pos.transform(matrices.getModel());
            posNormal(vertices, pos, normal).texture(uTo, vTo).light(lightTo).next();
            pos.set(x1, y1, length, 1.0F);
            pos.transform(matrices.getModel());
            posNormal(vertices, pos, normal).texture(uFrom, vTo).light(lightTo).next();
        }
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockRenderView world, RTreeMap<BlockPos, NetworkNode> nodesMap, RTreeMap<Line, NetworkEdge> edgesMap, Frustum frustum, float tickDelta) {
        // Select all entries in the node map intersecting with the camera frustum
        Selection<NetworkNode> nodes = nodesMap
            .values(box -> frustum.isVisible(new Box(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));
        VertexConsumer anchorVertices = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());

        nodes.forEach(node -> {
            BlockPos pos = node.getPathNode().getPos();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != ClotheslineBlocks.CLOTHESLINE_ANCHOR) return;

            int light = WorldRenderer.getLightmapCoordinates(world, pos);

            Network network = node.getNetwork();
            float shift = network.getState().getShift(tickDelta);
            float crankRotation = -(node.getPathNode().getBaseRotation() + shift) * 360.0F / AttachmentUnit.UNITS_PER_BLOCK;

            matrices.push();
            matrices.translate(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            if (state.get(ClotheslineAnchorBlock.FACE) == WallMountLocation.CEILING) {
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                crankRotation = -crankRotation;
            }
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(crankRotation));

            matrices.push();
            matrices.scale(2.0F, 2.0F, 2.0F);
            ItemModelRenderer.renderModel(BakedModels.pulleyWheel, ModelTransformation.Mode.FIXED, matrices, anchorVertices, light, OverlayTexture.DEFAULT_UV);
            if (!node.getNetwork().getState().getTree().isEmpty()) {
                ItemModelRenderer.renderModel(BakedModels.pulleyWheelRope, ModelTransformation.Mode.FIXED, matrices, anchorVertices, light, OverlayTexture.DEFAULT_UV);
            }
            matrices.pop();

            if (state.get(ClotheslineAnchorBlock.CRANK)) {
                matrices.push();
                matrices.translate(0.0F, 4.0F / 16.0F, 0.0F);
                ItemModelRenderer.renderModel(BakedModels.crank, ModelTransformation.Mode.FIXED, matrices, anchorVertices, light, OverlayTexture.DEFAULT_UV);
                matrices.pop();
            }

            matrices.pop();
        });

        // Select all entries in the edge map intersecting with the camera frustum
        Selection<NetworkEdge> edges = edgesMap
            .values(box -> frustum.isVisible(new Box(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        // World position of attachment item
        Vector4f wPos = new Vector4f();

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            Line line = pathEdge.getLine();
            NetworkState state = edge.getNetwork().getState();
            int lightFrom = WorldRenderer.getLightmapCoordinates(world, line.getFromPos());
            int lightTo = WorldRenderer.getLightmapCoordinates(world, line.getToPos());
            float shift = state.getShift(tickDelta);
            LineProjection p = LineProjection.create(line.getFromVec(), line.getToVec());

            // Render rope
            matrices.push();
            p.getTransformation().apply(matrices);
            VertexConsumer clotheslineVertices = vertexConsumers.getBuffer(ClotheslineRenderLayers.getClothesline());
            renderEdge(matrices.peek(), clotheslineVertices, pathEdge.getFromOffset() - shift, pathEdge.getToOffset() - shift, lightFrom, lightTo);
            matrices.pop();

            float fromAttachmentKey = state.offsetToAttachmentKey(pathEdge.getFromOffset(), tickDelta);
            float toAttachmentKey = fromAttachmentKey + pathEdge.getLength();

            List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange(MathHelper.floor(fromAttachmentKey), MathHelper.floor(toAttachmentKey));
            if (!attachments.isEmpty()) {
                EdgeAttachmentTransformations transformations = EdgeAttachmentTransformations.build(edge, p);

                for (MutableSortedIntMap.Entry<ItemStack> attachmentEntry : attachments) {
                    float attachmentOffset = state.attachmentKeyToOffset(attachmentEntry.getKey(), tickDelta);
                    // Local space to world space transformation
                    Transformation l2w = transformations.getL2WForAttachment(state.getMomentum(tickDelta), attachmentOffset);

                    // Create world position of attachment for lighting calculation
                    wPos.set(0.0F, 0.0F, 0.0F, 1.0F);
                    wPos.transform(l2w.getModel());
                    BlockPos pos = new BlockPos(wPos.getX(), wPos.getY(), wPos.getZ());
                    int light = WorldRenderer.getLightmapCoordinates(world, pos);

                    matrices.push();
                    l2w.apply(matrices);
                    client.getItemRenderer().renderItem(attachmentEntry.getValue(), ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
                    matrices.pop();
                }
            }
        });
    }

    public void renderOutline(MatrixStack matrices, VertexConsumer vertices, NetworkEdge edge, float r, float g, float b, float a) {
        matrices.push();
        LineProjection.create(edge).getTransformation().apply(matrices);
        for (int side = 0; side < 4; side++) {
            float x = EDGE_VERTEX_X[side];
            float y = EDGE_VERTEX_Y[side];

            vertices.vertex(matrices.peek().getModel(), x, y, 0.0F).color(r, g, b, a).next();
            vertices.vertex(matrices.peek().getModel(), x, y, (float) edge.getPathEdge().getLength() / AttachmentUnit.UNITS_PER_BLOCK).color(r, g, b, a).next();
        }
        matrices.pop();
    }

    private void renderDebugText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, String msg, Vec3d pos, Quaternion rotation, TextRenderer textRenderer) {
        matrices.push();
        matrices.translate(pos.x, pos.y, pos.z);
        matrices.multiply(rotation);
        matrices.scale(-0.025F, -0.025F, 0.025F);

        float x = -textRenderer.getWidth(msg) / 2.0F;
        float y = 0.0F;
        float textBackgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
        int color = 0x20FFFFFF;
        int backgroundColor = (int)(textBackgroundOpacity * 255.0F) << 24;
        int light = 0x00F000F0;
        textRenderer.draw(msg, x, y, color, false, matrices.peek().getModel(), vertexConsumers, false, backgroundColor, light);

        matrices.pop();
    }

    public void debugRender(
        MatrixStack matrices, VertexConsumerProvider vertexConsumers,
        RTreeMap<BlockPos, NetworkNode> nodesMap,
        RTreeMap<Line, NetworkEdge> edgesMap,
        Frustum frustum, Camera camera
    ) {
        TextRenderer textRenderer = client.textRenderer;

        // Select all edges in the edges map intersecting with the camera frustum
        Selection<NetworkEdge> edges = edgesMap
            .values(box -> frustum.isVisible(new Box(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            Vec3d pos = LineProjection.create(edge).projectRUF(-0.125F, 0.125F, 0.5F * pathEdge.getLength() / AttachmentUnit.UNITS_PER_BLOCK);
            if (camera.getPos().squaredDistanceTo(pos) <= DEBUG_VISIBLE_RANGE * DEBUG_VISIBLE_RANGE) {
                BlockPos nodePos = pathEdge.getLine().getFromPos();
                NetworkNode node = nodesMap.get(nodePos);
                Path.Node pathNode = node.getPathNode();
                int nodeIndex = pathNode.getEdges().indexOf(pathEdge);
                renderDebugText(matrices, vertexConsumers, "L" + nodeIndex + " G" + edge.getIndex(), pos, camera.getRotation(), textRenderer);
            }
        });
    }

    public void renderFirstPersonPlayerHeldClothesline(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, BlockPos fromPos, float tickDelta) {
        double posX = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        double posY = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        double posZ = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());
        float pitch = (float) Math.toRadians(MathHelper.lerp(tickDelta, player.prevPitch, player.pitch));
        float yaw = (float) Math.toRadians(MathHelper.lerp(tickDelta, player.prevYaw, player.yaw));
        int handedOffset = (player.getMainArm() == Arm.RIGHT ? 1 : -1) * (player.getActiveHand() == Hand.MAIN_HAND ? 1 : -1);
        double fovModifier = client.options.fov / 100.0D;
        Vec3d vecB = new Vec3d(posX, posY + player.getStandingEyeHeight(), posZ).add(new Vec3d(handedOffset * -0.36D * fovModifier, -0.045D * fovModifier, 0.4D).rotateX(-pitch).rotateY(-yaw));

        renderHeldClothesline(matrices, vertexConsumers, fromPos, vecB, player.world);
    }

    public void renderThirdPersonPlayerHeldClothesline(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, BlockPos fromPos, float tickDelta) {
        double posX = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        double posY = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        double posZ = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());
        float yaw = (float) Math.toRadians(MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw));
        int handedOffset = (player.getMainArm() == Arm.RIGHT ? 1 : -1) * (player.getActiveHand() == Hand.MAIN_HAND ? 1 : -1);
        double d0 = MathHelper.sin(yaw) * 0.35D;
        double d1 = MathHelper.cos(yaw) * 0.35D;
        Vec3d vecB = new Vec3d(
            posX - d0 - d1 * handedOffset,
            posY + (player.isSneaking() ? 0.4D : 0.9D),
            posZ - d0 * handedOffset + d1
        );

        matrices.push();
        matrices.translate(-posX, -posY, -posZ);
        renderHeldClothesline(matrices, vertexConsumers, fromPos, vecB, player.world);
        matrices.pop();
    }

    private void renderHeldClothesline(MatrixStack matrices, VertexConsumerProvider vertexConsumers, BlockPos posA, Vec3d vecB, BlockRenderView world) {
        Vec3d vecA = Utility.midVec(posA);
        BlockPos posB = new BlockPos(vecB);
        int lightA = WorldRenderer.getLightmapCoordinates(world, posA);
        int lightB = WorldRenderer.getLightmapCoordinates(world, posB);
        float length = AttachmentUnit.UNITS_PER_BLOCK * (float) vecB.distanceTo(vecA);
        VertexConsumer vertices = vertexConsumers.getBuffer(ClotheslineRenderLayers.getClothesline());

        matrices.push();
        LineProjection.createTransformation(vecA, vecB).apply(matrices);
        renderEdge(matrices.peek(), vertices, 0.0F, length, lightA, lightB);
        matrices.pop();

        matrices.push();
        LineProjection.createTransformation(vecB, vecA).apply(matrices);
        renderEdge(matrices.peek(), vertices, -length, 0.0F, lightB, lightA);
        matrices.pop();
    }
}
