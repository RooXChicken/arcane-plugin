package com.rooxchicken.arcane.Tasks;

import java.util.HashMap;

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
    public static HashMap<Player, String> playerScepterMap;

    public PreventMultipleScepters(Arcane _plugin)
    {
        super(_plugin);
        tickThreshold = 40;

        Bukkit.getServer().getPluginManager().registerEvents(this, _plugin);

        playerScepterMap = new HashMap<Player, String>();
    }

    @Override
    public void run()
    {
        playerScepterMap.clear();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            scepterCount(player);
        }
    }

    private int scepterCount(Player player)
    {
        Inventory inv = player.getInventory();
        int count = 0;
        for(ItemStack item : inv)
        {
            if(item != null && item.hasItemMeta() && item.getType() == Material.CARROT_ON_A_STICK)
            {
                if(count == 0)
                    playerScepterMap.put(player, item.getItemMeta().getDisplayName());
                else
                {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    player.getInventory().remove(item);
                }
                count++;
            }
        }

        return count;
    }

    @EventHandler
    private void preventPickup(EntityPickupItemEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            ItemStack item = event.getItem().getItemStack();
            if(item != null && item.hasItemMeta() && item.getType() == Material.CARROT_ON_A_STICK)
            {
                event.setCancelled(scepterCount((Player)event.getEntity()) > 0);
            }
        }
    }
}
