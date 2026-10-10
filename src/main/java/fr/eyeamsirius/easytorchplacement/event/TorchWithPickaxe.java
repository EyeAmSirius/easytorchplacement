package fr.eyeamsirius.easytorchplacement.event;

import fr.eyeamsirius.easytorchplacement.EasyTorchPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EasyTorchPlacement.MOD_ID)
public class TorchWithPickaxe {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();

        BlockPos clickedPos = event.getPos();
        BlockPos placePos = clickedPos.relative(event.getFace());

        if (player.getMainHandItem().getItem() instanceof PickaxeItem
                && player.getInventory().contains(new ItemStack(Items.TORCH))
                && level.getBlockState(placePos).isAir()) {

            if (event.getFace().getAxis().isHorizontal()) {
                BlockPos supportPos = placePos.relative(event.getFace().getOpposite());
                if (level.getBlockState(supportPos).isFaceSturdy(level, supportPos, event.getFace())) {

                    level.setBlock(
                            placePos,
                            Blocks.WALL_TORCH.defaultBlockState().setValue(
                                    HorizontalDirectionalBlock.FACING,
                                    event.getFace()
                            ),
                            3
                    );

                } else {
                    return;
                }

            } else {
                // TORCHE NORMALE (face UP)
                if (event.getFace() == Direction.UP &&
                        Blocks.TORCH.defaultBlockState().canSurvive(level, placePos)) {

                    level.setBlock(placePos, Blocks.TORCH.defaultBlockState(), 3);

                } else {
                    return;
                }
            }
            player.getInventory().clearOrCountMatchingItems(
                    stack -> stack.is(Items.TORCH),
                    1,
                    player.inventoryMenu.getCraftSlots()
            );

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);

            level.playSound(null, placePos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return;
    }
}
