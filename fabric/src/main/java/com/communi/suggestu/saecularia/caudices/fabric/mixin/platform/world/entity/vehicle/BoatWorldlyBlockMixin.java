package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity.vehicle;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity.EntityAccessor;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBoat.class, priority = Integer.MIN_VALUE)
public abstract class BoatWorldlyBlockMixin extends Entity
{
    @Unique private BlockState workingState;
    @Unique private BlockPos workingPos;

    public BoatWorldlyBlockMixin(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
    }


    @Inject(
        method = "getGroundFriction",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
        )
    )
    private void injectGetFrictionAdaptorForPosition(final CallbackInfoReturnable<Float> cir, @Local final BlockPos.MutableBlockPos mutableBlockPos)
    {
        this.workingPos = mutableBlockPos;
    }

    @Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Expression("? = ?.getBlockState(?)")
    @ModifyVariable(
            method = "getGroundFriction",
            at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
            ordinal = 0
    )
    private BlockState injectGetFrictionAdaptorForState(final BlockState current)
    {
        this.workingState = current;
        return current;
    }

    @ModifyVariable(
            method = "getGroundFriction",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
            ),
            ordinal = 0
    )
    private float injectGetFrictionAdaptorForState(final float current)
    {
        if (!(this instanceof EntityAccessor entityAccessor))
            return current;

        if (this.workingState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getFriction(this.workingState, entityAccessor.getLevel(), this.workingPos, this);
        }

        return current;
    }
}
