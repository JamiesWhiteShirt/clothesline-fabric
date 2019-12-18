package com.jamieswhiteshirt.clotheslinefabric.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clotheslinefabric.client.EdgeAttachmentProjector;
import com.jamieswhiteshirt.clotheslinefabric.client.LineProjection;
import com.jamieswhiteshirt.clotheslinefabric.client.Mat4f;
import com.jamieswhiteshirt.clotheslinefabric.client.Vec4f;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineAnchorBlock;
import com.jamieswhiteshirt.clotheslinefabric.common.block.ClotheslineBlocks;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import com.jamieswhiteshirt.rtree3i.Selection;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class RenderClotheslineNetwork {
    private static final Identifier TEXTURE = new Identifier("clothesline-fabric", "textures/misc/clothesline.png");
    private static final VertexFormat VERTEX_FORMAT = new VertexFormat()
        .add(VertexFormats.POSITION_ELEMENT)
        .add(VertexFormats.NORMAL_ELEMENT)
        .add(VertexFormats.TEXTURE_ELEMENT)
        .add(VertexFormats.LIGHT_ELEMENT);
    private static final double[] RIGHT_MULTIPLIERS = new double[] { -1.0D, -1.0D, 1.0D, 1.0D, -1.0D };
    private static final double[] UP_MULTIPLIERS = new double[] { -1.0D, 1.0D, 1.0D, -1.0D, -1.0D };
    private static final double[] NORMAL_RIGHT_MULTIPLIERS = new double[] { -1.0D, 0.0D, 1.0D, 0.0D };
    private static final double[] NORMAL_UP_MULTIPLIERS = new double[] { 0.0D, 1.0D, 0.0D, -1.0D };
    private final FloatBuffer l2wBuffer = GlAllocationUtils.allocateFloatBuffer(16);

    private final MinecraftClient client;

    public RenderClotheslineNetwork(MinecraftClient client) {
        this.client = client;
    }

    private static VertexConsumer pos(VertexConsumer vertices, Vec3d pos) {
        return vertices.vertex(pos.x, pos.y, pos.z);
    }

    private static VertexConsumer posNormal(VertexConsumer vertices, Vec3d pos, Vec3d normal) {
        return vertices.vertex(pos.x, pos.y, pos.z).normal((float) normal.x, (float) normal.y, (float) normal.z);
    }

    public void renderEdge(float fromOffset, float toOffset, int combinedLightFrom, int combinedLightTo, LineProjection p, VertexConsumer vertices, double x, double y, double z) {
        int lightFrom1 = combinedLightFrom >> 16 & 0xFFFF;
        int lightFrom2 = combinedLightFrom & 0xFFFF;
        int lightTo1 = combinedLightTo >> 16 & 0xFFFF;
        int lightTo2 = combinedLightTo & 0xFFFF;
        float vFrom = fromOffset / AttachmentUnit.UNITS_PER_BLOCK;
        float vTo = toOffset / AttachmentUnit.UNITS_PER_BLOCK;

        for (int j = 0; j < 4; j++) {
            double r1 = RIGHT_MULTIPLIERS[j];
            double r2 = RIGHT_MULTIPLIERS[j + 1];
            double u1 = UP_MULTIPLIERS[j];
            double u2 = UP_MULTIPLIERS[j + 1];
            double nr = NORMAL_RIGHT_MULTIPLIERS[j];
            double nu = NORMAL_UP_MULTIPLIERS[j];

            float uFrom = (4.0F - j) / 4.0F;
            float uTo = (3.0F - j) / 4.0F;

            Vec3d normal = p.projectTangentRU(nr, nu);
            posNormal(vertices, p.projectRUF(
                (r1 - 4.0D) / 32.0D,
                u1 / 32.0D,
                0.0D
            ).subtract(x, y, z), normal).texture(uFrom, vFrom).texture(lightFrom1, lightFrom2).next();
            posNormal(vertices, p.projectRUF(
                (r2 - 4.0D) / 32.0D,
                u2 / 32.0D,
                0.0D
            ).subtract(x, y, z), normal).texture(uTo, vFrom).texture(lightFrom1, lightFrom2).next();
            posNormal(vertices, p.projectRUF(
                (r2 - 4.0D) / 32.0D,
                u2 / 32.0D,
                1.0D
            ).subtract(x, y, z), normal).texture(uTo, vTo).texture(lightTo1, lightTo2).next();
            posNormal(vertices, p.projectRUF(
                (r1 - 4.0D) / 32.0D,
                u1 / 32.0D,
                1.0D
            ).subtract(x, y, z), normal).texture(uFrom, vTo).texture(lightTo1, lightTo2).next();
        }
    }

    private void renderEdge(BlockRenderView world, NetworkEdge edge, double x, double y, double z, VertexConsumer vertexConsumer, float delta) {
        Path.Edge ge = edge.getPathEdge();
        Line line = ge.getLine();
        int combinedLightFrom = world.getBaseLightLevel(line.getFromPos(), 0);
        int combinedLightTo = world.getBaseLightLevel(line.getToPos(), 0);
        float shift = (float) edge.getNetwork().getState().getShift(delta);
        renderEdge(ge.getFromOffset() - shift, ge.getToOffset() - shift, combinedLightFrom, combinedLightTo, LineProjection.create(edge), vertexConsumer, x, y, z);
    }

    public void buildAndDrawEdgeQuads(Consumer<VertexConsumer> consumer) {
        client.getTextureManager().bindTexture(TEXTURE);
        DiffuseLighting.enable();
        client.gameRenderer.getLightmapTextureManager().enable();
        RenderSystem.enableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(GL11.GL_QUADS, VERTEX_FORMAT);
        consumer.accept(bufferBuilder);
        tessellator.draw();
        RenderSystem.disableCull();
    }

    public void render(BlockRenderView world, RTreeMap<BlockPos, NetworkNode> nodesMap, RTreeMap<Line, NetworkEdge> edgesMap, VisibleRegion visibleRegion, double x, double y, double z, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Vec3d viewPos = new Vec3d(x, y, z);

        // Select all entries in the node map intersecting with the camera frustum
        Selection<NetworkNode> nodes = nodesMap
            .values(box -> visibleRegion.intersects(new Box(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        nodes.forEach(node -> {
            BlockPos pos = node.getPathNode().getPos();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != ClotheslineBlocks.CLOTHESLINE_ANCHOR) return;

            int light = world.getBaseLightLevel(pos, 0);

            Network network = node.getNetwork();
            float shift = network.getState().getShift() * delta + network.getState().getPreviousShift() * (1.0F - delta);
            float crankRotation = -(node.getPathNode().getBaseRotation() + shift) * 360.0F / AttachmentUnit.UNITS_PER_BLOCK;

            matrices.push();
            matrices.translate(pos.getX() - x + 0.5D, pos.getY() - y + 0.5D, pos.getZ() - z + 0.5D);
            if (state.get(ClotheslineAnchorBlock.FACE) == WallMountLocation.CEILING) {
                RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
                crankRotation = -crankRotation;
            }
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(crankRotation));

            matrices.push();
            matrices.scale(2.0F, 2.0F, 2.0F);
            ItemModelRenderer.renderModel(BakedModels.pulleyWheel, ModelTransformation.Type.FIXED, matrices, vertexConsumers, light, 0);
            if (!node.getNetwork().getState().getTree().isEmpty()) {
                ItemModelRenderer.renderModel(BakedModels.pulleyWheelRope, ModelTransformation.Type.FIXED, matrices, vertexConsumers, light, 0);
            }
            matrices.pop();

            if (state.get(ClotheslineAnchorBlock.CRANK)) {
                matrices.push();
                matrices.translate(0.0F, 4.0F / 16.0F, 0.0F);
                ItemModelRenderer.renderModel(BakedModels.crank, ModelTransformation.Type.FIXED, matrices, vertexConsumers, light, 0);
                matrices.pop();
            }

            matrices.pop();
        });

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
        client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();

        // Select all entries in the edge map intersecting with the camera frustum
        Selection<NetworkEdge> edges = edgesMap
            .values(box -> visibleRegion.intersects(new Box(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        // Draw the rope for all edges
        buildAndDrawEdgeQuads(bufferBuilder -> edges.forEach(edge -> renderEdge(world, edge, x, y, z, bufferBuilder, delta)));

        RenderSystem.enableRescaleNormal();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        DiffuseLighting.enable();
        RenderSystem.defaultBlendFunc();

        // World position of attachment item
        Vec4f wPos = new Vec4f();
        // Buffer for local space to world space matrix to upload to GL

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            NetworkState state = edge.getNetwork().getState();
            double fromAttachmentKey = state.offsetToAttachmentKey(pathEdge.getFromOffset(), delta);
            double toAttachmentKey = state.offsetToAttachmentKey(pathEdge.getToOffset(), delta);

            List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange((int) fromAttachmentKey, (int) toAttachmentKey);
            if (!attachments.isEmpty()) {
                EdgeAttachmentProjector projector = EdgeAttachmentProjector.build(edge);

                for (MutableSortedIntMap.Entry<ItemStack> attachmentEntry : attachments) {
                    double attachmentOffset = state.attachmentKeyToOffset(attachmentEntry.getKey(), delta);
                    // Local space to world space matrix
                    Mat4f l2w = projector.getL2WForAttachment(state.getMomentum(delta), attachmentOffset, delta);

                    // Create world position of attachment for lighting calculation
                    wPos.set(0.0F, 0.0F, 0.0F, 1.0F);
                    wPos.multiply(l2w);
                    BlockPos pos = new BlockPos(MathHelper.floor(wPos.getV0()), MathHelper.floor(wPos.getV1()), MathHelper.floor(wPos.getV2()));
                    int light = world.getBaseLightLevel(pos, 0);

                    // Store and flip to be ready for read
                    l2w.putIntoBuffer(l2wBuffer);
                    l2wBuffer.flip();

                    matrices.push();
                    matrices.translate(-viewPos.x, -viewPos.y, -viewPos.z);
                    RenderSystem.multMatrix(l2wBuffer);
                    client.getItemRenderer().renderItem(attachmentEntry.getValue(), ModelTransformation.Type.FIXED, light, 0, matrices, vertexConsumers);
                    matrices.pop();

                    l2wBuffer.clear();
                }
            }
        });

        RenderSystem.disableRescaleNormal();
    }

    public void renderOutline(LineProjection p, double x, double y, double z, float r, float g, float b, float a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < 4; i++) {
            double up = (UP_MULTIPLIERS[i] * 1.01D) / 32.0D;
            double right = (RIGHT_MULTIPLIERS[i] * 1.01D - 4.0D) / 32.0D;

            pos(bufferBuilder, p.projectRUF(right, up, 0.0D).subtract(x, y, z)).color(r, g, b, a).next();
            pos(bufferBuilder, p.projectRUF(right, up, 1.0D).subtract(x, y, z)).color(r, g, b, a).next();
        }

        tessellator.draw();
    }

    private void debugRenderText(String msg, double x, double y, double z, float yaw, float pitch, TextRenderer textRenderer) {
        if (x * x + y * y + z * z > 10.0D * 10.0D) return;
        GameRenderer.renderFloatingText(textRenderer, msg, (float)x, (float)y, (float)z, 0, yaw, pitch, false);
    }

    public void debugRender(
        RTreeMap<BlockPos, NetworkNode> nodesMap,
        RTreeMap<Line, NetworkEdge> edgesMap,
        VisibleRegion visibleRegion, double x, double y, double z, float delta
    ) {
        BlockEntityRenderDispatcher rendererDispatcher = BlockEntityRenderDispatcher.INSTANCE;
        Camera cameraEntity = rendererDispatcher.cameraEntity;
        float yaw = cameraEntity.getYaw();
        float pitch = cameraEntity.getPitch();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // Select all edges in the edges map intersecting with the camera frustum
        Selection<NetworkEdge> edges = edgesMap
            .values(box -> visibleRegion.intersects(new Box(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            BlockPos nodePos = pathEdge.getLine().getFromPos();
            NetworkNode node = nodesMap.get(nodePos);
            Path.Node pathNode = node.getPathNode();
            int nodeIndex = pathNode.getEdges().indexOf(pathEdge);
            Vec3d pos = LineProjection.create(edge).projectRUF(-0.125D, 0.125D, 0.5D);
            debugRenderText("L" + nodeIndex + " G" + edge.getIndex(), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, textRenderer);
        });
    }

    public void renderFirstPersonPlayerHeldClothesline(PlayerEntity player, double x, double y, double z, float delta) {
        ConnectorHolder connector = (ConnectorHolder) player;
        ItemUsageContext from = connector.getFrom();
        if (from == null) return;

        float pitch = (float) Math.toRadians(MathHelper.lerp(delta, player.prevPitch, player.pitch));
        float yaw = (float) Math.toRadians(MathHelper.lerp(delta, player.prevYaw, player.yaw));
        int handedOffset = (player.getMainArm() == Arm.RIGHT ? 1 : -1) * (player.getActiveHand() == Hand.MAIN_HAND ? 1 : -1);
        double f10 = client.options.fov / 100.0D;
        Vec3d vecB = new Vec3d(x, y, z).add(new Vec3d(handedOffset * -0.36D * f10, -0.045D * f10, 0.4D).rotateX(-pitch).rotateY(-yaw));

        renderHeldClothesline(from.getBlockPos(), vecB, player.world, x, y, z);
    }

    public void renderThirdPersonPlayerHeldClothesline(PlayerEntity player, double x, double y, double z, float delta) {
        ConnectorHolder connector = (ConnectorHolder) player;
        ItemUsageContext from = connector.getFrom();
        if (from == null) return;

        double posX = MathHelper.lerp(delta, player.lastRenderX, player.getX());
        double posY = MathHelper.lerp(delta, player.lastRenderY, player.getY());
        double posZ = MathHelper.lerp(delta, player.lastRenderZ, player.getZ());

        float yaw = (float) Math.toRadians(MathHelper.lerp(delta, player.prevYaw, player.yaw));
        int handedOffset = (player.getMainArm() == Arm.RIGHT ? 1 : -1) * (player.getActiveHand() == Hand.MAIN_HAND ? 1 : -1);
        double d0 = MathHelper.sin(yaw) * 0.35D;
        double d1 = MathHelper.cos(yaw) * 0.35D;
        Vec3d vecB = new Vec3d(
            posX - d0 - d1 * handedOffset,
            posY + (player.isSneaking() ? 0.4D : 0.9D),
            posZ - d0 * handedOffset + d1
        );

        renderHeldClothesline(from.getBlockPos(), vecB, player.world, x, y, z);
    }

    private void renderHeldClothesline(BlockPos posA, Vec3d vecB, BlockRenderView world, double x, double y, double z) {
        Vec3d vecA = Utility.midVec(posA);
        BlockPos posB = new BlockPos(vecB);
        int combinedLightA = world.getBaseLightLevel(posA, 0);
        int combinedLightB = world.getBaseLightLevel(posB, 0);
        float length = AttachmentUnit.UNITS_PER_BLOCK * (float) vecB.distanceTo(vecA);

        buildAndDrawEdgeQuads(vertices -> {
            renderEdge(0.0F, length, combinedLightA, combinedLightB, LineProjection.create(vecA, vecB), vertices, x, y, z);
            renderEdge(-length, 0.0F, combinedLightB, combinedLightA, LineProjection.create(vecB, vecA), vertices, x, y, z);
        });
    }
}
