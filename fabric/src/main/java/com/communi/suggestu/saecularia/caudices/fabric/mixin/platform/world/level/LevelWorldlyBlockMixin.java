package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelWorldlyBlockMixin implements LevelAccessor, AutoCloseable
{

    @Unique private BlockState previousState = null;
    @Unique private int oldLight = 0;

    private Level getInternalMixinTarget() {
        return (Level) (Object) this;
    }

    @Shadow public abstract @NotNull BlockState getBlockState(final BlockPos pPos);

    @Inject(
      method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
      at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/level/chunk/LevelChunk;setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
        ordinal = 0,
        shift = At.Shift.BEFORE
      )
    )
    public void onSetBlockStateCapturePreviousState(final BlockPos pos, final BlockState state, final int flags, final int recursionLeft, final CallbackInfoReturnable<Boolean> cir) {
        this.previousState = this.getBlockState(pos);
        if (previousState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            oldLight = blockWithWorldlyProperties.getLightEmission(previousState, getInternalMixinTarget(), pos);
        }
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 0
            )
    )
    public void onSetBlockStateDoLightEngineUpdate(final BlockPos pos, final BlockState state, final int flags, final int recursionLeft, final CallbackInfoReturnable<Boolean> cir) {
        final BlockState newState = this.getBlockState(pos);

        int oldOpacity = previousState.getLightBlock(getInternalMixinTarget(), pos);

        int newLight = newState.getLightEmission();
        int newOpacity = newState.getLightBlock(getInternalMixinTarget(), pos);

        if (newState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            newLight = blockWithWorldlyProperties.getLightEmission(newState, getInternalMixinTarget(), pos);
        }

        if ((flags & 128) == 0 && previousState != newState && (newOpacity != oldOpacity || newLight != oldLight || previousState.useShapeForLightOcclusion() || newState.useShapeForLightOcclusion())) {
            getInternalMixinTarget().getProfiler().push("queueCheckLight");
            this.getChunkSource().getLightEngine().checkBlock(pos);
            getInternalMixinTarget().getProfiler().pop();
        }
    }
}
