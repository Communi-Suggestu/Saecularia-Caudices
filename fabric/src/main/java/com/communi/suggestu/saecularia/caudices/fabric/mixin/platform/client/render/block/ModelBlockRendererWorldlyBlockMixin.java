package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.client.render.block;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererWorldlyBlockMixin
{
    @Shadow public abstract void tesselateWithAO(BlockAndTintGetter level, List<BlockModelPart> parts, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer consumer, boolean par7, int par8);

    @Inject(
        method = "tesselateBlock",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(Lnet/minecraft/world/phys/Vec3;)V"
        ),
        cancellable = true)
    private void handleWorldlyBlocksWhichDoNotEmitDefaultLightForAO(
        final BlockAndTintGetter blockAndTintGetter,
        final List<BlockModelPart> list,
        final BlockState blockState,
        final BlockPos blockPos,
        final PoseStack poseStack,
        final VertexConsumer vertexConsumer,
        final boolean bl,
        final int i,
        final CallbackInfo ci)
    {
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            boolean usesAmbientOcclusion = Minecraft.useAmbientOcclusion() && blockWithWorldlyProperties.getLightEmission(blockState, blockAndTintGetter, blockPos) == 0;
            if (usesAmbientOcclusion)
            {
                this.tesselateWithAO(blockAndTintGetter, list, blockState, blockPos, poseStack, vertexConsumer, bl, i);
                ci.cancel();
            }
        }
    }
}
