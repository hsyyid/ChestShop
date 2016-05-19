package io.github.hsyyid.spongychest.listeners;

import io.github.hsyyid.spongychest.SpongyChest;
import io.github.hsyyid.spongychest.data.isspongychest.IsSpongyChestData;
import io.github.hsyyid.spongychest.data.isspongychest.SpongeIsSpongyChestData;
import io.github.hsyyid.spongychest.data.itemchest.ItemChestData;
import io.github.hsyyid.spongychest.data.itemchest.SpongeItemChestData;
import io.github.hsyyid.spongychest.data.pricechest.PriceChestData;
import io.github.hsyyid.spongychest.data.pricechest.SpongePriceChestData;
import io.github.hsyyid.spongychest.data.uuidchest.SpongeUUIDChestData;
import io.github.hsyyid.spongychest.data.uuidchest.UUIDChestData;
import io.github.hsyyid.spongychest.utils.ChestShopModifier;
import io.github.hsyyid.spongychest.utils.ChestUtils;
import io.github.hsyyid.spongychest.utils.PlayerUtils;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.AgentData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvulnerabilityData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InteractBlockListener
{
	@Listener
	public void onPlayerInteractBlock(InteractBlockEvent.Secondary event, @First Player player)
	{
		if (event.getTargetBlock().getLocation().isPresent() && event.getTargetBlock().getState().getType() == BlockTypes.CHEST)
		{
			Chest chest = (Chest) event.getTargetBlock().getLocation().get().getTileEntity().get();

			if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get())
			{
				ItemStackSnapshot item = chest.get(ItemChestData.class).get().itemStackSnapshot().get();
				double price = chest.get(PriceChestData.class).get().sellPrice().get();
				UUID ownerUuid = chest.get(UUIDChestData.class).get().uuid().get();
				TileEntityChest realChest = (TileEntityChest) chest;
				EntityPlayer realPlayer = (EntityPlayer) player;

				if (player.getUniqueId().equals(ownerUuid))
				{
					return;
				}

				event.setCancelled(true);

				if (price == 0D)
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "The shop owner isn`t selling in this shop."));
					return;
				}

				if (!PlayerUtils.fitsItem(realPlayer, item))
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "You have not enough space in your inventory."));
					return;
				}

				if (ChestUtils.containsItem(realChest, item))
				{
					UniqueAccount ownerAccount = SpongyChest.economyService.getOrCreateAccount(ownerUuid).get();
					UniqueAccount userAccount = SpongyChest.economyService.getOrCreateAccount(player.getUniqueId()).get();

					if (userAccount.transfer(ownerAccount, SpongyChest.economyService.getDefaultCurrency(), new BigDecimal(price), Cause.of(NamedCause.source(player))).getResult() == ResultType.SUCCESS)
					{
						ChestUtils.removeItems(realChest, item);
						player.getInventory().offer(item.createStack());
						player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Purchased item(s)."));
					}
					else
					{
						player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "You don't have enough money to use this shop."));
					}
				}
				else
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "This shop is out of stock."));
				}
			}
			else if (player.hasPermission("spongychest.shop.create"))
			{
				Optional<ChestShopModifier> chestShopModifier = SpongyChest.chestShopModifiers.stream().filter(m -> m.getUuid().equals(player.getUniqueId())).findAny();

				if (chestShopModifier.isPresent())
				{
					event.setCancelled(true);
					chest.offer(new SpongeIsSpongyChestData(true));
					chest.offer(new SpongeItemChestData(chestShopModifier.get().getItem()));
					List<Double> prices = new ArrayList<>(2);
					prices.add(SpongyChest.SELL_PRICE_INDEX, chestShopModifier.get().getSellPrice().doubleValue());
					prices.add(SpongyChest.BUY_PRICE_INDEX, chestShopModifier.get().getBuyPrice().doubleValue());
					chest.offer(new SpongePriceChestData(prices));
					chest.offer(new SpongeUUIDChestData(chestShopModifier.get().getUuid()));
					SpongyChest.chestShopModifiers.remove(chestShopModifier.get());

					Location<World> frameLocation = chest.getLocation().add(0, 1, 0);
					Optional<Entity> itemFrame = chest.getLocation().getExtent().createEntity(EntityTypes.ITEM_FRAME, frameLocation.getPosition());

					if (itemFrame.isPresent())
					{
						ItemFrame entity = (ItemFrame) itemFrame.get();
						ItemStack frameStack = chestShopModifier.get().getItem().createStack();
						frameStack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Item: ", TextColors.WHITE, frameStack.getTranslation().get(), " ", TextColors.GREEN, "Amount: ", TextColors.WHITE, frameStack.getQuantity(), " ", chestShopModifier.get().getSellPrice().doubleValue() == 0 ? Text.EMPTY : Text.of(TextColors.GREEN, "SellPrice: ", TextColors.WHITE, SpongyChest.economyService.getDefaultCurrency().getSymbol().toPlain(), chestShopModifier.get().getSellPrice()), " ", chestShopModifier.get().getBuyPrice().doubleValue() == 0 ? Text.EMPTY : Text.of(TextColors.GREEN, "BuyPrice: ", TextColors.WHITE, SpongyChest.economyService.getDefaultCurrency().getSymbol().toPlain(), chestShopModifier.get().getBuyPrice())));
						entity.offer(Keys.REPRESENTED_ITEM, frameStack.createSnapshot());
						((EntityHanging) entity).updateFacingWithBoundingBox(EnumFacing.byName(chest.getLocation().getBlock().get(Keys.DIRECTION).get().name()));

						if (((EntityHanging) entity).onValidSurface())
						{
							chest.getLocation().getExtent().spawnEntity(entity, Cause.of(NamedCause.source(player)));
						}
					}

					player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Created shop."));
				}
			}
		}
	}

	@Listener
	public void onPlayerInteractEntity(InteractEntityEvent.Secondary event, @First Player player)
	{
		if (event.getTargetEntity().getType() == EntityTypes.ITEM_FRAME)
		{
			ItemFrame frame = (ItemFrame) event.getTargetEntity();
			Optional<TileEntity> tileEntity = frame.getWorld().getTileEntity(frame.getLocation().getBlockPosition().add(0, -1, 0));

			if (tileEntity.isPresent() && tileEntity.get() instanceof Chest)
			{
				Chest chest = (Chest) tileEntity.get();

				if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get())
				{
					ItemStackSnapshot item = chest.get(ItemChestData.class).get().itemStackSnapshot().get();
					double price = chest.get(PriceChestData.class).get().sellPrice().get();
					UUID ownerUuid = chest.get(UUIDChestData.class).get().uuid().get();
					TileEntityChest realChest = (TileEntityChest) chest;
					EntityPlayer realPlayer = (EntityPlayer) player;

					if (player.getUniqueId().equals(ownerUuid))
					{
						return;
					}

					event.setCancelled(true);

					if (price == 0D)
					{
						player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "The shop owner isn`t selling in this shop."));
						return;
					}

					if (!PlayerUtils.fitsItem(realPlayer, item))
					{
						player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "You have not enough space in your inventory."));
						return;
					}

					if (ChestUtils.containsItem(realChest, item))
					{
						UniqueAccount ownerAccount = SpongyChest.economyService.getOrCreateAccount(ownerUuid).get();
						UniqueAccount userAccount = SpongyChest.economyService.getOrCreateAccount(player.getUniqueId()).get();

						if (userAccount.transfer(ownerAccount, SpongyChest.economyService.getDefaultCurrency(), new BigDecimal(price), Cause.of(NamedCause.source(player))).getResult() == ResultType.SUCCESS)
						{
							ChestUtils.removeItems(realChest, item);
							player.getInventory().offer(item.createStack());
							player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Purchased item(s)."));
						}
						else
						{
							player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "You don't have enough money to use this shop."));
						}
					}
					else
					{
						player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "This shop is out of stock."));
					}
				}
			}
		}
	}
}
