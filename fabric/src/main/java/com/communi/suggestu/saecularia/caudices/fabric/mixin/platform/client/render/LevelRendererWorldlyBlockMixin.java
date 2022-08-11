package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.client.render;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererWorldlyBlockMixin implements ResourceManagerReloadListener, AutoCloseable
{

    @Shadow private ClientLevel level;

    @Shadow public abstract void levelEvent(final int p_234305_, final BlockPos p_234306_, final int p_234307_);

    @Unique private int currentEventArgument;
    @Unique private BlockPos currentEventPosition;

    @ModifyVariable(
            method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
            ),
            ordinal = 0
    )
    private static int injectGetBlockStateSoundType(final int current, BlockAndTintGetter pLevel, BlockState pState, BlockPos pPos)
    {
        final BlockState blockState = pLevel.getBlockState(pPos);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getLightEmission(pState, pLevel, pPos);
        }
        return current;
    }

    @Inject(
            method = "levelEvent",
            at = @At("HEAD")
    )
    public void captureEventArguments(final int i, final BlockPos blockPos, final int j, final CallbackInfo ci)
    {
        currentEventPosition = blockPos;
        currentEventArgument = j;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
      method = "levelEvent",
      at = @At(
        value = "INVOKE_ASSIGN",
        target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
      )
    )
    public SoundType redirectGetBlockStateSoundType(SoundType current)
    {
        final BlockState blockState = Block.stateById(currentEventArgument);
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getSoundType(
              blockState, this.level, currentEventPosition, Minecraft.getInstance().cameraEntity
            );
        }
        return current;
    }
}
