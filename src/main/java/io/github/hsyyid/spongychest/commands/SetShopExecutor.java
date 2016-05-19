package io.github.hsyyid.spongychest.commands;

import io.github.hsyyid.spongychest.SpongyChest;
import io.github.hsyyid.spongychest.utils.ChestShopModifier;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class SetShopExecutor implements CommandExecutor
{
	@Override
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		if (!ctx.hasAny("sell-price") && !ctx.hasAny("buy-price"))
		{
			CommandResult.empty();
		}
		BigDecimal sellPrice = new BigDecimal(ctx.<Double> getOne("sell-price").orElse(0D));
		BigDecimal buyPrice = new BigDecimal(ctx.<Double> getOne("buy-price").orElse(0D));

		if (src instanceof Player)
		{
			Player player = (Player) src;

			if (player.getItemInHand().isPresent())
			{
				Optional<ChestShopModifier> modifier = SpongyChest.chestShopModifiers.stream().filter(m -> m.getUuid().equals(player.getUniqueId())).findAny();

				ChestShopModifier chestShopModifier = new ChestShopModifier(player.getUniqueId(), player.getItemInHand().get().createSnapshot(), sellPrice, buyPrice);

				if (modifier.isPresent())
				{
					SpongyChest.chestShopModifiers.remove(modifier.get());
				}

				SpongyChest.chestShopModifiers.add(chestShopModifier);
				player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Right click a chest!"));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You must be an in-game player to execute this command."));
		}

		return CommandResult.success();
	}
}
