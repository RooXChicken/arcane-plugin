package com.rooxchicken.arcane.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rooxchicken.arcane.Arcane;
import com.rooxchicken.arcane.Abilities.Ability;
public class FirstAbility implements CommandExecutor
{
    private Arcane plugin;
    private int state = -1;

    public FirstAbility(Arcane _plugin, int _state)
    {
        plugin = _plugin;
        state = _state;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        // Ability ability = plugin.getPlayerAbility(Bukkit.getPlayer(sender.getName()));
        // if(ability != null)
        //     ability.activateFirstAbility(state);

        return true;
    }

}
