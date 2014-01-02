package gmail.theultimatebudgie.Listeners;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListener implements Listener {
	ZombieCore plugin;
	
	public MovementListener (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	//Checks to see if someone is walking into the invisible borders. If they do... bedrock in the face!
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove (final PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		if (event.getTo().getBlock().getType().equals(Material.PISTON_MOVING_PIECE)){
			p.sendBlockChange(event.getTo().add(0,1,0), Material.BEDROCK, (byte) 0);
			//p.sendMessage("Block 36 test");
			p.teleport(event.getFrom());
			event.setCancelled(true);
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					p.sendBlockChange(event.getTo(), Material.PISTON_MOVING_PIECE, (byte) 0);
				}
				
			}, 20);
		}
	}
}
