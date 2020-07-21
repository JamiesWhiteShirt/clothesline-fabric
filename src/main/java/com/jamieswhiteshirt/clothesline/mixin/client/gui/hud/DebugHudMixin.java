package com.jamieswhiteshirt.clothesline.mixin.client.gui.hud;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.client.raytrace.NetworkRaytraceHit;
import com.jamieswhiteshirt.clothesline.client.raytrace.NetworkRaytraceHitEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin extends DrawableHelper {
    @Shadow @Final private MinecraftClient client;
    @Shadow private HitResult blockHit;

    @Inject(
        at = @At("RETURN"),
        method = "getRightText()Ljava/util/List;"
    )
    private void getRightText(CallbackInfoReturnable<List<String>> cir) {
        if (!client.hasReducedDebugInfo()) {
            if (client.targetedEntity instanceof NetworkRaytraceHitEntity) {
                NetworkRaytraceHit hit = ((NetworkRaytraceHitEntity) client.targetedEntity).getHit();
                NetworkEdge edge = hit.edge;
                Network network = edge.getNetwork();
                cir.getReturnValue().addAll(Arrays.asList(
                    "",
                    Formatting.UNDERLINE + "Targeted Clothesline",
                    "Edge ID: " + edge.getIndex() + "@" + network.getId(),
                    "Span: " + edge.getPathEdge().getFromOffset() + " to " + edge.getPathEdge().getToOffset(),
                    hit.getDebugString()
                ));
            }
            if (blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
                NetworkManager manager = ((NetworkManagerProvider) client.world).getNetworkManager();
                NetworkNode node = manager.getNetworks().getNodes().get(((BlockHitResult) blockHit).getBlockPos());
                if (node != null) {
                    cir.getReturnValue().addAll(Arrays.asList(
                        "",
                        Formatting.UNDERLINE + "Targeted Clothesline Anchor",
                        "Network ID: " + node.getNetwork().getId()
                    ));
                }
            }
        }
    }
}
