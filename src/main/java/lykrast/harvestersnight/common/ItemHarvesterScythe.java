package lykrast.harvestersnight.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemHarvesterScythe extends ItemSword {

	public ItemHarvesterScythe(ToolMaterial material) {
		super(material);
	}
	
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (state.getBlockHardness(worldIn, pos) != 0) stack.damageItem(1, entityLiving);
        return true;
    }
    
    public boolean canHarvestBlock(IBlockState blockIn) {
        Block block = blockIn.getBlock();
        if (block == Blocks.WEB || block == Blocks.VINE || block == Blocks.LEAVES || block == Blocks.LEAVES2) return true;
        Material material = blockIn.getMaterial();
        return material == Material.LEAVES || material == Material.PLANTS || material == Material.VINE || material == Material.WEB;
    }
    
    //Adapted from the CoFH Core sickles
    //https://github.com/CoFH/CoFHCore/blob/master/src/main/java/cofh/core/item/tool/ItemSickleCore.java
    @Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
		World world = player.world;
		IBlockState state = world.getBlockState(pos);

		if (!canHarvestBlock(state, stack)) {
			if (!player.capabilities.isCreativeMode) {
				stack.damageItem(1, player);
			}
			return false;
		}
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		int used = 0;
		world.playEvent(2001, pos, Block.getStateId(state));

		//7x3x7
		for (int i = x - 3; i <= x + 3; i++) {
			for (int j = z - 3; j <= z + 3; j++) {
				for (int k = y - 1; k <= y + 1; k++) {
					if (harvestBlock(world, new BlockPos(i, k, j), player)) used++;
				}
			}
		}
		if (used > 0 && !player.capabilities.isCreativeMode) {
			stack.damageItem(used, player);
		}
		return false;
	}
    
    //Sickle logic uses it so copied it too, still from CoFH Core
    //https://github.com/CoFH/CoFHCore/blob/master/src/main/java/cofh/core/item/tool/ItemToolCore.java
    protected boolean harvestBlock(World world, BlockPos pos, EntityPlayer player) {

		if (world.isAirBlock(pos)) {
			return false;
		}
		EntityPlayerMP playerMP = null;
		if (player instanceof EntityPlayerMP) {
			playerMP = (EntityPlayerMP) player;
		}
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		// only effective materials
		if (!canHarvestBlock(state, player.getHeldItemMainhand())) {
			return false;
		}
		if (!ForgeHooks.canHarvestBlock(block, player, world, pos)) {
			return false;
		}
		// send the blockbreak event
		int xpToDrop = 0;
		if (playerMP != null) {
			xpToDrop = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
			if (xpToDrop == -1) {
				return false;
			}
		}
		// Creative Mode
		//TODO: check why this is not working, might be an issue to report to cofh
		if (player.capabilities.isCreativeMode) {
			if (!world.isRemote) {
				if (block.removedByPlayer(state, world, pos, player, false)) {
					block.onBlockDestroyedByPlayer(world, pos, state);
				}
				// always send block update to client
				playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
			} else {
				if (block.removedByPlayer(state, world, pos, player, false)) {
					block.onBlockDestroyedByPlayer(world, pos, state);
				}
				Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
			}
		}
		// Otherwise
		if (!world.isRemote) {
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
				block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
				if (xpToDrop > 0) {
					block.dropXpOnBlockBreak(world, pos, xpToDrop);
				}
			}
			// always send block update to client
			playerMP.connection.sendPacket(new SPacketBlockChange(world, pos));
		} else {
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state);
			}
			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, Minecraft.getMinecraft().objectMouseOver.sideHit));
		}
		return true;
	}
    
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
		if (enchantment == Enchantments.FORTUNE) return true;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

}
