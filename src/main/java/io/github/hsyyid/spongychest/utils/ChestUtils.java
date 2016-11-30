package io.github.hsyyid.spongychest.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class ChestUtils
{
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
					// getStackSize
					foundItems += stack.func_190916_E();

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
					if (neededItems >= foundItems + stack.func_190916_E() /* getStackSize */)
					{
						chest.removeStackFromSlot(i);
						foundItems += stack.func_190916_E(); /* getStackSize */
					}
					else
					{
						int amount = (foundItems + stack.func_190916_E() /* getStackSize */) - neededItems;
                        stack.func_190920_e(amount); /* setStackSize */
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
