package com.communi.suggestu.saecularia.caudices.forge.mixin;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class CloneStackEventHandlerMixin
{
    @WrapOperation(
        method = "handlePickItemFromBlock(Lnet/minecraft/network/protocol/game/ServerboundPickItemFromBlockPacket;)V",
        at = @At(
            target = "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelReader;ZLnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;",
            value = "INVOKE"
        )
    )
    public ItemStack onGetCloneStackHook(
        final BlockState instance,
        final BlockPos pos,
        final LevelReader levelReader,
        final boolean b,
        final Player player,
        final Operation<ItemStack> original)
    {
        if (instance.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            final HitResult result = player.pick(
                player.blockInteractionRange(),
                0f,
                false
            );

            return blockWithWorldlyProperties.getCloneItemStack(
                instance,
                result,
                levelReader,
                pos,
                player
            );
        }

        return original.call(instance, pos, levelReader, b, player);
    }
}
