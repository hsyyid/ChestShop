package io.github.hsyyid.spongychest.data.uuidchest;

import com.google.common.collect.ComparisonChain;
import io.github.hsyyid.spongychest.SpongyChest;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;
import java.util.UUID;

public class ImmutableSpongeUUIDChestData extends AbstractImmutableSingleData<UUID, ImmutableUUIDChestData, UUIDChestData> implements ImmutableUUIDChestData, Comparable<ImmutableUUIDChestData>
{
	public ImmutableSpongeUUIDChestData(UUID value)
	{
		super(value, SpongyChest.UUID_CHEST);
	}

	@Override
	protected ImmutableValue<UUID> getValueGetter()
	{
		return uuid();
	}

	@Override
	public DataContainer toContainer()
	{
		return super.toContainer().set(SpongyChest.UUID_CHEST.getQuery(), this.getValue().toString());
	}
	
	@Override
	public UUIDChestData asMutable()
	{
		return new SpongeUUIDChestData(this.value);
	}

	@Override
	public ImmutableValue<UUID> uuid()
	{
		return Sponge.getRegistry().getValueFactory().createValue(SpongyChest.UUID_CHEST, this.getValue()).asImmutable();
	}

	@Override
	public int compareTo(ImmutableUUIDChestData o)
	{
		return ComparisonChain.start()
			.compare(uuid().get(), o.uuid().get())
			.result();
	}

	@Override
	public <E> Optional<ImmutableUUIDChestData> with(Key<? extends BaseValue<E>> key, E value)
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
