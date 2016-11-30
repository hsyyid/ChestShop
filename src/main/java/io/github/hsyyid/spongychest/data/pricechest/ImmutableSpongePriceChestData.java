package io.github.hsyyid.spongychest.data.pricechest;

import com.google.common.collect.ComparisonChain;
import io.github.hsyyid.spongychest.SpongyChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;

public class ImmutableSpongePriceChestData extends AbstractImmutableSingleData<Double, ImmutablePriceChestData, PriceChestData> implements ImmutablePriceChestData, Comparable<ImmutablePriceChestData>
{
	public ImmutableSpongePriceChestData(Double value)
	{
		super(value, SpongyChest.PRICE_CHEST);
	}

	@Override
	protected ImmutableValue<Double> getValueGetter()
	{
		return price();
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.PRICE_CHEST.getQuery(), this.getValue());
	}
	
	@Override
	public PriceChestData asMutable()
	{
		return new SpongePriceChestData(this.value);
	}

	@Override
	public ImmutableValue<Double> price()
	{
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.PRICE_CHEST, this.getValue()).asImmutable();
	}

	@Override
	public int compareTo(ImmutablePriceChestData o)
	{
		return ComparisonChain.start()
			.compare(price().get(), o.price().get())
			.result();
	}

	@Override
	public <E> Optional<ImmutablePriceChestData> with(Key<? extends BaseValue<E>> key, E value)
	{
		if (this.supports(key))
		{
			return Optional.of(asMutable().set(key, value).asImmutable());
		}
		else
		{
			return Optional.empty();
		}
	}

	@Override
	public int getContentVersion()
	{
		return 1;
	}
}
