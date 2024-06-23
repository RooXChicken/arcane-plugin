package com.rooxchicken.arcane.Abilities;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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
import com.rooxchicken.arcane.Tasks.CrystalCircleTask;
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

    private HashMap<Player, CrystalCircleTask> crystalParticlesMap;

    public CrystalAbilities(Arcane _plugin) { plugin = _plugin; crystalParticlesMap = new HashMap<Player, CrystalCircleTask>(); }

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
                event.setDamage(event.getDamage() * 0.7 * (checkScepter(item, player.getInventory().getItemInMainHand()) ? 0.8 : 1));
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
        ItemStack playerMainhandItem = player.getInventory().getItemInMainHand();

        ItemStack damagerOffhandItem = damager.getInventory().getItemInOffHand();
        ItemStack damagerMainhandItem = damager.getInventory().getItemInMainHand();

        if(checkName(playerOffhandItem, glacialCrystal) && Math.random() < 0.1 * (checkScepter(playerOffhandItem, playerMainhandItem) ? 2 : 1))
        {
            damager.setFreezeTicks(240);
            damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
        }

        if(checkName(damagerOffhandItem, infernalCrystal) && Math.random() < 0.1 * (checkScepter(damagerOffhandItem, damagerMainhandItem) ? 2 : 1))
        {
            player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(), 30);
            event.setDamage(event.getDamage() + 4);
        }

        if(checkName(damagerOffhandItem, lightningCrystal) && Math.random() < 0.1 * (checkScepter(damagerOffhandItem, damagerMainhandItem) ? 2 : 1))
        {
            player.getWorld().strikeLightningEffect(player.getLocation());
            event.setDamage(event.getDamage() + 6);
        }

        if(checkName(damagerOffhandItem, vampiricCrystal) && Math.random() < 0.1 * (checkScepter(damagerOffhandItem, damagerMainhandItem) ? 2 : 1))
        {
            player.setHealth(player.getHealth() - 2);
            damager.setHealth(damager.getHealth() + 2);
        }

        if(checkName(damagerOffhandItem, shiningCrystal) && Math.random() < 0.1 * (checkScepter(damagerOffhandItem, damagerMainhandItem) ? 2 : 1))
            damager.setHealth(damager.getHealth() + event.getFinalDamage());

        if(checkName(damagerOffhandItem, chainCrystal) && Math.random() < 0.1 * (checkScepter(damagerOffhandItem, damagerMainhandItem) ? 2 : 1))
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
            ItemStack scepter = player.getInventory().getItemInMainHand();

            if(checkName(item, oceanicCrystal))
            {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 21, (checkScepter(item, scepter) ? 1 : 0)));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 21, (checkScepter(item, scepter) ? 1 : 0)));
            }

            if(checkName(item, windCrystal))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, (checkScepter(item, scepter) ? 1 : 0)));

            if(checkName(item, venomousCrystal))
            {
                if(player.hasPotionEffect(PotionEffectType.POISON))
                {
                    PotionEffect poison = player.getPotionEffect(PotionEffectType.POISON);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int)(poison.getDuration() * (checkScepter(item, scepter) ? 1.1: 1)), poison.getAmplifier() + (checkScepter(item, scepter) ? 1 : 0)));
                    player.removePotionEffect(PotionEffectType.POISON);
                }
            }

            if(hasCrystal(item))
            {
                String[] split = item.getItemMeta().getDisplayName().split("§");
                String name = split[split.length-1].substring(1);

                boolean upgraded = checkScepter(item, scepter);
                Library.sendPlayerData(player, "4_" + name + "_" + upgraded);
                if(!crystalParticlesMap.containsKey(player))
                {
                    CrystalCircleTask task = new CrystalCircleTask(plugin, player);
                    Arcane.tasks.add(task);
                    crystalParticlesMap.put(player, task);
                }

                crystalParticlesMap.get(player).upgraded = upgraded;
                crystalParticlesMap.get(player).color = getColor(item.getItemMeta().getDisplayName());
                //player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 0.2, 0), 6, 0.3, 0.2, 0.3, new Particle.DustOptions(getColor(item.getItemMeta().getDisplayName()), (upgraded) ? 1 : 0.5f));
            }
            else
            {
                if(crystalParticlesMap.containsKey(player))
                {
                    crystalParticlesMap.get(player).cancel = true;
                    crystalParticlesMap.remove(player);
                }
            }
        }
    }

    private Color getColor(String itemName)
    {
        if(itemName.equals(glacialCrystal))
            return Color.BLUE;
        if(itemName.equals(infernalCrystal))
            return Color.fromRGB(0xAA0000);
        if(itemName.equals(lightningCrystal))
            return Color.fromRGB(0x8CB6FF);
        if(itemName.equals(venomousCrystal))
            return Color.fromRGB(0x4DB560);
        if(itemName.equals(vampiricCrystal))
            return Color.fromRGB(0x660000);
        if(itemName.equals(shiningCrystal))
            return Color.fromRGB(0xFFD700);
        if(itemName.equals(explosionCrystal))
            return Color.fromRGB(0xD16341);
        if(itemName.equals(oceanicCrystal))
            return Color.fromRGB(0xA4DDED);
        if(itemName.equals(windCrystal))
            return Color.fromRGB(0xD0E8C3);
        if(itemName.equals(chainCrystal))
            return Color.fromRGB(0xD16341);
        if(itemName.equals(shadowCrystal))
            return Color.fromRGB(0x3142A5);

        return Color.WHITE;
    }

    private boolean checkName(ItemStack item, String name)
    {
        return (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(name));
    }

    private boolean checkScepter(ItemStack crystal, ItemStack scepter)
    {
        if(crystal != null && scepter != null && crystal.hasItemMeta() && scepter.hasItemMeta())
        {
            String crystalName = crystal.getItemMeta().getDisplayName();
            String scepterName = scepter.getItemMeta().getDisplayName();

            return scepterName.contains(crystalName.split(" ")[0]);
        }

        return false;
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
