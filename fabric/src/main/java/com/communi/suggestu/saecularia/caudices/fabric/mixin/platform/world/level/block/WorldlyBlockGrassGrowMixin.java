package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.block;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpreadingSnowyBlock.class)
public abstract class WorldlyBlockGrassGrowMixin
{

    @Inject(
      method = "canStayAlive",
      at = @At("HEAD"),
      cancellable = true
    )
    private static void canBeGrassWorldlyBlock(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> ci){
        BlockPos targetPosition = pos.above();
        BlockState targetState = level.getBlockState(targetPosition);

        if (targetState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            ci.setReturnValue(blockWithWorldlyProperties.canBeGrass(
                level, state, pos, targetState, targetPosition
                    )
            );
        }
    }
}
