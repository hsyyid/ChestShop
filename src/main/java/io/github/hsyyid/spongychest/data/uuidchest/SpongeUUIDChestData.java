package io.github.hsyyid.spongychest.data.uuidchest;

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
import java.util.UUID;

public class SpongeUUIDChestData extends AbstractSingleData<UUID, UUIDChestData, ImmutableUUIDChestData> implements UUIDChestData, Comparable<UUIDChestData>
{
	public SpongeUUIDChestData()
	{
		this(UUID.randomUUID());
	}

	public SpongeUUIDChestData(UUID value)
	{
		super(value, SpongyChest.UUID_CHEST);
	}

	@Override
	public Optional<UUIDChestData> fill(DataHolder dataHolder, MergeFunction overlap)
	{
		UUIDChestData typeOfChestData = Preconditions.checkNotNull(overlap).merge(copy(), from(dataHolder.toContainer()).orElse(null));
		return Optional.of(set(SpongyChest.UUID_CHEST, typeOfChestData.get(SpongyChest.UUID_CHEST).get()));
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.UUID_CHEST.getQuery(), this.getValue().toString());
	}

	@Override
	public Optional<UUIDChestData> from(DataContainer container)
	{
		UUID value = (UUID) container.get(DataQuery.of("UUIDChest")).orElse(null);

		if (value != null)
			return Optional.of(new SpongeUUIDChestData(value));
		else
			return Optional.empty();
	}

	@Override
	public UUIDChestData copy()
	{
		return new SpongeUUIDChestData(getValue());
	}

	@Override
	public int getContentVersion()
	{
		return 1;
	}

	@Override
	public ImmutableUUIDChestData asImmutable()
	{
		return new ImmutableSpongeUUIDChestData(getValue());
	}

	@Override
	public Value<UUID> uuid()
	{
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.UUID_CHEST, this.getValue());
	}

	@Override
	protected Value<?> getValueGetter()
	{
		return uuid();
	}

	@Override
	public int compareTo(UUIDChestData o)
	{
		return ComparisonChain.start()
			.compare(this.uuid().get(), o.uuid().get())
			.result();
	}
}
