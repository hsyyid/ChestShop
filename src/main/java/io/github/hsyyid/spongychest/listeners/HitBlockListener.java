package io.github.hsyyid.spongychest.listeners;

import io.github.hsyyid.spongychest.SpongyChest;
import io.github.hsyyid.spongychest.data.isspongychest.IsSpongyChestData;
import io.github.hsyyid.spongychest.data.isspongychest.SpongeIsSpongyChestData;
import io.github.hsyyid.spongychest.data.itemchest.ItemChestData;
import io.github.hsyyid.spongychest.data.pricechest.PriceChestData;
import io.github.hsyyid.spongychest.data.uuidchest.UUIDChestData;
import io.github.hsyyid.spongychest.utils.ChestUtils;
import io.github.hsyyid.spongychest.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class HitBlockListener
{
	@Listener
	public void onPlayerHitBlock(InteractBlockEvent.Primary event, @First Player player)
	{
		if (event.getTargetBlock().getState().getType() == BlockTypes.CHEST && event.getTargetBlock().getLocation().isPresent() && event.getTargetBlock().getLocation().get().getTileEntity().isPresent())
		{
			Chest chest = (Chest) event.getTargetBlock().getLocation().get().getTileEntity().get();

			if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get())
			{
				UUID uuid = chest.get(UUIDChestData.class).get().uuid().get();

				if (uuid.equals(player.getUniqueId()) || player.hasPermission("spongychest.shop.destroy"))
				{
					player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Successfully deleted shop!"));
					chest.offer(new SpongeIsSpongyChestData(false));
				}
				else
				{
					event.setCancelled(true);

					if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get())
					{
						ItemStackSnapshot item = chest.get(ItemChestData.class).get().itemStackSnapshot().get();
						double price = chest.get(PriceChestData.class).get().buyPrice().get();
						UUID ownerUuid = chest.get(UUIDChestData.class).get().uuid().get();
						TileEntityChest realChest = (TileEntityChest) chest;
						EntityPlayer realPlayer = (EntityPlayer) player;

						if (player.getUniqueId().equals(ownerUuid))
						{
							return;
						}

						if (price == 0D)
						{
							player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "The shop owner isn`t buying in this shop."));
							return;
						}

						if (!ChestUtils.fitsItem(realChest, item))
						{
							player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "The shop is full."));
							return;
						}

						if (PlayerUtils.containsItem(realPlayer, item))
						{
							UniqueAccount ownerAccount = SpongyChest.economyService.getOrCreateAccount(ownerUuid).get();
							UniqueAccount userAccount = SpongyChest.economyService.getOrCreateAccount(player.getUniqueId()).get();

							if (ownerAccount.transfer(userAccount, SpongyChest.economyService.getDefaultCurrency(), new BigDecimal(price), Cause.of(NamedCause.source(player))).getResult() == ResultType.SUCCESS)
							{
								PlayerUtils.removeItems(realPlayer, item);
								chest.getInventory().offer(item.createStack());
								player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Sold item(s)."));
							}
							else
							{
								player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "The shop owner has not enough money to buy your items."));
							}
						}
						else
						{
							player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "You have not enough items."));
						}
					}
				}
			}
		}
	}

	@Listener
	public void onPlayerHitEntity(InteractEntityEvent.Primary event, @First Player player)
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
					UUID uuid = chest.get(UUIDChestData.class).get().uuid().get();

					if (uuid.equals(player.getUniqueId()) || player.hasPermission("spongychest.shop.destroy"))
					{
						player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Successfully deleted shop!"));
						chest.offer(new SpongeIsSpongyChestData(false));
					}
					else
					{
						event.setCancelled(true);

						if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get())
						{
							ItemStackSnapshot item = chest.get(ItemChestData.class).get().itemStackSnapshot().get();
							double price = chest.get(PriceChestData.class).get().buyPrice().get();
							UUID ownerUuid = chest.get(UUIDChestData.class).get().uuid().get();
							TileEntityChest realChest = (TileEntityChest) chest;
							EntityPlayer realPlayer = (EntityPlayer) player;

							if (player.getUniqueId().equals(ownerUuid))
							{
								return;
							}

							if (price == 0D)
							{
								player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "The shop owner isn`t buying in this shop."));
								return;
							}

							if (!ChestUtils.fitsItem(realChest, item))
							{
								player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "The shop is full."));
								return;
							}

							if (PlayerUtils.containsItem(realPlayer, item))
							{
								UniqueAccount ownerAccount = SpongyChest.economyService.getOrCreateAccount(ownerUuid).get();
								UniqueAccount userAccount = SpongyChest.economyService.getOrCreateAccount(player.getUniqueId()).get();

								if (ownerAccount.transfer(userAccount, SpongyChest.economyService.getDefaultCurrency(), new BigDecimal(price), Cause.of(NamedCause.source(player))).getResult() == ResultType.SUCCESS)
								{
									PlayerUtils.removeItems(realPlayer, item);
									chest.getInventory().offer(item.createStack());
									player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Sold item(s)."));
								}
								else
								{
									player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "The shop owner has not enough money to buy your items."));
								}
							}
							else
							{
								player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.RED, "You have not enough items."));
							}
						}
					}
				}
			}
		}
	}
}
