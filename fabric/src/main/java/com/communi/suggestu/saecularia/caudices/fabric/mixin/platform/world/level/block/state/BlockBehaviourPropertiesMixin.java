package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.block.state;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.Properties.class)
public class BlockBehaviourPropertiesMixin {


    @Inject(method = "method_26239", remap = false, at = @At("HEAD"), cancellable = true)
    private static void onCallDefaultIsValidSpawnCallback(final BlockState blockState, final BlockGetter blockGetter, final BlockPos position, final EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (!(blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties))
            return;

        cir.setReturnValue(blockState.isFaceSturdy(blockGetter, position, Direction.UP) && blockWithWorldlyProperties.getLightEmission(blockState, blockGetter, position) < 14);
    }
}
