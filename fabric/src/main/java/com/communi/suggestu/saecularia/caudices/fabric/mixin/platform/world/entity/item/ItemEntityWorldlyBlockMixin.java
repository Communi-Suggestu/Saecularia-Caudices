package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity.item;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity.EntityAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityWorldlyBlockMixin extends Entity
{
    public ItemEntityWorldlyBlockMixin(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
    }

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;getFriction()F"
        ))
    private float injectGetFrictionAdaptor(final Block instance, final Operation<Float> original)
    {
        if (!(this instanceof EntityAccessor entityAccessor))
            return original.call(instance);

        final BlockPos pPos = new BlockPos(this.getBlockX(), this.getBlockY(), this.getBlockZ()).below();
        final BlockState blockState = entityAccessor.getLevel().getBlockState(pPos);

        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getFriction(
                    blockState, entityAccessor.getLevel(), pPos, this
            );
        }
        return original.call(instance);
    }
}
