package io.github.hsyyid.spongychest;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.github.hsyyid.spongychest.commands.SetShopExecutor;
import io.github.hsyyid.spongychest.commands.SpongyChestExecutor;
import io.github.hsyyid.spongychest.data.isspongychest.ImmutableIsSpongyChestData;
import io.github.hsyyid.spongychest.data.isspongychest.ImmutableSpongeIsSpongyChestData;
import io.github.hsyyid.spongychest.data.isspongychest.IsSpongyChestData;
import io.github.hsyyid.spongychest.data.isspongychest.IsSpongyChestDataBuilder;
import io.github.hsyyid.spongychest.data.isspongychest.SpongeIsSpongyChestData;
import io.github.hsyyid.spongychest.data.itemchest.ImmutableItemChestData;
import io.github.hsyyid.spongychest.data.itemchest.ImmutableSpongeItemChestData;
import io.github.hsyyid.spongychest.data.itemchest.ItemChestData;
import io.github.hsyyid.spongychest.data.itemchest.ItemChestDataBuilder;
import io.github.hsyyid.spongychest.data.itemchest.SpongeItemChestData;
import io.github.hsyyid.spongychest.data.pricechest.ImmutablePriceChestData;
import io.github.hsyyid.spongychest.data.pricechest.ImmutableSpongePriceChestData;
import io.github.hsyyid.spongychest.data.pricechest.PriceChestData;
import io.github.hsyyid.spongychest.data.pricechest.PriceChestDataBuilder;
import io.github.hsyyid.spongychest.data.pricechest.SpongePriceChestData;
import io.github.hsyyid.spongychest.data.uuidchest.ImmutableSpongeUUIDChestData;
import io.github.hsyyid.spongychest.data.uuidchest.ImmutableUUIDChestData;
import io.github.hsyyid.spongychest.data.uuidchest.SpongeUUIDChestData;
import io.github.hsyyid.spongychest.data.uuidchest.UUIDChestData;
import io.github.hsyyid.spongychest.data.uuidchest.UUIDChestDataBuilder;
import io.github.hsyyid.spongychest.listeners.HitBlockListener;
import io.github.hsyyid.spongychest.listeners.InteractBlockListener;
import io.github.hsyyid.spongychest.utils.ChestShopModifier;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Plugin(id = "io.github.hsyyid.spongychest", name = "SpongyChest", version = "0.4.3")
public class SpongyChest
{
	public static EconomyService economyService;
	public static Set<ChestShopModifier> chestShopModifiers = Sets.newHashSet();

	// Keys
	public static int SELL_PRICE_INDEX = 0;
	public static int BUY_PRICE_INDEX = 1;
	public static final Key<Value<Boolean>> IS_SPONGY_CHEST = KeyFactory.makeSingleKey(Boolean.class, Value.class, DataQuery.of("IsSpongyChest"));
	public static final Key<Value<UUID>> UUID_CHEST = KeyFactory.makeSingleKey(UUID.class, Value.class, DataQuery.of("UUIDChest"));
	public static final Key<Value<ItemStackSnapshot>> ITEM_CHEST = KeyFactory.makeSingleKey(ItemStackSnapshot.class, Value.class, DataQuery.of("ItemChest"));
	public static final Key<ListValue<Double>> PRICES_CHEST = KeyFactory.makeListKey(Double.class, DataQuery.of("PricesChest"));
	public static final Key<Value<Double>> SELL_PRICE_CHEST = KeyFactory.makeSingleKey(Double.class, Value.class, DataQuery.of("PriceChest"));
	public static final Key<Value<Double>> BUY_PRICE_CHEST = KeyFactory.makeSingleKey(Double.class, Value.class, DataQuery.of("BuyPriceChest"));

	@Inject
	private Logger logger;

	public Logger getLogger()
	{
		return logger;
	}

	@Inject
	@DefaultConfig(sharedRoot = true)
	private File dConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> confManager;

	@Listener
	public void onServerInit(GameInitializationEvent event)
	{
		getLogger().info("SpongyChest loading...");

		HashMap<List<String>, CommandSpec> subcommands = new HashMap<List<String>, CommandSpec>();

		subcommands.put(Arrays.asList("setshop"), CommandSpec.builder()
			.description(Text.of("Creates SpongyChest shops"))
			.permission("spongychest.setshop.command")
			.arguments(
					GenericArguments.firstParsing(
							GenericArguments.seq(
									GenericArguments.doubleNum(Text.of("sell-price")),
									GenericArguments.optional(
											GenericArguments.doubleNum(Text.of("buy-price"))
											)
									),
							GenericArguments.flags()
							.valueFlag(GenericArguments.doubleNum(Text.of("sell-price")), "s")
							.valueFlag(GenericArguments.doubleNum(Text.of("buy-price")), "b")
							.flag("-sell-price")
							.flag("-buy-price")
							.buildWith(GenericArguments.none())
							)
					)
			.executor(new SetShopExecutor())
			.build());

		CommandSpec spongyChestCommandSpec = CommandSpec.builder()
			.description(Text.of("SpongyChest Command"))
			.permission("spongychest.command")
			.executor(new SpongyChestExecutor())
			.children(subcommands)
			.build();

		Sponge.getCommandManager().register(this, spongyChestCommandSpec, "sc", "spongychest");

		Sponge.getEventManager().registerListeners(this, new InteractBlockListener());
		Sponge.getEventManager().registerListeners(this, new HitBlockListener());

		// Chest
		Sponge.getDataManager().register(IsSpongyChestData.class, ImmutableIsSpongyChestData.class, new IsSpongyChestDataBuilder());
		Sponge.getDataManager().register(SpongeIsSpongyChestData.class, ImmutableSpongeIsSpongyChestData.class, new IsSpongyChestDataBuilder());

		// Item
		Sponge.getDataManager().register(ItemChestData.class, ImmutableItemChestData.class, new ItemChestDataBuilder());
		Sponge.getDataManager().register(SpongeItemChestData.class, ImmutableSpongeItemChestData.class, new ItemChestDataBuilder());

		// UUID
		Sponge.getDataManager().register(UUIDChestData.class, ImmutableUUIDChestData.class, new UUIDChestDataBuilder());
		Sponge.getDataManager().register(SpongeUUIDChestData.class, ImmutableSpongeUUIDChestData.class, new UUIDChestDataBuilder());

		// Price
		Sponge.getDataManager().register(PriceChestData.class, ImmutablePriceChestData.class, new PriceChestDataBuilder());
		Sponge.getDataManager().register(SpongePriceChestData.class, ImmutableSpongePriceChestData.class, new PriceChestDataBuilder());

		getLogger().info("-----------------------------");
		getLogger().info("SpongyChest was made by HassanS6000!");
		getLogger().info("Please post all errors on the Sponge Thread or on GitHub!");
		getLogger().info("Have fun, and enjoy! :D");
		getLogger().info("-----------------------------");
		getLogger().info("SpongyChest loaded!");
	}

	@Listener
	public void onPostInit(GamePostInitializationEvent event)
	{
		Optional<EconomyService> optionalEconomyService = Sponge.getServiceManager().provide(EconomyService.class);

		if (optionalEconomyService.isPresent())
		{
			economyService = optionalEconomyService.get();
		}
		else
		{
			getLogger().error("No economy plugin was found! This plugin will not work correctly!");
		}
	}
}
