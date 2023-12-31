package pl.pabilo8.immersiveintelligence.common.gui;

import blusunrize.immersiveengineering.common.gui.ContainerIEBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second.TileEntityVulcanizer;

/**
 * @author Pabilo8
 * @since 10-07-2019
 */
public class ContainerVulcanizer extends ContainerIEBase<TileEntityVulcanizer>
{
	public ContainerVulcanizer(InventoryPlayer inventoryPlayer, TileEntityVulcanizer tile)
	{
		super(inventoryPlayer, tile);
		//Input/Output Slots

		this.addSlotToContainer(new Slot(this.inv, 0, 6, 19){
			@Override
			public boolean isItemValid(ItemStack itemStack)
			{
				return super.isItemValid(itemStack)&&tile.isStackValid(0,itemStack);
			}
		});
		this.addSlotToContainer(new Slot(this.inv, 1, 6, 39){
			@Override
			public boolean isItemValid(ItemStack itemStack)
			{
				return super.isItemValid(itemStack)&&tile.isStackValid(1,itemStack);
			}
		});
		this.addSlotToContainer(new Slot(this.inv, 2, 6, 59){
			@Override
			public boolean isItemValid(ItemStack itemStack)
			{
				return super.isItemValid(itemStack)&&tile.isStackValid(2,itemStack);
			}
		});

		this.slotCount = tile.getInventory().size();
		this.tile = tile;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 86+i*18));
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventoryPlayer, i, 8+i*18, 144));
	}
}
