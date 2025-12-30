package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity.animal.horse;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity.EntityAccessor;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseWorldlyBlockMixin extends Entity
{
    public AbstractHorseWorldlyBlockMixin(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
    }

    @Definition(id = "getSoundType", method = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;")
    @Expression("? = ?.getSoundType()")
    @ModifyVariable(
        method = "playStepSound",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
        name = "soundType")
    private SoundType injectGetBlockStateSoundType(final SoundType current, BlockPos pPos, BlockState pBlock)
    {
        if (!(this instanceof EntityAccessor entityAccessor))
            return current;

        if (pBlock.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getSoundType(
                    pBlock, entityAccessor.getLevel(), pPos, this
            );
        }
        return current;
    }
}
