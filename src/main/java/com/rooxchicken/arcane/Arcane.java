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
import com.rooxchicken.arcane.Abilities.Ability;
import com.rooxchicken.arcane.Commands.FirstAbility;
import com.rooxchicken.arcane.Commands.ResetCooldown;
import com.rooxchicken.arcane.Commands.SecondAbility;
import com.rooxchicken.arcane.Commands.SkillTree;
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
    public ArrayList<ScepterData> scepterData;

    private ProtocolManager protocolManager;

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
                        String[] data = component.toString().split("\"");
                        int mana = Integer.parseInt(data[15].trim());
                        int maxMana = Integer.parseInt(data[27].trim());
                        String manaUse = data[37].substring(6).trim();
                        String name = data[47].trim();
                        String cooldown = data[47].split(":")[1].trim();
                        if(!cooldown.contains("READY") && !cooldown.contains("ACTIVE") && !cooldown.contains("INACTIVE") && !cooldown.contains("CHARGING"))
                        {
                            cooldown = data[55].trim();
                        }
                        else
                            name = name.split(":")[0];
                        
                        Library.sendPlayerData(event.getPlayer(), "1_" + manaUse + "_" + name + "_" + cooldown);
                        event.setCancelled(true);
                    }
                }
            }
        });

        tasks = new ArrayList<Task>();
        tasks.add(new PullDataFromDatapack(this));
        hasMod = new ArrayList<Player>();

        scepterData = new ArrayList<ScepterData>();
        fillScepterData();

        getServer().getPluginManager().registerEvents(this, this);

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
            }
        }, 0, 1);

        for(Player player : getServer().getOnlinePlayers())
            Library.sendPlayerData(player, "0");

        getLogger().info("Arcane SMP (featuring 1987 different plugins!) (made by roo)");
    }

    private void fillScepterData()
    {
        HashMap<String, SkillData> skills = new HashMap<String, SkillData>();
        scepterData.add(new ScepterData(0, "Glacial", ""));

        skills.clear();
        skills.put("", new SkillData("Ray of Frost", 0, "ASGlacialCD1"));
        scepterData.get(0).skills = skills;
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
