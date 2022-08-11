package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.level.block.entity;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityWorldlyBlockMixin extends BlockEntity {

    @Unique
    private static final ThreadLocal<BlockPos> requestedBlockPos = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<BeaconBlockEntity> requestedBlockEntity = new ThreadLocal<>();


    public BeaconBlockEntityWorldlyBlockMixin(final BlockEntityType<?> blockEntityType, final BlockPos blockPos, final BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow private int lastCheckY;

    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            )
    )
    private static void injectInitialBlockPosSetterIntoTickForDiffuseColor(final Level level, final BlockPos pos, final BlockState state, final BeaconBlockEntity blockEntity, final CallbackInfo ci) {
        requestedBlockPos.set(pos);
        requestedBlockEntity.set(blockEntity);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/core/BlockPos"
            )
    )
    private static void injectOverrideBlockPosSetterIntoTickForDiffuseColor(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        requestedBlockPos.set(new BlockPos(pos.getX(), ((IBeaconBlockEntityAccessor) blockEntity).getLastCheckY() + 1, pos.getZ()));
    }

    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"
            ),
            ordinal = 0
    )
    private static BlockPos injectAboveBlockPosSetterIntoTickForDiffuseColor(BlockPos current) {
        requestedBlockPos.set(current);
        return current;
    }

    @ModifyVariable(
            method = "tick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColors()[F"
            ),
            ordinal = 0
    )
    private static float[] redirectGetDyeColorGetTextureDiffuseColors(float[] colors) {
        final BlockState blockState = requestedBlockEntity.get().getLevel().getBlockState(requestedBlockPos.get());
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            return blockWithWorldlyProperties.getBeaconColorMultiplier(
                    blockState, requestedBlockEntity.get().getLevel(), requestedBlockPos.get(), requestedBlockEntity.get().getBlockPos()
            );
        }

        return colors;
    }
}
