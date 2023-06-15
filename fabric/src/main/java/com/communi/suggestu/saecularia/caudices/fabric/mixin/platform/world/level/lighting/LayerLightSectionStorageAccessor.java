package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.lighting;

import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LayerLightSectionStorage.class)
public interface LayerLightSectionStorageAccessor<M extends DataLayerStorageMap<M>> {

    @Invoker
    boolean callLightOnInSection(long sectionPos);
}
