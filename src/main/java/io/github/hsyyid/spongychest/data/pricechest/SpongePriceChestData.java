package io.github.hsyyid.spongychest.data.pricechest;

import com.google.common.base.Preconditions;
import io.github.hsyyid.spongychest.SpongyChest;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractListData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpongePriceChestData extends AbstractListData<Double, PriceChestData, ImmutablePriceChestData> implements PriceChestData
{
	public SpongePriceChestData()
	{
		this(new ArrayList<>(2));
	}

	public SpongePriceChestData(List<Double> value)
	{
		super(value, SpongyChest.PRICES_CHEST);
	}

	@Override
	public Optional<PriceChestData> fill(DataHolder dataHolder, MergeFunction overlap)
	{
		PriceChestData typeOfChestData = Preconditions.checkNotNull(overlap).merge(copy(), from(dataHolder.toContainer()).orElse(null));
		return Optional.of(set(SpongyChest.PRICES_CHEST, typeOfChestData.get(SpongyChest.PRICES_CHEST).get()));
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.PRICES_CHEST.getQuery(), this.getValue());
	}

	@Override
	public Optional<PriceChestData> from(DataContainer container)
	{
		List<Double> value = (List<Double>) container.get(SpongyChest.PRICES_CHEST.getQuery()).orElse(null);

		if (value != null)
			return Optional.of(new SpongePriceChestData(value));
		else
		{
			if (container.contains(SpongyChest.SELL_PRICE_CHEST.getQuery()))
			{
				value = new ArrayList<>(2);
				value.add(SpongyChest.SELL_PRICE_INDEX, (Double)container.get(SpongyChest.SELL_PRICE_CHEST.getQuery()).get());
				value.add(SpongyChest.BUY_PRICE_INDEX, 0D);
				PriceChestData data = new SpongePriceChestData(value);
				container.remove(SpongyChest.SELL_PRICE_CHEST.getQuery());
				container.set(SpongyChest.PRICES_CHEST.getQuery(), data);
				return Optional.of(data);
			}
			return Optional.empty();
		}
	}

	@Override
	public PriceChestData copy()
	{
		return new SpongePriceChestData(getValue());
	}

	@Override
	public int getContentVersion()
	{
		return 1;
	}

	@Override
	public ImmutablePriceChestData asImmutable()
	{
		return new ImmutableSpongePriceChestData(getValue());
	}

	@Override
	public ListValue<Double> prices()
	{
		return Sponge.getRegistry().getValueFactory().createListValue(SpongyChest.PRICES_CHEST, this.getValue());
	}

	@Override
	protected ListValue<Double> getValueGetter()
	{
		return prices();
	}

	@Override
	public Value<Double> sellPrice() {
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.SELL_PRICE_CHEST, this.getValue().get(SpongyChest.SELL_PRICE_INDEX));
	}

	@Override
	public Value<Double> buyPrice() {
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.BUY_PRICE_CHEST, this.getValue().get(SpongyChest.BUY_PRICE_INDEX));
	}
}
