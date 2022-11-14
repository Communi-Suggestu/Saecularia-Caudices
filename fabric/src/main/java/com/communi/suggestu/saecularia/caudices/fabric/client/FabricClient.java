package com.communi.suggestu.saecularia.caudices.fabric.client;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FabricClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("SaeculariaCaudices-Fabric-Client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initialized Saecularia-Caudices Client systems");

        ClientPickBlockGatherCallback.EVENT.register((player, result) -> {
            if (result instanceof BlockHitResult blockHitResult
                    && Minecraft.getInstance().level != null
                    && Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof IBlockWithWorldlyProperties multiStateBlock) {
                return multiStateBlock.getCloneItemStack(
                        Minecraft.getInstance().level.getBlockState(blockHitResult.getBlockPos()),
                        result,
                        Minecraft.getInstance().level,
                        ((BlockHitResult) result).getBlockPos(),
                        player
                );
            }

            return ItemStack.EMPTY;
        });
    }
}
