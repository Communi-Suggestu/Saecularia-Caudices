package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {


    @Unique
    private SignalGetter getInternalMixinTarget() {
        return (SignalGetter) this;
    }

    @Inject(
            method = "getSignal",
            at = @At("HEAD"),
            cancellable = true
    )
    default void checkForWorldlySignalBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        final BlockState blockState = getInternalMixinTarget().getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            final boolean shouldCheck = blockWithWorldlyProperties.shouldCheckWeakPower(
                    blockState, getInternalMixinTarget(), pos, direction
            );

            final int signal = blockState.getSignal(getInternalMixinTarget(), pos, direction);

            cir.setReturnValue(
                    shouldCheck ? Math.max(signal, getInternalMixinTarget().getDirectSignalTo(pos)) : signal
            );
        }
    }
}
