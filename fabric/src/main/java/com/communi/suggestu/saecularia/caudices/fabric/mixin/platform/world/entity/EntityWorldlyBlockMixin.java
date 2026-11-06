package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform.world.entity;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityWorldlyBlockMixin
{

    @Shadow private Level level;

    @Shadow public abstract void playSound(final SoundEvent sound, final float volume, final float pitch);

    @Unique
    private Entity getThis() {
        return (Entity) (Object) this;
    }

    @SuppressWarnings("deprecation")
    @Inject(
            method = "playStepSound",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void redirectGetOffsetBlockStateSoundType(BlockPos pos, BlockState blockState, CallbackInfo ci)
    {
        if (!blockState.liquid()) {
            BlockState blockStateAbove = this.level.getBlockState(pos.above());
            blockStateAbove = blockStateAbove.is(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? blockStateAbove : blockState;

            if (blockStateAbove.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
                SoundType soundType = blockWithWorldlyProperties.getSoundType(blockStateAbove, this.level, pos, this.getThis());
                this.playSound(soundType.getStepSound(), soundType.getVolume() * 0.15F, soundType.getPitch());
                ci.cancel();
            }
        }
    }
}
