package io.github.hsyyid.spongychest.data.pricechest;

import io.github.hsyyid.spongychest.SpongyChest;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PriceChestDataBuilder implements DataManipulatorBuilder<PriceChestData, ImmutablePriceChestData>
{
	@Override
	public PriceChestData create()
	{
		return new SpongePriceChestData();
	}

	@Override
	public Optional<PriceChestData> createFrom(DataHolder dataHolder)
	{
		return create().fill(dataHolder);
	}

	@Override
	public Optional<PriceChestData> build(DataView container)
	{
		if (!container.contains(SpongyChest.PRICES_CHEST.getQuery()))
		{
			if (container.contains(SpongyChest.SELL_PRICE_CHEST.getQuery()))
			{
				List<Double> values = new ArrayList<>(2);
				values.add(SpongyChest.SELL_PRICE_INDEX, (Double)container.get(SpongyChest.SELL_PRICE_CHEST.getQuery()).get());
				values.add(SpongyChest.BUY_PRICE_INDEX, 0D);
				PriceChestData data = new SpongePriceChestData(values);
				container.remove(SpongyChest.SELL_PRICE_CHEST.getQuery());
				container.set(SpongyChest.PRICES_CHEST.getQuery(), data);
				return Optional.of(data);
			}
			return Optional.empty();
		}

		PriceChestData data = new SpongePriceChestData((List<Double>) container.get(SpongyChest.PRICES_CHEST.getQuery()).get());
		return Optional.of(data);
	}
}
