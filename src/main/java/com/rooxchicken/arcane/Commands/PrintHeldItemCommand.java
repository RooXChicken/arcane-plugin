package com.rooxchicken.arcane.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.rooxchicken.arcane.Arcane;
public class PrintHeldItemCommand implements CommandExecutor
{
    private Arcane plugin;

    public PrintHeldItemCommand(Arcane _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!sender.isOp())
            return false;

        ItemStack item = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
        Bukkit.getLogger().info(item.getItemMeta().getDisplayName());

        // plugin.setCooldownForce(Bukkit.getPlayer(sender.getName()), 0, Infinity.ability1CooldownKey);
        // plugin.setCooldownForce(Bukkit.getPlayer(sender.getName()), 0, Infinity.ability2CooldownKey);

        return true;
    }

}
