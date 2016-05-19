package io.github.hsyyid.spongychest.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class ChestUtils
{
	public static boolean fitsItem(TileEntityChest chest, ItemStackSnapshot snapshot)
	{
		int foundFreeItems = 0;
		Item item = Item.getByNameOrId(snapshot.getType().getId());
		
		if (item != null)
		{
			for (int i = 0; i < chest.getSizeInventory(); i++)
			{
				ItemStack stack = chest.getStackInSlot(i);
				
				if (stack == null)
				{
					foundFreeItems += chest.getInventoryStackLimit();
				}
				else if (stack.getItem().equals(item)) // TODO: Metadata && stack.getMetadata() == snapshot.)
				{
					foundFreeItems += chest.getInventoryStackLimit() - stack.stackSize;
				}

				if (foundFreeItems >= snapshot.getCount())
				{
					return true;
				}
			}
		}

		return false;
	}

	public static boolean containsItem(TileEntityChest chest, ItemStackSnapshot snapshot)
	{
		int foundItems = 0;
		Item item = Item.getByNameOrId(snapshot.getType().getId());

		if (item != null)
		{
			for (int i = 0; i < chest.getSizeInventory(); i++)
			{
				ItemStack stack = chest.getStackInSlot(i);

				if (stack != null && stack.getItem().equals(item)) // TODO: Metadata && stack.getMetadata() == snapshot.)
				{
					foundItems += stack.stackSize;

					if (foundItems >= snapshot.getCount())
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	public static void removeItems(TileEntityChest chest, ItemStackSnapshot snapshot)
	{
		int neededItems = snapshot.getCount();
		int foundItems = 0;
		Item item = Item.getByNameOrId(snapshot.getType().getId());

		if (item != null)
		{
			for (int i = 0; i < chest.getSizeInventory(); i++)
			{
				ItemStack stack = chest.getStackInSlot(i);

				if (stack != null && stack.getItem().equals(item)) // TODO: Metadata && stack.getMetadata() == snapshot.)
				{
					if (neededItems >= foundItems + stack.stackSize)
					{
						chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).extractItem(i, stack.stackSize, false);
						foundItems += stack.stackSize;
					}
					else
					{
						int amount = (foundItems + stack.stackSize) - neededItems;
						stack.stackSize = amount;
						foundItems = neededItems;
					}
				}

				if (foundItems == neededItems)
				{
					return;
				}
			}
		}
	}
}
