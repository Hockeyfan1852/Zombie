package gmail.theultimatebudgie.ZombieSurvival;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InfectionDetection {
	ZombieCore plugin;

	public InfectionDetection (ZombieCore plugin) {
		this.plugin = plugin;
	}

	public void MainTask() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run() {
				if (plugin.currentPhase.equalsIgnoreCase("round")){
					Player[] players = Bukkit.getOnlinePlayers();
					for (Player p : players) {
						if (plugin.playerContainer.get(p.getName()).state.equals("infected") ){
							List<Entity> entities = p.getNearbyEntities(0.2, 0.2, 0.2);
							for (Entity e : entities) {
								if (e instanceof Player) {
									Player infected = (Player) e;
									if (plugin.playerContainer.get(infected.getName()).state.equals("human")) {
										plugin.getServer().broadcastMessage(p.getName() + " &c ate " + infected.getName());
										plugin.getServer().broadcastMessage(p.getName() + " &c murdered " + infected.getName());
										ZombieCore.stats.get(p.getName()).kills++;
										plugin.playerContainer.get(infected.getName()).setInfected();
									}
								}
							}
						}
					}
				}
			}
		},0,5);
	}

}
