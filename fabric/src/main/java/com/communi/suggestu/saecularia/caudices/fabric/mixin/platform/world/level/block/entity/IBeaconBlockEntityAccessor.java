package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.block.entity;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface IBeaconBlockEntityAccessor
{
    @Accessor
    int getLastCheckY();
}
