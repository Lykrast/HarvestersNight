package lykrast.harvestersnight.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHarvesterScythe extends SwordItem {

	public ItemHarvesterScythe(IItemTier material, Item.Properties builder) {
		//Mimic Iron Sword
		super(material, 3, -2.4F, builder);
	}
	
    @Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getBlockHardness(worldIn, pos) != 0) stack.damageItem(1, entityLiving, (e) -> e.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }
    
    @Override
	public boolean canHarvestBlock(BlockState blockIn) {
        Block block = blockIn.getBlock();
        if (block == Blocks.COBWEB || block == Blocks.VINE || block instanceof LeavesBlock) return true;
        Material material = blockIn.getMaterial();
        return material == Material.LEAVES
        		|| material == Material.PLANTS
        		|| material == Material.TALL_PLANTS
        		|| material == Material.SEA_GRASS
        		|| material == Material.OCEAN_PLANT
        		|| material == Material.WEB;
    }
    
	@Override
	public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return true;
	}
	
	//TODO:FIX
    
//    //Adapted from the CoFH Core sickles
//    //https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/item/tool/ItemSickleCore.java
//    @Override
//	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
//		World world = player.world;
//		BlockState state = world.getBlockState(pos);
//
//		if (!canHarvestBlock(state, stack)) {
//			if (!player.capabilities.isCreativeMode) {
//				stack.damageItem(1, player);
//			}
//			return false;
//		}
//		int x = pos.getX();
//		int y = pos.getY();
//		int z = pos.getZ();
//
//		int used = 0;
//		//world.playEvent(2001, pos, Block.getStateId(state));
//		if (player.isSneaking()) {
//			if (!player.capabilities.isCreativeMode) {
//				stack.damageItem(1, player);
//			}
//			return false;
//		}
//		//7x3x7
//		for (int i = x - 3; i <= x + 3; i++) {
//			for (int j = z - 3; j <= z + 3; j++) {
//				for (int k = y - 1; k <= y + 1; k++) {
//					if (harvestBlock(world, new BlockPos(i, k, j), player)) used++;
//				}
//			}
//		}
//		if (used > 0 && !player.capabilities.isCreativeMode) {
//			stack.damageItem(used, player);
//		}
//		return false;
//	}
//    
//    //Sickle logic uses it so copied it too, still from CoFH Core
//    //https://github.com/CoFH/CoFHCore/blob/1.12/src/main/java/cofh/core/item/tool/ItemToolCore.java
//    protected boolean harvestBlock(World world, BlockPos pos, PlayerEntity player) {
//		if (world.isAirBlock(pos)) {
//			return false;
//		}
//		ServerPlayerEntity playerMP = null;
//		if (player instanceof ServerPlayerEntity) {
//			playerMP = (ServerPlayerEntity) player;
//		}
//		BlockState state = world.getBlockState(pos);
//		Block block = state.getBlock();
//
//		// only effective materials
//		if (!canHarvestBlock(player.getHeldItemMainhand(), state)) {
//			return false;
//		}
//		if (!ForgeHooks.canHarvestBlock(state, player, world, pos)) {
//			return false;
//		}
//		// send the blockbreak event
//		int xpToDrop = 0;
//		if (playerMP != null) {
//			xpToDrop = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
//			if (xpToDrop == -1) {
//				return false;
//			}
//		}
//		if (!world.isRemote) {
//			if (block.removedByPlayer(state, world, pos, player, !player.isCreative(), world.getFluidState(pos))) {
//				block.onBlockHarvested(world, pos, state, player);
//				if (!player.isCreative()) {
//					block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
//					if (xpToDrop > 0) {
//						block.dropXpOnBlockBreak(world, pos, xpToDrop);
//					}
//				}
//			}
//			// always send block update to client
//			playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
//		} else {
//			if (block.removedByPlayer(state, world, pos, player, !player.capabilities.isCreativeMode)) {
//				block.onBlockDestroyedByPlayer(world, pos, state);
//			}
//			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
//		}
//		return true;
//	}
    
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
		if (enchantment == Enchantments.FORTUNE) return true;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

}
