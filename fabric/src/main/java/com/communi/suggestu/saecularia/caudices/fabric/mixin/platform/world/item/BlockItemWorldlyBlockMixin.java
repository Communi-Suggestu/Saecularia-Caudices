package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.item;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemWorldlyBlockMixin extends Item
{

    @Unique private BlockState soundState;
    @Unique private SoundType soundType;

    public BlockItemWorldlyBlockMixin(final Properties properties) {
        super(properties);
    }

    @Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Expression("? = ?.getBlockState(?)")
    @ModifyVariable(
        method = "place",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
        name = "placementState")
    private BlockState injectGetSoundTypeAdaptorForInitialState(final BlockState current)
    {
        this.soundState = current;
        return current;
    }

    @Definition(id = "updateBlockStateFromTag", method = "Lnet/minecraft/world/item/BlockItem;updateBlockStateFromTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Expression("? = ?.updateBlockStateFromTag(?, ?, ?, ?)")
    @ModifyVariable(
        method = "place",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
        name = "placementState")
    private BlockState injectGetSoundTypeAdaptorForTagUpdate(final BlockState current)
    {
        this.soundState = current;
        return current;
    }


    @Definition(id = "getSoundType", method = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;")
    @Expression("? = ?.getSoundType()")
    @ModifyVariable(
        method = "place",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
        name = "soundType")
    private SoundType injectGetSoundTypeAdaptor(final SoundType current, BlockPlaceContext blockPlaceContext)
    {
        if (soundState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties)
        {
            this.soundType = blockWithWorldlyProperties.getSoundType(
                    soundState, blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos(), blockPlaceContext.getPlayer()
            );
            return this.soundType;
        }
        this.soundType = null;
        return current;
    }

    @Inject(
      method = "getPlaceSound",
      at = @At(
        value = "HEAD"
      ),
      cancellable = true)
    public void redirectGetBlockStateSoundTypePlace(final BlockState state, final CallbackInfoReturnable<SoundEvent> cir)
    {
        if (this.soundType != null) {
            cir.setReturnValue(this.soundType.getPlaceSound());
        }
    }
}
