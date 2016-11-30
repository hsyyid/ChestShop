package io.github.hsyyid.spongychest.data.itemchest;

import com.google.common.collect.ComparisonChain;
import io.github.hsyyid.spongychest.SpongyChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

public class ImmutableSpongeItemChestData extends AbstractImmutableSingleData<ItemStackSnapshot, ImmutableItemChestData, ItemChestData> implements ImmutableItemChestData, Comparable<ImmutableItemChestData>
{
	public ImmutableSpongeItemChestData(ItemStackSnapshot value)
	{
		super(value, SpongyChest.ITEM_CHEST);
	}

	@Override
	protected ImmutableValue<ItemStackSnapshot> getValueGetter()
	{
		return itemStackSnapshot();
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.ITEM_CHEST.getQuery(), this.getValue());
	}
	
	@Override
	public ItemChestData asMutable()
	{
		return new SpongeItemChestData(this.value);
	}

	@Override
	public ImmutableValue<ItemStackSnapshot> itemStackSnapshot()
	{
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.ITEM_CHEST, this.getValue()).asImmutable();
	}

	@Override
	public int compareTo(ImmutableItemChestData o)
	{
		return ComparisonChain.start()
			.compare(itemStackSnapshot().get().getType().getId(), o.itemStackSnapshot().get().getType().getId())
			.compare(itemStackSnapshot().get().getCount(), o.itemStackSnapshot().get().getCount())
			.result();
	}

	@Override
	public <E> Optional<ImmutableItemChestData> with(Key<? extends BaseValue<E>> key, E value)
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
