package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.lighting;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>> extends LightEngine<M, S> {

    @Unique
    private static ThreadLocal<BlockLightEngine> INSTANCE = new ThreadLocal<>();

    protected BlockLightEngineMixin(LightChunkGetter lightChunkGetter, S layerLightSectionStorage) {
        super(lightChunkGetter, layerLightSectionStorage);
    }

    @Inject(method = "getEmission", at = @At("HEAD"), cancellable = true)
    public void onGetEmission(long p_285243_, BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (!(this instanceof LightEngineAccessor<?,?> lightEngineAccessor)) {
            return;
        }

        if (!(lightEngineAccessor.getStorage() instanceof LayerLightSectionStorageAccessor<?> layerLightSectionStorageAccessor)) {
            return;
        }

        final Block block = state.getBlock();
        if (!(block instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)) {
            return;
        }

        final int lightEmission = blockWithWorldlyProperties.getLightEmission(state, lightEngineAccessor.getChunkSource().getLevel(), BlockPos.of(p_285243_));
        cir.setReturnValue(lightEmission > 0 && layerLightSectionStorageAccessor.callLightOnInSection(SectionPos.blockToSection(p_285243_)) ? lightEmission : 0);
    }

    @Inject(method="propagateLightSources", at = @At("HEAD"))
    private void onPropagateLightSourcesCall(ChunkPos chunkPos, CallbackInfo ci) {
        INSTANCE.set((BlockLightEngine) (Object) this);
    }

    @Inject(method="propagateLightSources", at = @At("RETURN"))
    private void onPropagateLightSourcesEnd(ChunkPos chunkPos, CallbackInfo ci) {
        INSTANCE.remove();
    }

    @Inject(method = "method_51532", remap = false, at = @At("HEAD"), cancellable = true)
    private void onCallPropagateLightSourcesCallback(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (!(blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)) {
            return;
        }

        final Object currentInstance = INSTANCE.get();
        if (currentInstance == null) {
            return;
        }

        //noinspection ConstantValue
        if (!(currentInstance instanceof LightEngineAccessor<?,?> lightEngineAccessor)) {
            return;
        }

        int lightEmission = blockWithWorldlyProperties.getLightEmission(blockState, lightEngineAccessor.getChunkSource().getLevel(), blockPos);
        lightEngineAccessor.callEnqueueIncrease(blockPos.asLong(), LightEngine.QueueEntry.increaseLightFromEmission(lightEmission, isEmptyShape(blockState)));

        ci.cancel();
    }
}
