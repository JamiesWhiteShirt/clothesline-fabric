package com.jamieswhiteshirt.clotheslinefabric.client.render;

import com.jamieswhiteshirt.clotheslinefabric.api.*;
import com.jamieswhiteshirt.clotheslinefabric.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clotheslinefabric.client.EdgeAttachmentProjector;
import com.jamieswhiteshirt.clotheslinefabric.client.LineProjection;
import com.jamieswhiteshirt.clotheslinefabric.internal.ConnectorHolder;
import com.jamieswhiteshirt.rtree3i.RTreeMap;
import com.jamieswhiteshirt.rtree3i.Selection;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_856;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ExtendedBlockView;
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
        .add(VertexFormats.UV_ELEMENT)
        .add(VertexFormats.LMAP_ELEMENT);
    private static final double[] RIGHT_MULTIPLIERS = new double[] { -1.0D, -1.0D, 1.0D, 1.0D, -1.0D };
    private static final double[] UP_MULTIPLIERS = new double[] { -1.0D, 1.0D, 1.0D, -1.0D, -1.0D };
    private static final double[] NORMAL_RIGHT_MULTIPLIERS = new double[] { -1.0D, 0.0D, 1.0D, 0.0D };
    private static final double[] NORMAL_UP_MULTIPLIERS = new double[] { 0.0D, 1.0D, 0.0D, -1.0D };

    private final MinecraftClient client;

    public RenderClotheslineNetwork(MinecraftClient client) {
        this.client = client;
    }

    private static BufferBuilder pos(BufferBuilder bufferBuilder, Vec3d pos) {
        return bufferBuilder.vertex(pos.x, pos.y, pos.z);
    }

    private static BufferBuilder posNormal(BufferBuilder bufferBuilder, Vec3d pos, Vec3d normal) {
        // Why are we calling vertex twice...?
        // Calling normal will round the components toward zero.
        // The buffer builder doesn't care what the current vertex format element represents, so it works!
        return bufferBuilder.vertex(pos.x, pos.y, pos.z).vertex(normal.x * 127, normal.y * 127, normal.z * 127);
    }

    public void renderEdge(double fromOffset, double toOffset, int combinedLightFrom, int combinedLightTo, LineProjection p, BufferBuilder bufferBuilder, double x, double y, double z) {
        int lightFrom1 = combinedLightFrom >> 16 & 0xFFFF;
        int lightFrom2 = combinedLightFrom & 0xFFFF;
        int lightTo1 = combinedLightTo >> 16 & 0xFFFF;
        int lightTo2 = combinedLightTo & 0xFFFF;
        double vFrom = fromOffset / AttachmentUnit.UNITS_PER_BLOCK;
        double vTo = toOffset / AttachmentUnit.UNITS_PER_BLOCK;

        for (int j = 0; j < 4; j++) {
            double r1 = RIGHT_MULTIPLIERS[j];
            double r2 = RIGHT_MULTIPLIERS[j + 1];
            double u1 = UP_MULTIPLIERS[j];
            double u2 = UP_MULTIPLIERS[j + 1];
            double nr = NORMAL_RIGHT_MULTIPLIERS[j];
            double nu = NORMAL_UP_MULTIPLIERS[j];

            double uFrom = (4.0D - j) / 4.0D;
            double uTo = (3.0D - j) / 4.0D;

            Vec3d normal = p.projectTangentRU(nr, nu);
            posNormal(bufferBuilder, p.projectRUF(
                (r1 - 4.0D) / 32.0D,
                u1 / 32.0D,
                0.0D
            ).subtract(x, y, z), normal).texture(uFrom, vFrom).texture(lightFrom1, lightFrom2).next();
            posNormal(bufferBuilder, p.projectRUF(
                (r2 - 4.0D) / 32.0D,
                u2 / 32.0D,
                0.0D
            ).subtract(x, y, z), normal).texture(uTo, vFrom).texture(lightFrom1, lightFrom2).next();
            posNormal(bufferBuilder, p.projectRUF(
                (r2 - 4.0D) / 32.0D,
                u2 / 32.0D,
                1.0D
            ).subtract(x, y, z), normal).texture(uTo, vTo).texture(lightTo1, lightTo2).next();
            posNormal(bufferBuilder, p.projectRUF(
                (r1 - 4.0D) / 32.0D,
                u1 / 32.0D,
                1.0D
            ).subtract(x, y, z), normal).texture(uFrom, vTo).texture(lightTo1, lightTo2).next();
        }
    }

    private void renderEdge(ExtendedBlockView world, NetworkEdge edge, double x, double y, double z, BufferBuilder bufferBuilder, float delta) {
        Path.Edge ge = edge.getPathEdge();
        Line line = ge.getLine();
        int combinedLightFrom = world.getLightmapIndex(line.getFromPos(), 0);
        int combinedLightTo = world.getLightmapIndex(line.getToPos(), 0);
        double shift = edge.getNetwork().getState().getShift(delta);
        renderEdge(ge.getFromOffset() - shift, ge.getToOffset() - shift, combinedLightFrom, combinedLightTo, LineProjection.create(edge), bufferBuilder, x, y, z);
    }

    public void buildAndDrawEdgeQuads(Consumer<BufferBuilder> consumer) {
        client.getTextureManager().bindTexture(TEXTURE);
        GuiLighting.enable();
        client.worldRenderer.enableLightmap();
        GlStateManager.enableCull();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();

        bufferBuilder.begin(GL11.GL_QUADS, VERTEX_FORMAT);
        consumer.accept(bufferBuilder);
        tessellator.draw();
        GlStateManager.disableCull();
    }

    public void render(ExtendedBlockView world, RTreeMap<Line, NetworkEdge> edgesMap, class_856 camera, double x, double y, double z, float delta) {
        Vec3d viewPos = new Vec3d(x, y, z);

        // Select all entries in the edge map intersecting with the camera frustum
        Selection<NetworkEdge> edges = edgesMap
            .values(box -> camera.method_3699(new BoundingBox(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        // Draw the rope for all edges
        buildAndDrawEdgeQuads(bufferBuilder -> edges.forEach(edge -> renderEdge(world, edge, x, y, z, bufferBuilder, delta)));

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GuiLighting.enable();
        GlStateManager.blendFuncSeparate(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO);

        // World position of attachment item
        Vector4f wPos = new Vector4f();
        // Buffer for local space to world space matrix to upload to GL
        FloatBuffer l2wBuffer = GlAllocationUtils.allocateFloatBuffer(16);

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
                    Matrix4f l2w = projector.getL2WForAttachment(state.getMomentum(delta), attachmentOffset, delta);

                    // Create world position of attachment for lighting calculation
                    wPos.set(0.0F, 0.0F, 0.0F, 1.0F);
                    wPos.multiply(l2w);
                    int light = world.getLightmapIndex(new BlockPos(MathHelper.floor(wPos.x()), MathHelper.floor(wPos.y()), MathHelper.floor(wPos.z())), 0);
                    GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)(light & 0xFFFF), (float)((light >> 16) & 0xFFFF));

                    // Store and flip to be ready for read
                    l2w.putIntoBuffer(l2wBuffer);
                    l2wBuffer.flip();

                    GlStateManager.pushMatrix();
                    GlStateManager.translated(-viewPos.x, -viewPos.y, -viewPos.z);
                    GlStateManager.multMatrix(l2wBuffer);
                    client.getItemRenderer().renderItemWithTransformation(attachmentEntry.getValue(), ModelTransformation.Type.FIXED);
                    GlStateManager.popMatrix();

                    l2wBuffer.clear();
                }
            }
        });

        GlStateManager.disableRescaleNormal();
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

    private void debugRenderText(String msg, double x, double y, double z, float yaw, float pitch, FontRenderer fontRenderer) {
        GameRenderer.method_3179(fontRenderer, msg, (float)x, (float)y, (float)z, 0, yaw, pitch, false, false);
    }

    public void debugRender(
        RTreeMap<BlockPos, NetworkNode> nodesMap,
        RTreeMap<Line, NetworkEdge> edgesMap,
        class_856 camera, double x, double y, double z, float delta
    ) {
        BlockEntityRenderDispatcher rendererDispatcher = BlockEntityRenderDispatcher.INSTANCE;
        float yaw = rendererDispatcher.cameraYaw;
        float pitch = rendererDispatcher.cameraPitch;
        FontRenderer fontRenderer = MinecraftClient.getInstance().fontRenderer;

        // Select all edges in the edges map intersecting with the camera frustum
        Selection<NetworkEdge> edges = edgesMap
            .values(box -> camera.method_3699(new BoundingBox(box.x1(), box.y1(), box.z1(), box.x2(), box.y2(), box.z2())));

        edges.forEach(edge -> {
            Path.Edge pathEdge = edge.getPathEdge();
            BlockPos nodePos = pathEdge.getLine().getFromPos();
            NetworkNode node = nodesMap.get(nodePos);
            Path.Node pathNode = node.getPathNode();
            int nodeIndex = pathNode.getEdges().indexOf(pathEdge);
            Vec3d pos = LineProjection.create(edge).projectRUF(-0.125D, 0.125D, 0.5D);
            debugRenderText("L" + nodeIndex + " G" + edge.getIndex(), pos.x - x, pos.y - y, pos.z - z, yaw, pitch, fontRenderer);
        });
    }

    public void renderFirstPersonPlayerHeldClothesline(PlayerEntity player, double x, double y, double z, float delta) {
        ConnectorHolder connector = (ConnectorHolder) player;
        ItemUsageContext from = connector.getFrom();
        if (from != null) {
            float pitch = (float) Math.toRadians(MathHelper.lerp(delta, player.prevPitch, player.pitch));
            float yaw = (float) Math.toRadians(MathHelper.lerp(delta, player.prevYaw, player.yaw));
            int k = player.getMainHand() == OptionMainHand.RIGHT ? 1 : -1;
            double f10 = client.options.fov / 100.0D;
            Vec3d vecB = new Vec3d(x, y + player.getEyeHeight(), z).add(new Vec3d(k * -0.36D * f10, -0.045D * f10, 0.4D).rotateX(-pitch).rotateY(-yaw));

            renderHeldClothesline(from.getPos(), vecB, player.world, x, y, z);
        }
    }

    public void renderThirdPersonPlayerHeldClothesline(PlayerEntity player, double x, double y, double z, float delta) {
        ConnectorHolder connector = (ConnectorHolder) player;
        ItemUsageContext from = connector.getFrom();
        if (from != null) {
            double posX = MathHelper.lerp(delta, player.prevRenderX, player.x);
            double posY = MathHelper.lerp(delta, player.prevRenderY, player.y);
            double posZ = MathHelper.lerp(delta, player.prevRenderZ, player.z);

            float yaw = (float) Math.toRadians(MathHelper.lerp(delta, player.prevYaw, player.yaw));
            int k = player.getMainHand() == OptionMainHand.RIGHT ? 1 : -1;
            double d0 = MathHelper.sin(yaw) * 0.35D;
            double d1 = MathHelper.cos(yaw) * 0.35D;
            Vec3d vecB = new Vec3d(
                posX - d0 - d1 * k,
                posY + (player.isSneaking() ? 0.4D : 0.9D),
                posZ - d0 * k + d1
            );

            renderHeldClothesline(from.getPos(), vecB, player.world, x, y, z);
        }
    }

    private void renderHeldClothesline(BlockPos posA, Vec3d vecB, ExtendedBlockView world, double x, double y, double z) {
        Vec3d vecA = Utility.midVec(posA);
        BlockPos posB = new BlockPos(vecB);
        int combinedLightA = world.getLightmapIndex(posA, 0);
        int combinedLightB = world.getLightmapIndex(posB, 0);
        double length = AttachmentUnit.UNITS_PER_BLOCK * vecB.distanceTo(vecA);

        buildAndDrawEdgeQuads(bufferBuilder -> {
            renderEdge(0.0D, length, combinedLightA, combinedLightB, LineProjection.create(vecA, vecB), bufferBuilder, x, y, z);
            renderEdge(-length, 0.0D, combinedLightB, combinedLightA, LineProjection.create(vecB, vecA), bufferBuilder, x, y, z);
        });
    }
}
