package dev.huskuraft.effortless.vanilla.core;

import java.util.Optional;

import dev.huskuraft.effortless.api.core.Block;
import dev.huskuraft.effortless.api.core.BlockInteraction;
import dev.huskuraft.effortless.api.core.BlockPosition;
import dev.huskuraft.effortless.api.core.BlockState;
import dev.huskuraft.effortless.api.core.FluidState;
import dev.huskuraft.effortless.api.core.ItemStack;
import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.core.World;
import dev.huskuraft.effortless.api.core.fluid.BucketCollectable;
import dev.huskuraft.effortless.api.core.fluid.Fluid;
import dev.huskuraft.effortless.api.core.fluid.LiquidPlaceable;
import dev.huskuraft.effortless.api.sound.Sound;
import dev.huskuraft.effortless.vanilla.sound.MinecraftSound;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.LiquidBlockContainer;

public record MinecraftBlock(
        net.minecraft.world.level.block.Block referenceValue
) implements Block {

    @Override
    public BlockState getDefaultBlockState() {
        return MinecraftBlockState.ofNullable(referenceValue().defaultBlockState());
    }

    @Override
    public BlockState getBlockState(Player player, BlockInteraction interaction) {
        return MinecraftBlockState.ofNullable(referenceValue().getStateForPlacement(new BlockPlaceContext(
                player.reference(),
                MinecraftConvertor.toPlatformInteractionHand(interaction.getHand()),
                player.getItemStack(interaction.getHand()).reference(),
                MinecraftConvertor.toPlatformBlockInteraction(interaction)
        )));
    }

    @Override
    public BucketCollectable getBucketCollectable() {
        if (referenceValue() instanceof net.minecraft.world.level.block.BucketPickup bucketPickup) {
            return new BucketCollectable() {

                @Override
                public ItemStack pickupBlock(World world, Player player, BlockPosition blockPosition, BlockState blockState) {
                    return new MinecraftItemStack(bucketPickup.pickupBlock(player.reference(), world.reference(), MinecraftConvertor.toPlatformBlockPosition(blockPosition), blockState.reference()));
                }

                @Override
                public Optional<Sound> getPickupSound() {
                    return bucketPickup.getPickupSound().map(MinecraftSound::new);
                }
            };
        }
        return null;
    }

    @Override
    public LiquidPlaceable getLiquidPlaceable() {
        if (referenceValue() instanceof LiquidBlockContainer liquidPlace) {
            return new LiquidPlaceable() {
                @Override
                public boolean canPlaceLiquid(World world, Player player, BlockPosition blockPosition, BlockState blockState, Fluid fluid) {
                    return liquidPlace.canPlaceLiquid(player.reference(), world.reference(), MinecraftConvertor.toPlatformBlockPosition(blockPosition), blockState.reference(), fluid.reference());
                }

                @Override
                public boolean placeLiquid(World world, BlockPosition blockPosition, BlockState blockState, FluidState fluidState) {
                    return liquidPlace.placeLiquid(world.reference(), MinecraftConvertor.toPlatformBlockPosition(blockPosition), blockState.reference(), fluidState.reference());
                }
            };
        }
        return null;
    }
}
