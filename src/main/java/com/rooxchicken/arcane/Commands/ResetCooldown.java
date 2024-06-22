package com.rooxchicken.arcane.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rooxchicken.arcane.Arcane;
public class ResetCooldown implements CommandExecutor
{
    private Arcane plugin;

    public ResetCooldown(Arcane _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!sender.isOp())
            return false;

        // plugin.setCooldownForce(Bukkit.getPlayer(sender.getName()), 0, Infinity.ability1CooldownKey);
        // plugin.setCooldownForce(Bukkit.getPlayer(sender.getName()), 0, Infinity.ability2CooldownKey);

        return true;
    }

}
