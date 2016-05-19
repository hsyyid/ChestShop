package io.github.hsyyid.spongychest.data.pricechest;

import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public interface ImmutablePriceChestData extends ImmutableListData<Double, ImmutablePriceChestData, PriceChestData>
{
	ImmutableListValue<Double> prices();
	ImmutableValue<Double> sellPrice();
	ImmutableValue<Double> buyPrice();
}
