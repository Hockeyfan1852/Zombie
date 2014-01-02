package gmail.theultimatebudgie.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class WorldListener implements Listener {
	ZombieCore plugin;
	
	public WorldListener (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMapLoadEvent (WorldLoadEvent event){
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player p : players) {
			
		}
		
		Bukkit.getServer().broadcastMessage(plugin.prefixWorld + event.getWorld().getName() + " has been loaded!");
	}
	
}
