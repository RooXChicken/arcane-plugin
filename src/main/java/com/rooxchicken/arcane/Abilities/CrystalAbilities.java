package com.rooxchicken.arcane.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.arcane.Arcane;
import com.rooxchicken.arcane.Library;
import com.rooxchicken.arcane.Tasks.ChainTask;
import com.rooxchicken.arcane.Tasks.ShadowTask;

public class CrystalAbilities implements Listener
{
    private Arcane plugin;

    private String glacialCrystal = "§9§lGlacial Crystal";
    private String infernalCrystal = "§4§lInfernal Crystal";
    private String lightningCrystal = "§x§8§C§B§6§F§F§lLightning Crystal";
    private String venomousCrystal = "§x§4§D§B§5§6§0§lVenomous Crystal";
    private String vampiricCrystal = "§x§6§6§0§0§0§0§lVampiric Crystal";
    private String shiningCrystal = "§x§F§F§D§7§0§0§lShining Crystal";
    private String explosionCrystal = "§x§D§1§6§3§4§1§lExplosion Crystal";
    private String oceanicCrystal = "§x§A§4§D§D§E§D§lWater Crystal";
    private String windCrystal = "§x§D§0§E§8§C§3§lWind Crystal";
    private String chainCrystal = "§x§D§1§6§3§4§1§lChain Crystal";
    private String shadowCrystal = "§x§3§1§4§2§A§5§lShadow Crystal";

    public CrystalAbilities(Arcane _plugin) { plugin = _plugin; }

    @EventHandler
    public void damageEvents(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player)event.getEntity();
            ItemStack item = player.getInventory().getItemInOffHand();

            if(event.getCause() == DamageCause.FALL)
                event.setCancelled(checkName(item, windCrystal));

            if(checkName(item, explosionCrystal) && (event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION))
            {
                event.setDamage(event.getDamage() * 0.7);
            }
        }
    }

    @EventHandler
    public void playerHurtPlayer(EntityDamageByEntityEvent event)
    {
        if(!(event.getDamager() instanceof Player && event.getEntity() instanceof Player))
            return;

        Player player = (Player)event.getEntity();
        Player damager = (Player)event.getDamager();

        ItemStack playerOffhandItem = player.getInventory().getItemInOffHand();
        ItemStack damagerOffhandItem = damager.getInventory().getItemInOffHand();

        if(checkName(playerOffhandItem, glacialCrystal) && Math.random() < 0.1)
        {
            damager.setFreezeTicks(240);
            damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
        }

        if(checkName(damagerOffhandItem, infernalCrystal) && Math.random() < 0.1)
        {
            player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(), 30);
            event.setDamage(event.getDamage() + 4);
        }

        if(checkName(damagerOffhandItem, lightningCrystal) && Math.random() < 0.1)
        {
            player.getWorld().strikeLightningEffect(player.getLocation());
            event.setDamage(event.getDamage() + 6);
        }

        if(checkName(damagerOffhandItem, vampiricCrystal) && Math.random() < 0.1)
        {
            player.setHealth(player.getHealth() - 2);
            damager.setHealth(damager.getHealth() + 2);
        }

        if(checkName(damagerOffhandItem, shiningCrystal) && Math.random() < 0.1)
            damager.setHealth(damager.getHealth() + event.getFinalDamage());

        if(checkName(damagerOffhandItem, chainCrystal) && Math.random() < 0.1)
            Arcane.tasks.add(new ChainTask(plugin, player));

        if(player.getHealth() - event.getFinalDamage() < 4 && checkName(playerOffhandItem, shadowCrystal) && Math.random() < 0.4)
        {
            Arcane.tasks.add(new ShadowTask(plugin, player));
        }
    }

    public void tick()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            ItemStack item = player.getInventory().getItemInOffHand();

            if(checkName(item, oceanicCrystal))
            {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 21, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 21, 0));
            }

            if(checkName(item, windCrystal))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, 0));

            if(checkName(item, venomousCrystal))
            {
                if(player.hasPotionEffect(PotionEffectType.POISON))
                {
                    PotionEffect poison = player.getPotionEffect(PotionEffectType.POISON);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, poison.getDuration(), poison.getAmplifier()));
                    player.removePotionEffect(PotionEffectType.POISON);
                }
            }

            if(hasCrystal(item))
            {
                String[] name = item.getItemMeta().getDisplayName().split("§");
                Library.sendPlayerData(player, "4_" + name[name.length-1].substring(1));
            }
        }
    }

    private boolean checkName(ItemStack item, String name)
    {
        return (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name));
    }

    public boolean hasCrystal(ItemStack item)
    {
        if(item != null && item.hasItemMeta())
        {
            String name = item.getItemMeta().getDisplayName();
            if(name.equals(glacialCrystal) || name.equals(infernalCrystal) || name.equals(lightningCrystal) || name.equals(venomousCrystal) || name.equals(vampiricCrystal) || name.equals(shiningCrystal) || name.equals(explosionCrystal) || name.equals(oceanicCrystal) || name.equals(windCrystal) || name.equals(chainCrystal)|| name.equals(shadowCrystal))
            {
                return true;
            }
        }

        return false;
    }
}
