package com.communi.suggesta.saecularia.caudices.fabric.mixin.platform.world.level;

import com.communi.suggesta.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourWorldlyBlockMixin
{
    @Inject(
      method = "getDestroyProgress",
      at = @At(
        value = "HEAD"
      )
    )
    public void handleWorldlyBreakableCondition(final BlockState state, final Player player, final BlockGetter level, final BlockPos pos, final CallbackInfoReturnable<Float> cir)
    {
        if (state.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            float f = state.getDestroySpeed(level, pos);
            if (f == -1.0F) {
                cir.setReturnValue(0.0F);
            } else {
                int i = blockWithWorldlyProperties.canHarvestBlock(
                        state, level, pos, player
                ) ? 30 : 100;
                cir.setReturnValue(player.getDestroySpeed(state) / f / (float)i);
            }
        }
    }
}
