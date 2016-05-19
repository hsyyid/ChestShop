package io.github.hsyyid.spongychest.listeners;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import io.github.hsyyid.spongychest.data.isspongychest.IsSpongyChestData;

public class ItemFrameListener
{
	@Listener
	@IsCancelled(Tristate.FALSE)
	public void onCollideEntityImpact(CollideEntityEvent event, @First Player player)
	{
		for (Entity entity : event.getOriginalEntities())
		{
			Location<World> location = entity.getLocation().sub(0, 1, 0);
			if (location.getBlockType() == BlockTypes.CHEST && location.getTileEntity().isPresent())
			{
				Chest chest = (Chest) location.getTileEntity().get();

				if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get())
				{
					event.setCancelled(true);
				}
			}
		}
	}
}
