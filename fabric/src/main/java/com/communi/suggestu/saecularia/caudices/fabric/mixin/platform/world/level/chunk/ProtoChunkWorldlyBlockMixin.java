package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.chunk;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkWorldlyBlockMixin extends ChunkAccess implements BlockGetter
{

    @Shadow @Final private List<BlockPos> lights;
    @Shadow private volatile ChunkStatus status;
    @Shadow @Nullable private volatile LevelLightEngine lightEngine;

    public ProtoChunkWorldlyBlockMixin(final ChunkPos chunkPos, final UpgradeData upgradeData, final LevelHeightAccessor levelHeightAccessor, final Registry<Biome> registry, final long l, @Nullable final LevelChunkSection[] levelChunkSections, @Nullable final BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Inject(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I",
                    ordinal = 0
            )
    )
    public void onSetBlockStateAddCustomLights(final BlockPos pos, final BlockState state, final boolean isMoving, final CallbackInfoReturnable<BlockState> cir) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        if (state.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties &&
                blockWithWorldlyProperties.getLightEmission(state, this, pos) > 0) {
            this.lights.add(new BlockPos((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
        }
    }

    @ModifyVariable(
            method = "setBlockState",
            at = @At(
                    value = "STORE"
            ),
            index = 9)
    public BlockState onSetBlockStateDoLightEngineUpdate(final BlockState blockState, BlockPos pos, BlockState state, boolean isMoving) {
        if (state.getLightEmission() == blockState.getLightEmission() && (
                state.getBlock() instanceof IBlockWithWorldlyProperties ||
                blockState.getBlock() instanceof IBlockWithWorldlyProperties
                )) {
            int newBlockEmissions = state.getLightEmission();
            if (state.getBlock() instanceof IBlockWithWorldlyProperties newBlockWithWorldlyProperties) {
                newBlockEmissions = newBlockWithWorldlyProperties.getLightEmission(state, this, pos);
            }

            int oldBlockEmissions = blockState.getLightEmission();
            if (blockState.getBlock() instanceof IBlockWithWorldlyProperties oldBlockWithWorldlyProperties) {
                oldBlockEmissions = oldBlockWithWorldlyProperties.getLightEmission(state, this, pos);
            }

            if (this.status.isOrAfter(ChunkStatus.FEATURES) && state != blockState && (state.getLightBlock(this, pos) != blockState.getLightBlock(this, pos) || newBlockEmissions != oldBlockEmissions || state.useShapeForLightOcclusion() || blockState.useShapeForLightOcclusion())) {
                if (this.lightEngine != null) {
                    this.lightEngine.checkBlock(pos);
                }
            }
        }

        return blockState;
    }
}
