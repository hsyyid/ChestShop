package io.github.hsyyid.spongychest.data.itemchest;

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
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

public class SpongeItemChestData extends AbstractSingleData<ItemStackSnapshot, ItemChestData, ImmutableItemChestData> implements ItemChestData, Comparable<ItemChestData>
{
	public SpongeItemChestData()
	{
		this(ItemStack.builder().itemType(ItemTypes.NONE).build().createSnapshot());
	}

	public SpongeItemChestData(ItemStackSnapshot stack)
	{
		super(stack, SpongyChest.ITEM_CHEST);
	}

	@Override
	public Optional<ItemChestData> fill(DataHolder dataHolder, MergeFunction overlap)
	{
		ItemChestData typeOfChestData = Preconditions.checkNotNull(overlap).merge(copy(), from(dataHolder.toContainer()).orElse(null));
		return Optional.of(set(SpongyChest.ITEM_CHEST, typeOfChestData.get(SpongyChest.ITEM_CHEST).get()));
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.ITEM_CHEST.getQuery(), this.getValue());
	}

	@Override
	public Optional<ItemChestData> from(DataContainer container)
	{
		ItemStackSnapshot value = (ItemStackSnapshot) container.get(DataQuery.of("ItemChest")).orElse(null);

		if (value != null)
			return Optional.of(new SpongeItemChestData(value));
		else
			return Optional.empty();
	}

	@Override
	public ItemChestData copy()
	{
		return new SpongeItemChestData(getValue());
	}

	@Override
	public int getContentVersion()
	{
		return 1;
	}

	@Override
	public ImmutableItemChestData asImmutable()
	{
		return new ImmutableSpongeItemChestData(getValue());
	}

	@Override
	public Value<ItemStackSnapshot> itemStackSnapshot()
	{
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.ITEM_CHEST, this.getValue());
	}

	@Override
	protected Value<?> getValueGetter()
	{
		return itemStackSnapshot();
	}

	@Override
	public int compareTo(ItemChestData o)
	{
		return ComparisonChain.start()
			.compare(this.itemStackSnapshot().get().getType().getId(), o.itemStackSnapshot().get().getType().getId())
			.compare(this.itemStackSnapshot().get().getCount(), o.itemStackSnapshot().get().getCount())
			.result();
	}
}
