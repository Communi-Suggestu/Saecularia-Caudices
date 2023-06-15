package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbWorldlyBlockMixin extends Entity
{
    public ExperienceOrbWorldlyBlockMixin(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
            )
    )
    private float injectGetFrictionAdaptor(final float current)
    {
        if (!(this instanceof EntityAccessor entityAccessor))
            return current;

        final BlockPos pPos = new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ()).below();
        final BlockState blockState = entityAccessor.getLevel().getBlockState(pPos);

        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getFriction(
                    blockState, entityAccessor.getLevel(), pPos, this
            ) * 0.98f;
        }
        return current;
    }
}
