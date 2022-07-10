package com.communi.suggesta.saecularia.caudices.fabric.mixin.platform.world.entity;

import com.communi.suggesta.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(LivingEntity.class)
public abstract class LivingEntityWorldlyBlockMixin extends Entity
{
    public LivingEntityWorldlyBlockMixin(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
    }

    @ModifyVariable(
      method = "travel",
      slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;")),
      at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"), ordinal = 0
    )
    private float rewriteFrictionValueForWorldlyBlocks(float original) { // shut, MCDev
        final BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
        final BlockState blockState = this.level.getBlockState(pos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getFriction(blockState, this.level, pos, this);
        }

        return original;
    }


    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "playBlockFallSound",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            ),
            ordinal = 0
    )
    private SoundType injectGetBlockStateSoundType(final SoundType current)
    {
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY() - (double)0.2F);
        int k = Mth.floor(this.getZ());
        final BlockPos pos = new BlockPos(i, j, k);
        BlockState blockState = this.level.getBlockState(pos);

        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getSoundType(
                    blockState, level, pos, this
            );
        }
        return current;
    }
}
