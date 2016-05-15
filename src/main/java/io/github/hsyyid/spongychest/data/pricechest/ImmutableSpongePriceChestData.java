package io.github.hsyyid.spongychest.data.pricechest;

import io.github.hsyyid.spongychest.SpongyChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableListData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.List;
import java.util.Optional;

public class ImmutableSpongePriceChestData extends AbstractImmutableListData<Double, ImmutablePriceChestData, PriceChestData> implements ImmutablePriceChestData
{
	public ImmutableSpongePriceChestData(List<Double> value)
	{
		super(value, SpongyChest.PRICES_CHEST);
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.PRICES_CHEST.getQuery(), this.getValue());
	}
	
	@Override
	public PriceChestData asMutable()
	{
		return new SpongePriceChestData(this.value);
	}

	@Override
	public ImmutableListValue<Double> prices()
	{
		return Sponge.getRegistry().getValueFactory().createListValue(SpongyChest.PRICES_CHEST, this.getValue()).asImmutable();
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

	@Override
	public ImmutableValue<Double> sellPrice() {
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.SELL_PRICE_CHEST, this.getValue().get(SpongyChest.SELL_PRICE_INDEX)).asImmutable();
	}

	@Override
	public ImmutableValue<Double> buyPrice() {
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.BUY_PRICE_CHEST, this.getValue().get(SpongyChest.BUY_PRICE_INDEX)).asImmutable();
	}
}
