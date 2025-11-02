package com.communi.suggestu.saecularia.caudices.fabric.mixin.platform;

import com.communi.suggestu.saecularia.caudices.core.block.IBlockWithWorldlyProperties;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {

    @Inject(
            method = "placeInWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/world/level/storage/ValueInput;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void placeInWorld$afterLoadWithComponents$inject$afterLoadWithComponents(ServerLevelAccessor serverLevel,
                                                                                    BlockPos offset,
                                                                                    BlockPos pos,
                                                                                    StructurePlaceSettings settings,
                                                                                    RandomSource random,
                                                                                    int flags,
                                                                                    CallbackInfoReturnable<Boolean> cir,
                                                                                    @Local(ordinal = 2) BlockPos blockPos,
                                                                                    @Local BlockState blockState) {
        if (blockState.getBlock() instanceof IBlockWithWorldlyProperties blockWithWorldlyProperties) {
            blockWithWorldlyProperties.mirror(blockState, serverLevel, blockPos, settings.getMirror());
            blockWithWorldlyProperties.rotate(blockState, serverLevel, blockPos, settings.getRotation());
        }
    }
}
