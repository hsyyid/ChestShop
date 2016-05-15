package io.github.hsyyid.spongychest.utils;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.math.BigDecimal;
import java.util.UUID;

public class ChestShopModifier
{
	private UUID uuid;
	private ItemStackSnapshot item;
	private BigDecimal sellPrice;
    private BigDecimal buyPrice;

	public ChestShopModifier(UUID uuid, ItemStackSnapshot item, BigDecimal sellPrice, BigDecimal buyPrice)
	{
		this.uuid = uuid;
		this.item = item;
		this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public ItemStackSnapshot getItem()
	{
		return item;
	}
	
	public BigDecimal getSellPrice()
	{
		return sellPrice;
	}
    
    public BigDecimal getBuyPrice()
    {
        return buyPrice;
    }
}
