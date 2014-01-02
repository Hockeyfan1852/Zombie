package gmail.theultimatebudgie.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class MobListener implements Listener{
	ZombieCore plugin;
	
	public MobListener (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		//plugin.getLogger().info("CSE Debug");
		event.setCancelled(true);
	}
}
