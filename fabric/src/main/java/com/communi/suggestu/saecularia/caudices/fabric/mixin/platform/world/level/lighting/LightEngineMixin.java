package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.lighting;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public class LightEngineMixin {

    @Inject(method = "hasDifferentLightProperties", at = @At("HEAD"), cancellable = true)
    private static void onHasDifferentLightProperties(BlockGetter blockGetter, BlockPos blockPos, BlockState oldState, BlockState newState, CallbackInfoReturnable<Boolean> cir) {
        if (oldState == newState) {
            return;
        }

        if (!(oldState.getBlock() instanceof IBlockWithWorldlyProperties)) {
            if (!(newState.getBlock() instanceof IBlockWithWorldlyProperties)) {
                return;
            }
        }

        final IBlockWithWorldlyProperties oldWithWorldlyProperties = (IBlockWithWorldlyProperties) oldState.getBlock();

        if (!(newState.getBlock() instanceof final IBlockWithWorldlyProperties newWithWorldlyProperties)) {
            cir.setReturnValue(newState.getLightBlock(blockGetter, blockPos) != oldState.getLightBlock(blockGetter, blockPos) || newState.getLightEmission() != oldWithWorldlyProperties.getLightEmission(oldState, blockGetter, blockPos) || newState.useShapeForLightOcclusion() || oldState.useShapeForLightOcclusion());
            return;
        }

        cir.setReturnValue(newState.getLightBlock(blockGetter, blockPos) != oldState.getLightBlock(blockGetter, blockPos) || newWithWorldlyProperties.getLightEmission(newState, blockGetter, blockPos) != oldWithWorldlyProperties.getLightEmission(oldState, blockGetter, blockPos) || newState.useShapeForLightOcclusion() || oldState.useShapeForLightOcclusion());
    }
}
