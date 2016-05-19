package io.github.hsyyid.spongychest.data.pricechest;

import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;

public interface PriceChestData extends ListData<Double, PriceChestData, ImmutablePriceChestData>
{
	ListValue<Double> prices();
	Value<Double> sellPrice();
	Value<Double> buyPrice();
}
