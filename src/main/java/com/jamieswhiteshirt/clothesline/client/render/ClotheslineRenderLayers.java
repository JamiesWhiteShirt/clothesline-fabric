package com.jamieswhiteshirt.clothesline.client.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class ClotheslineRenderLayers extends RenderLayer {
    private ClotheslineRenderLayers(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final VertexFormat CLOTHESLINE_VERTEX_FORMAT = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
        .add(VertexFormats.POSITION_ELEMENT)
        .add(VertexFormats.NORMAL_ELEMENT)
        .add(VertexFormats.TEXTURE_ELEMENT)
        .add(VertexFormats.LIGHT_ELEMENT)
        .build()
    );

    private static final Identifier CLOTHESLINE_TEXTURE = new Identifier("clothesline", "textures/misc/clothesline.png");
    // TODO: What is a reasonable default buffer size?
    private static final RenderLayer CLOTHESLINE = RenderLayer.of("clothesline", CLOTHESLINE_VERTEX_FORMAT, GL11.GL_QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
        .texture(new RenderPhase.Texture(CLOTHESLINE_TEXTURE, false, false))
        .transparency(NO_TRANSPARENCY)
        .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
        .lightmap(ENABLE_LIGHTMAP)
        .build(true)
    );

    public static RenderLayer getClothesline() {
        return CLOTHESLINE;
    }
}
