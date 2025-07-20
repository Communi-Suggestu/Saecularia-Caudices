package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Leashable.class)
public class LeashableWorldlyBlockMixin
{

    @ModifyExpressionValue(
        method = "angularFriction",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
    )
    private static <E extends Entity & Leashable> float OnAngularFriction(final float original, final E entity) {
        final var blockState = entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement());
        final var block = blockState.getBlock();
        if (block instanceof IBlockWithWorldlyProperties worldlyProperties) {
            return worldlyProperties.getFriction(blockState, entity.level(), entity.getBlockPosBelowThatAffectsMyMovement(), entity);
        }

        return original;
    }
}
