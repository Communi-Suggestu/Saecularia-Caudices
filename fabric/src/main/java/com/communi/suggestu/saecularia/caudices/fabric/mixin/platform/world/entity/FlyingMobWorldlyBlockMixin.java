package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FlyingMob.class)
public abstract class FlyingMobWorldlyBlockMixin extends Mob
{
    protected FlyingMobWorldlyBlockMixin(final EntityType<? extends Mob> entityType, final Level level)
    {
        super(entityType, level);
    }

    @ModifyVariable(
            method = "travel",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
            ),
            ordinal = 0
    )
    private float injectGetFrictionAdaptorForGCalculation(final float current)
    {
        return handleInjectionPoint(current);
    }

    private float handleInjectionPoint(final float current)
    {
        if (!(this instanceof EntityAccessor entityAccessor))
            return current;

        final BlockPos pPos = new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ()).below();
        final BlockState blockState = entityAccessor.getLevel().getBlockState(pPos);

        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getFriction(
                    blockState, entityAccessor.getLevel(), pPos, this
            ) * 0.91f;
        }
        return current;
    }
}
