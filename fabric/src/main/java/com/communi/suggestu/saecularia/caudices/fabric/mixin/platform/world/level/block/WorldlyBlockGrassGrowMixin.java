package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.block;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpreadingSnowyDirtBlock.class)
public abstract class WorldlyBlockGrassGrowMixin
{

    @Inject(
      method = "canBeGrass",
      at = @At("HEAD"),
      cancellable = true
    )
    private static void canBeGrassWorldlyBlock(BlockState grassState, LevelReader levelReader, BlockPos grassBlockPos, CallbackInfoReturnable<Boolean> ci){
        BlockPos targetPosition = grassBlockPos.above();
        BlockState targetState = levelReader.getBlockState(targetPosition);

        if (targetState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            ci.setReturnValue(blockWithWorldlyProperties.canBeGrass(
                            levelReader, grassState, grassBlockPos, targetState, targetPosition
                    )
            );
        }
    }
}
