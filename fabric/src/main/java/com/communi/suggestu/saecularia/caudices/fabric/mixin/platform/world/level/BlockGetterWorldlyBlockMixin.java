package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface BlockGetterWorldlyBlockMixin {

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "getLightEmission(Lnet/minecraft/core/BlockPos;)I", at = @At("HEAD"), cancellable = true)
    public default void onGetLightEmission(final BlockPos pos, final CallbackInfoReturnable<Integer> cir) {
        final BlockState blockState = getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            cir.setReturnValue(blockWithWorldlyProperties.getLightEmission(blockState, (BlockGetter) this, pos));
        }
    }
}
