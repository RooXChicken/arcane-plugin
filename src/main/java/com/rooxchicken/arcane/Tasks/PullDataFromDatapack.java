package com.rooxchicken.arcane.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.rooxchicken.arcane.Arcane;
import com.rooxchicken.arcane.Library;

public class PullDataFromDatapack extends Task
{
    Arcane plugin;
    private Scoreboard scoreboard;
    public PullDataFromDatapack(Arcane _plugin)
    {
        super(_plugin);
        plugin = _plugin;
        tickThreshold = 1;

        scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    }

    @Override
    public void run()
    {
        for(Player player : plugin.hasMod)
        {
            Library.sendPlayerData(player, "2_" + scoreboard.getObjective("ASMana").getScore(player.getName()).getScore() + "_" + scoreboard.getObjective("ASMaxMana").getScore(player.getName()).getScore());
            Library.sendPlayerData(player, "3");
        }
    }
}
