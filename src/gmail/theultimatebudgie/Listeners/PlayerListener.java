package gmail.theultimatebudgie.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class PlayerListener implements Listener {
	ZombieCore plugin;
	public final static int delay = 10;
	public List<String> cooldown = new ArrayList<String>();
	public PlayerListener (ZombieCore plugin) {
		this.plugin = plugin;
	}

	//Negates any food level changing :D
	@EventHandler
	public void onFoodLevelChange (FoodLevelChangeEvent event) {
		//plugin.getLogger().info("FLCE Debug");
		Player p = (Player) event.getEntity();
		p.setFoodLevel(6);
		p.setSaturation(20);
		event.setCancelled(true);
	}

	//Will possible handle sprinting in the future
	@EventHandler
	public void onSprintEvent (PlayerToggleSprintEvent event){
		final Player player = event.getPlayer();
		/*if(event.isSprinting()){
			player.setFoodLevel(1);
			event.setCancelled(true);
			plugin.playerContainer.get(player.getName()).warnSprint();
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable(){
			public void run(){
				player.setFoodLevel(20);
			}
		}, 0L);*/
		player.setFoodLevel(6);
	}

	//Negates all regular damage :)
	@EventHandler
	public void onPlayerDamageEvent (EntityDamageEvent event){
		if (event.getEntity() instanceof Player) {
			//plugin.getLogger().info("EDE Debug");
			Player p = (Player) event.getEntity();
			p.setHealth(20D);
			event.setDamage(0D);
			if (!event.getCause().equals(DamageCause.WITHER)){
				event.setCancelled(true);
			}
		}
	}

	//Negates knock-back from being hit:
	@EventHandler
	public void onEntityDamageByEntity (EntityDamageByEntityEvent event){
		if (event.getEntity() instanceof Player){
			event.setDamage(0D);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		if(event.getAction()==Action.LEFT_CLICK_BLOCK){
			if(cooldown.contains(event.getPlayer().getName())){
				return;
			}else{
				cooldown.add(event.getPlayer().getName());
				final String name = event.getPlayer().getName();
				BlockBreakEvent blockEvent = new BlockBreakEvent(event.getClickedBlock(),event.getPlayer());
				plugin.getServer().getPluginManager().callEvent(blockEvent);
				if(!blockEvent.isCancelled()){
					blockEvent.getBlock().setType(Material.AIR);
				}
				plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable(){
					@Override
					public void run() {
						cooldown.remove(name);
					}
				}, delay);
			}
		}
	}
}
