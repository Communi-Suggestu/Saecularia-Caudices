package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityWorldlyBlockMixin extends Entity
{
    public LivingEntityWorldlyBlockMixin(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
    }

    @WrapOperation(
        method = "travelInAir",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
        )
    )
    private float rewriteFrictionValueForWorldlyBlocks(final Block instance, final Operation<Float> original, @Local(name = "posBelow") BlockPos blockPos) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return original.call(instance);

        final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
        final BlockState blockState = entityAccessor.getLevel().getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getFriction(blockState, entityAccessor.getLevel(), pos, this);
        }

        return original.call(instance);
    }

    @WrapOperation(
        method = "playBlockFallSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
        )
    )
    private SoundType rewriteSoundTypeForWorldlyBlocks(final BlockState instance, final Operation<SoundType> original) {
        if (!(this instanceof EntityAccessor entityAccessor))
            return original.call(instance);

        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY() - (double)0.2F);
        int k = Mth.floor(this.getZ());
        final BlockPos pos = new BlockPos(i, j, k);
        BlockState blockState = entityAccessor.getLevel().getBlockState(pos);

        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getSoundType(
                blockState, entityAccessor.getLevel(), pos, this
            );
        }
        return original.call(instance);
    }
}
