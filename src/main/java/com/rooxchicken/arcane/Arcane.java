package com.rooxchicken.arcane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.filefilter.CanExecuteFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Predicate;
import com.rooxchicken.arcane.Abilities.CrystalAbilities;
import com.rooxchicken.arcane.Commands.PrintHeldItemCommand;
import com.rooxchicken.arcane.Commands.VerifyMod;
import com.rooxchicken.arcane.Data.ScepterData;
import com.rooxchicken.arcane.Data.SkillData;
import com.rooxchicken.arcane.Tasks.PullDataFromDatapack;
import com.rooxchicken.arcane.Tasks.Task;

public class Arcane extends JavaPlugin implements Listener
{
    public static ArrayList<Task> tasks;
    public ArrayList<Player> hasMod;

    private List<String> blockedCommands = new ArrayList<>();

    private ProtocolManager protocolManager;
    private CrystalAbilities crystalAbilities;

    @Override
    public void onEnable()
    {
        //Library.init();

        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(this);

        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.SET_ACTION_BAR_TEXT)
        {
            @Override
            public void onPacketSending(PacketEvent event)
            {
                List<WrappedChatComponent> components = event.getPacket().getChatComponents().getValues();
                for (WrappedChatComponent component : components)
                {
                    if(component.toString().contains("Mana"))
                    {
                        int offset = 0;
                        ItemStack scepter = event.getPlayer().getInventory().getItemInMainHand();
                        if(scepter != null && scepter.hasItemMeta() && scepter.getItemMeta().getDisplayName().contains("Shadow"))
                        {
                            offset = 8;
                        }
                        //try{
                        //Bukkit.getLogger().info(component.toString());
                        String[] data = component.toString().split("\"");
                        int mana = Integer.parseInt(data[15].trim());
                        int maxMana = Integer.parseInt(data[27].trim());
                        String manaUse = data[37+offset];
                        if(offset != 0)
                            manaUse = " Cost: " + manaUse;
                        manaUse = manaUse.substring(6).trim();
                        if(offset != 0 && manaUse.equals("3"))
                        {
                            offset += 10;
                            manaUse += "/s";
                        }
                        String name = data[47+offset].split(":")[0].trim();
                        String cooldown = data[47+offset].split(":")[1].trim();
                        String nameColor = data[43+offset];
                        nameColor = nameColor.replace("_", "-");
                        String costColor = data[33+offset];
                        costColor = costColor.replace("_", "-");
                        String cooldownColor = data[43];
                        int bloodIndex = 0;
                        boolean blood = false;
                        String bloodUsage = " ";
                        //}
                        //catch(Exception e)
                        //{
                            //Bukkit.getLogger().info(cooldown);
                        //}
                        
                        
                        if(cooldown.equals(""))
                        {
                            cooldown = data[55+offset].trim();
                            cooldownColor = data[51+offset].trim();
                            cooldown += "s";

                            if(data.length >= 71+offset && data[71+offset].contains("Blood"))
                                bloodIndex = 79;
                        }
                        else if(data.length >= 55+offset && data[55+offset].contains("Blood"))
                            bloodIndex = 63;

                        if(bloodIndex != 0)
                        {
                            blood = true;
                            bloodUsage = data[bloodIndex+8+offset];
                        }
                        if(name.contains("Life Drain"))
                            blood = true;

                        cooldownColor = cooldownColor.replace("_", "-");
                        
                        Library.sendPlayerData(event.getPlayer(), "1_" + manaUse + "_" + name + "_" + cooldown + "_" + nameColor + "_" + costColor + "_" + cooldownColor + "_" + bloodUsage + "_" + blood);
                        event.setCancelled(true);
                    }
                }
            }
        });

        
        tasks = new ArrayList<Task>();
        tasks.add(new PullDataFromDatapack(this));
        hasMod = new ArrayList<Player>();

        crystalAbilities = new CrystalAbilities(this);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(crystalAbilities, this);

        this.getCommand("printname").setExecutor(new PrintHeldItemCommand(this));

        this.getCommand("hdn_verifymod").setExecutor(new VerifyMod(this));
		blockedCommands.add("hdn_verifymod");
        

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            public void run()
            {
                ArrayList<Task> _tasks = new ArrayList<Task>();
                for(Task t : tasks)
                    _tasks.add(t);
                
                ArrayList<Task> toRemove = new ArrayList<Task>();

                for(Task t : _tasks)
                {
                    t.tick();

                    if(t.cancel)
                        toRemove.add(t);
                }

                for(Task t : toRemove)
                {
                    t.onCancel();
                    tasks.remove(t);
                }

                crystalAbilities.tick();
            }
        }, 0, 1);

        for(Player player : getServer().getOnlinePlayers())
            Library.sendPlayerData(player, "0");

        getLogger().info("Arcane SMP (featuring 1987 different plugins!) (made by roo)");
    }

    @EventHandler
    private void checkIfPlayerHasMod(PlayerJoinEvent e)
    {
        Library.sendPlayerData(e.getPlayer(), "0");
    }

    @EventHandler
    private void unRegisterPlayer(PlayerQuitEvent e)
    {
        hasMod.remove(e.getPlayer());
    }

    @EventHandler
	private void removeCommands(PlayerCommandSendEvent e)
    {
		e.getCommands().removeAll(blockedCommands);
	}
    
    @EventHandler
    private void preventKick(PlayerKickEvent event)
    {
        if(event.getReason().equals("Kicked for spamming"))
            event.setCancelled(true);
    }

    public void verifyMod(Player player)
    {
        if(!hasMod.contains(player))
            hasMod.add(player);
    }
}
