package com.communi.suggestu.saecularia.caudices.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines a block whose properties are determined by a backing block entity on a given position.
 */
public interface IBlockWithWorldlyProperties extends ItemLike, BeaconBeamBlock
{
    /**
     * Gets the friction of the block on the given position.
     *
     * @param state The state of the block in question.
     * @param levelReader The level reader in question
     * @param pos The position in question.
     * @param entity The entity for the friction in question.
     * @return The friction value.
     */
    float getFriction(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity);

    /**
     * Determines the amount of light the given blockstate exudes.
     *
     * @param state The state in question.
     * @param blockGetter The block getter to get contextual information from.
     * @param pos The position the block is in.
     * @return The light emission factor.
     */
    int getLightEmission(BlockState state, BlockGetter blockGetter, BlockPos pos);

    /**
     * Determines if the blockstate is harvestable by a given player.
     *
     * @param state The blockstate in question.
     * @param blockGetter The block getter to pull contextual information from.
     * @param pos The position of the block.
     * @param player The player in question.
     * @return True when the player can harvest the blockstate, false when not.
     */
    boolean canHarvestBlock(BlockState state, BlockGetter blockGetter, BlockPos pos, Player player);

    /**
     * Determines the stack returned from the middle-click of a player on the given blockstate.
     *
     * @param state The blockstate in question.
     * @param target The hit result of the middle-click operation.
     * @param blockGetter The block getter to pull contextual information from.
     * @param pos The position in question.
     * @param player The player in question.
     * @return The stack as the result of the middle-click operation.
     */
    ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter blockGetter, BlockPos pos, Player player);

    /**
     * Used to rotate the given blockstate around.
     *
     * @param state The blockstate in question.
     * @param levelAccessor The level accessor to pull contextual information from.
     * @param pos The position in question.
     * @param rotation The rotation to apply.
     * @return The rotated blockstate.
     */
    BlockState rotate(BlockState state, LevelAccessor levelAccessor, BlockPos pos, Rotation rotation);

    /**
     * Indicates if the blockstate is capable of processing weak redstone power.
     *
     * @param state The blockstate in question.
     * @param signalGetter The level reader to pull contextual information from.
     * @param pos The position in question.
     * @param side The side to check weak redstone power from.
     * @return True when the check should be performed, false when not.
     */
    boolean shouldCheckWeakPower(BlockState state, SignalGetter signalGetter, BlockPos pos, Direction side);

    /**
     * Indicates if the fluid state should render an overlay side if it is touching the blockstate in question.
     *
     * @param state The blockstate in question.
     * @param blockAndTintGetter The block and tint getter to pull contextual information from.
     * @param pos The position in question.
     * @param fluidState The fluidstate for which the check is performed
     * @return True when the overlay should be rendered, false when not.
     */
    boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, FluidState fluidState);

    /**
     * Determines the color multiplier of a blockstate when the beacon beam passes through it.
     *
     * @param state The blockstate in question.
     * @param levelReader The level reader to pull contextual information from.
     * @param pos The position in question.
     * @param beaconPos The position of the beacon block in question.
     * @return A RGB Float array pointing to the color multiplier of the beacon beam color.
     */
    float[] getBeaconColorMultiplier(BlockState state, LevelReader levelReader, BlockPos pos, BlockPos beaconPos);

    /**
     * Determines the sound type of the blockstate in question.
     *
     * @param state The blockstate in question.
     * @param levelReader The level reader to pull contextual information from.
     * @param pos The position in question.
     * @param entity The entity in question.
     * @return The sound type of the blockstate.
     */
    SoundType getSoundType(BlockState state, LevelReader levelReader, BlockPos pos, @Nullable Entity entity);

    /**
     * Determines the explosion resistance of the blockstate in question against the given explosion.
     *
     * @param state The blockstate in question.
     * @param blockGetter The block getter to pull contextual information from.
     * @param position The position in question.
     * @param explosion The explosion to get the resistance against for.
     * @return The explosion resistance.
     */
    float getExplosionResistance(BlockState state, BlockGetter blockGetter, BlockPos position, Explosion explosion);

    /**
     * Default override implementation allows for simpler patching of the required methods.
     *
     * @return The default colors.
     */
    @Override
    default @NotNull DyeColor getColor() {
        return DyeColor.LIGHT_BLUE;
    }

    /**
     * Determines if the target blockstate (of this worldly block) at the target position can have the grass state on the grass position below it become a grass block.
     * Default implementation follow vanilla guide-lines.
     *
     * @param levelReader The level reader of the world.
     * @param grassState The state of the grass supporting block.
     * @param grassBlockPos The position of the grass supporting block.
     * @param targetState The target state of the block above the grass.
     * @param targetPosition The position of the target state in the level reader.
     * @return {@code true} when the grass can grow, false when not.
     */
    default boolean canBeGrass(LevelReader levelReader, BlockState grassState, BlockPos grassBlockPos, BlockState targetState, BlockPos targetPosition) {
        if (targetState.is(Blocks.SNOW) && targetState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (targetState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(levelReader, grassState, grassBlockPos, targetState, targetPosition, Direction.UP, targetState.getLightBlock(levelReader, targetPosition));
            return i < levelReader.getMaxLightLevel();
        }
    }
}
