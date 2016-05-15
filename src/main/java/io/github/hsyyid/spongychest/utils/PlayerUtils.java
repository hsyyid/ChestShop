package io.github.hsyyid.spongychest.utils;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class PlayerUtils
{
	public static boolean fitsItem(EntityPlayer player, ItemStackSnapshot snapshot)
	{
		int foundFreeItems = 0;
		Item item = Item.getByNameOrId(snapshot.getType().getId());
		
		if (item != null)
		{
			for (int i = 0; i < player.inventory.getSizeInventory() - 4; i++)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);
				
				if (stack == null)
				{
					foundFreeItems += player.inventory.getInventoryStackLimit();
				}
				else if (stack.getItem().equals(item)) // TODO: Metadata && stack.getMetadata() == snapshot.)
				{
					foundFreeItems += player.inventory.getInventoryStackLimit() - stack.stackSize;
				}

				if (foundFreeItems >= snapshot.getCount())
				{
					return true;
				}
			}
		}

		return false;
	}

	public static boolean containsItem(EntityPlayer player, ItemStackSnapshot snapshot)
	{
		int foundItems = 0;
		Item item = Item.getByNameOrId(snapshot.getType().getId());

		if (item != null)
		{
			for (int i = 0; i < player.inventory.getSizeInventory() - 4; i++)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);

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

	public static void removeItems(EntityPlayer player, ItemStackSnapshot snapshot)
	{
		int neededItems = snapshot.getCount();
		int foundItems = 0;
		Item item = Item.getByNameOrId(snapshot.getType().getId());

		if (item != null)
		{
			for (int i = 0; i < player.inventory.getSizeInventory() - 4; i++)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);

				if (stack != null && stack.getItem().equals(item)) // TODO: Metadata && stack.getMetadata() == snapshot.)
				{
					if (neededItems >= foundItems + stack.stackSize)
					{
						player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).extractItem(i, stack.stackSize, false);
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
					player.inventory.markDirty();
					return;
				}
			}
		}
	}
}
