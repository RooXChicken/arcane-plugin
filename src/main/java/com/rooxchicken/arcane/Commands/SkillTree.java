package com.rooxchicken.arcane.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.arcane.Arcane;
import com.rooxchicken.arcane.Library;
public class SkillTree implements CommandExecutor
{
    private Arcane plugin;

    public SkillTree(Arcane _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Library.sendPlayerData(Bukkit.getPlayer(sender.getName()), "3");

        return true;
    }

}
