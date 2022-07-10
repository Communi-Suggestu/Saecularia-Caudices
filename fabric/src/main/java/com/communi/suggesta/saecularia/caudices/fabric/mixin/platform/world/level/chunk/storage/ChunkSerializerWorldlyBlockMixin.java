package com.communi.suggesta.saecularia.caudices.fabric.mixin.platform.world.level.chunk.storage;

import com.communi.suggesta.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerWorldlyBlockMixin
{

    @Unique
    private static final ThreadLocal<ProtoChunk> chunkAccessHolder = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<BlockPos>    blockPosHolder    = new ThreadLocal<>();


    @ModifyArg(
            method = "read",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            index = 0
    )
    private static BlockPos redirectGetChunkAccessBlockPos(BlockPos blockPos)
    {
        final BlockState blockState = chunkAccessHolder.get().getBlockState(blockPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            if (blockWithWorldlyProperties.getLightEmission(blockState, chunkAccessHolder.get(), blockPosHolder.get()) != 0) {
                chunkAccessHolder.get().addLight(blockPos);
            }
        }

        return blockPos;
    }

    @ModifyVariable(
            method = "read",
            at = @At(
                    value = "STORE"
            ),
            ordinal = 0
    )
    private static ProtoChunk redirectGetChunkAccessChunkAccess(final ProtoChunk value)
    {
        chunkAccessHolder.set(value);
        return value;
    }
}
