package io.github.hsyyid.spongychest;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.hsyyid.spongychest.commands.SetShopExecutor;
import io.github.hsyyid.spongychest.commands.SpongyChestExecutor;
import io.github.hsyyid.spongychest.data.isspongychest.*;
import io.github.hsyyid.spongychest.data.itemchest.*;
import io.github.hsyyid.spongychest.data.pricechest.*;
import io.github.hsyyid.spongychest.data.uuidchest.*;
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
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.*;

@Plugin(
		id = "spongychest",
		name = "SpongyChest",
		version = "0.4.7",
		description = "SpongyChest is a plugin that utilizes Chests to make in-game item shops for players to sell items to each other!"
)
public class SpongyChest
{
	public static EconomyService economyService;
	public static Set<ChestShopModifier> chestShopModifiers = Sets.newHashSet();

	// Keys
	public static final Key<Value<Boolean>> IS_SPONGY_CHEST = KeyFactory.makeSingleKey(TypeToken.of(Boolean.class), new TypeToken<Value<Boolean>>() {}, DataQuery.of("IsSpongyChest"), "spongychest:is_spongy_chest", "Is SpongyChest");
	public static final Key<Value<UUID>> UUID_CHEST = KeyFactory.makeSingleKey(TypeToken.of(UUID.class), new TypeToken<Value<UUID>>() {}, DataQuery.of("UUIDChest"), "spongychest:uuid_chest", "The UUID of the owner");
	public static final Key<Value<ItemStackSnapshot>> ITEM_CHEST = KeyFactory.makeSingleKey(TypeToken.of(ItemStackSnapshot.class), new TypeToken<Value<ItemStackSnapshot>>() {}, DataQuery.of("ItemChest"), "spongychest:item_chest", "The actual item this SpongyChest sells/buys");
	public static final Key<Value<Double>> PRICE_CHEST = KeyFactory.makeSingleKey(TypeToken.of(Double.class), new TypeToken<Value<Double>>() {}, DataQuery.of("PriceChest"), "spongychest:price_chest", "The price this SpongyChest sells/buys for");

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
			.arguments(GenericArguments.doubleNum(Text.of("price")))
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
