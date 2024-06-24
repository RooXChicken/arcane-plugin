package com.rooxchicken.arcane.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.rooxchicken.arcane.Arcane;

public class PreventMultipleScepters extends Task implements Listener
{
    public PreventMultipleScepters(Arcane _plugin)
    {
        super(_plugin);
        tickThreshold = 40;

        Bukkit.getServer().getPluginManager().registerEvents(this, _plugin);
    }

    @Override
    public void run()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            if(scepterCount(player.getInventory()) > 1)
            {
                dropAdditional(player);
            }
        }
    }

    // @EventHandler
    // private void preventMoving(InventoryClickEvent event)
    // {
    //     if(!event.getAction().equals(InventoryAction.PICKUP_ALL) && !event.getAction().equals(InventoryAction.PICKUP_HALF) && !event.getAction().equals(InventoryAction.PICKUP_ONE) && !event.getAction().equals(InventoryAction.PICKUP_SOME))
    //         event.setCancelled(scepterCount(event.getWhoClicked().getInventory()) > 0);
    // }

    @EventHandler
    private void preventPickup(EntityPickupItemEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            ItemStack item = event.getItem().getItemStack();
            if(item != null && item.getType() == Material.CARROT_ON_A_STICK)
            {
                event.setCancelled(scepterCount(((Player)event.getEntity()).getInventory()) > 0);
            }
        }
    }

    private int scepterCount(Inventory inv)
    {
        int count = 0;
        for(ItemStack item : inv)
        {
            if(item != null && item.getType() == Material.CARROT_ON_A_STICK)
            {
                count++;
            }
        }

        return count;
    }

    private void dropAdditional(Player player)
    {
        int count = 0;
        for(ItemStack item : player.getInventory())
        {
            if(item != null && item.getType() == Material.CARROT_ON_A_STICK)
            {
                if(count > 0)
                {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.getInventory().remove(item);
                }
                count++;

            }
        }
    }
}
