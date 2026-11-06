package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.item;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class BlockItemWorldlyBlockMixin extends Item
{

    public BlockItemWorldlyBlockMixin(final Properties properties) {
        super(properties);
    }


    @WrapOperation(
        method = "place",
        at = @At(value = "INVOKE",
                target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"))
    private SoundType injectGetSoundTypeAdaptor(
            final BlockState instance,
            final Operation<SoundType> original,
            BlockPlaceContext placeContext,
            @Local(name = "pos") BlockPos pos,
            @Local(name = "placedState") BlockState placedState)
    {
        if (placedState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            return blockWithWorldlyProperties.getSoundType(
                    instance, placeContext.getLevel(), placeContext.getClickedPos(), placeContext.getPlayer()
            );
        }

        return original.call(instance);
    }
}
