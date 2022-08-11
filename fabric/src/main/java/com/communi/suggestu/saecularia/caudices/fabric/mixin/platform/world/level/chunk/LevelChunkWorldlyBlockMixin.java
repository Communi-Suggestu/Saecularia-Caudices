package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.chunk;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LevelChunk.class, priority = Integer.MIN_VALUE)
public abstract class LevelChunkWorldlyBlockMixin extends ChunkAccess
{

    public LevelChunkWorldlyBlockMixin(
      final ChunkPos chunkPos,
      final UpgradeData upgradeData,
      final LevelHeightAccessor levelHeightAccessor,
      final Registry<Biome> registry,
      final long l,
      @Nullable final LevelChunkSection[] levelChunkSections,
      @Nullable final BlendingData blendingData)
    {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Shadow public abstract BlockState getBlockState(final @NotNull BlockPos param0);

    @Shadow public abstract Level getLevel();

    @Inject(method = "method_12217", at = @At(value = "HEAD"), cancellable = true)
    public void getLightsInject(final BlockPos blockPos, final CallbackInfoReturnable<Boolean> cir)
    {
        final BlockState blockState = getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            cir.setReturnValue(blockWithWorldlyProperties.getLightEmission(
              blockState,getLevel(),blockPos
            ) != 0);
        }
    }
}
