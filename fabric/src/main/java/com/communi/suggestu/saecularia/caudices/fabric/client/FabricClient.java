package com.communi.suggestu.saecularia.caudices.fabric.client;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class FabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("SaeculariaCaudices-Fabric-Client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initialized Saecularia-Caudices Client systems");

        PlayerPickItemEvents.BLOCK.register((serverPlayer, blockPos, blockState, b) -> {
            if (blockState.getBlock() instanceof IBlockWithWorldlyProperties multiStateBlock) {
                final HitResult result = serverPlayer.pick(
                        serverPlayer.blockInteractionRange(),
                        0f,
                        false
                );
                return multiStateBlock.getCloneItemStack(
                        blockState,
                        result,
                        Minecraft.getInstance().level,
                        ((BlockHitResult) result).getBlockPos(),
                        serverPlayer
                );
            }

            return null;
        });
    }
}
