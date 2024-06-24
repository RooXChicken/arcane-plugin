package com.rooxchicken.arcane.Tasks;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import com.rooxchicken.arcane.Arcane;

public class CrystalCircleTask extends Task
{
    private Arcane plugin;
    private Player player;

    private int i;
    private double size = 1;

    private double[] cacheX;
    private double[] cacheZ;

    public boolean upgraded = false;
    public Color color;
    
    public CrystalCircleTask(Arcane _plugin, Player _player)
    {
        super(_plugin);

        plugin = _plugin;
        player = _player;

        tickThreshold = 1;

        cacheX = new double[180];
        cacheZ = new double[180];

        for(int i = 0; i < 180; i++)
        {
            double rad = Math.toRadians(i*2);
            cacheX[i] = Math.sin(rad) * size;
            cacheZ[i] = Math.cos(rad) * size;
        }
    }

    @Override
    public void run()
    {
        if(player == null || !player.isOnline())
            cancel = true;
        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(cacheX[i], 0, cacheZ[i]), 1, 0, 0, 0, new Particle.DustOptions(color, upgraded ? 2 : 1));
        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(cacheX[i+90], 0, cacheZ[i+90]), 1, 0, 0, 0, new Particle.DustOptions(color, upgraded ? 2 : 1));
    
        if(++i > 89)
            i = 0;
    }
}
