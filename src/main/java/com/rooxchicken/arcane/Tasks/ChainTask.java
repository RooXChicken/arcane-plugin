package com.rooxchicken.arcane.Tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.rooxchicken.arcane.Arcane;

public class ChainTask extends Task
{
    private Player player;
    private Location freeze;

    private int t = 0;

    public ChainTask(Arcane _plugin, Player _player)
    {
        super(_plugin);
        tickThreshold = 1;

        player = _player;
        freeze = player.getLocation().clone();
    }

    @Override
    public void run()
    {
        player.teleport(freeze);
        
        if(++t > 20)
            cancel = true;
    }
}
