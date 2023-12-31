package pl.pabilo8.immersiveintelligence.common.gui;

import blusunrize.immersiveengineering.common.gui.ContainerIEBase;
import blusunrize.immersiveengineering.common.gui.IESlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.first.TileEntityArithmeticLogicMachine;
import pl.pabilo8.immersiveintelligence.common.items.ItemIIFunctionalCircuit;

/**
 * @author Pabilo8
 * @since 30-06-2019
 */
public class ContainerArithmeticLogicMachine extends ContainerIEBase<TileEntityArithmeticLogicMachine>
{
	public ContainerArithmeticLogicMachine(InventoryPlayer inventoryPlayer, TileEntityArithmeticLogicMachine tile, int id)
	{
		this(inventoryPlayer, tile, id, 0);
	}

	public ContainerArithmeticLogicMachine(InventoryPlayer inventoryPlayer, TileEntityArithmeticLogicMachine tile, int id, int page)
	{
		super(inventoryPlayer, tile);
		this.tile = tile;
		this.slotCount = tile.getInventory().size();

		switch(id)
		{
			case 0: //Storage
			{
				this.addSlotToContainer(new CircuitSlot(this, this.inv, 0, 6, 26));
				this.addSlotToContainer(new CircuitSlot(this, this.inv, 1, 6, 53));
				this.addSlotToContainer(new CircuitSlot(this, this.inv, 2, 6, 79));
				this.addSlotToContainer(new CircuitSlot(this, this.inv, 3, 6, 105));
			}
			break;
			case 1: //Variables
				this.addSlotToContainer(new CircuitSlot(this, this.inv, page, 6, 61));
				break;
			case 2: //Edit
				break;
		}


		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 141+i*18));
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventoryPlayer, i, 8+i*18, 199));
	}

	public static class CircuitSlot extends IESlot
	{
		public CircuitSlot(Container container, IInventory inv, int id, int x, int y)
		{
			super(container, inv, id, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return stack.getItem() instanceof ItemIIFunctionalCircuit;
		}
	}
}
