package io.github.hsyyid.spongychest.data.pricechest;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import io.github.hsyyid.spongychest.SpongyChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class SpongePriceChestData extends AbstractSingleData<Double, PriceChestData, ImmutablePriceChestData> implements PriceChestData, Comparable<PriceChestData>
{
	public SpongePriceChestData()
	{
		this(0d);
	}

	public SpongePriceChestData(Double value)
	{
		super(value, SpongyChest.PRICE_CHEST);
	}

	@Override
	public Optional<PriceChestData> fill(DataHolder dataHolder, MergeFunction overlap)
	{
		PriceChestData typeOfChestData = Preconditions.checkNotNull(overlap).merge(copy(), from(dataHolder.toContainer()).orElse(null));
		return Optional.of(set(SpongyChest.PRICE_CHEST, typeOfChestData.get(SpongyChest.PRICE_CHEST).get()));
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.PRICE_CHEST.getQuery(), this.getValue());
	}

	@Override
	public Optional<PriceChestData> from(DataContainer container)
	{
		Double value = (Double) container.get(DataQuery.of("PriceChest")).orElse(null);

		if (value != null)
			return Optional.of(new SpongePriceChestData(value));
		else
			return Optional.empty();
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
	public Value<Double> price()
	{
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.PRICE_CHEST, this.getValue());
	}

	@Override
	protected Value<?> getValueGetter()
	{
		return price();
	}

	@Override
	public int compareTo(PriceChestData o)
	{
		return ComparisonChain.start()
			.compare(this.price().get(), o.price().get())
			.result();
	}
}
