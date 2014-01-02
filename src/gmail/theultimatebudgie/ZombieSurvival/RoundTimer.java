package gmail.theultimatebudgie.ZombieSurvival;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RoundTimer {
	ZombieCore plugin;
	
	
	public RoundTimer (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	public void MainTimer() {
		//Main Timer - In charge of co-ordinating rounds!
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run() {
				if (plugin.votingTime == plugin.votingOfficialTime) {
					startVoting();
					plugin.votingTime--;
				}
				
				else if (plugin.votingTime > 0) {
					announceTime(plugin.votingTime, "Voting");

					plugin.votingTime--;
					
				}
				
				else if (plugin.votingTime == 0) {
					plugin.votingTime = -1;
					plugin.switchPhases("break");
					
					String nextMap = tallyVotes();
					plugin.getServer().broadcastMessage(plugin.prefixVoting + nextMap + " won!");
					
					//Reset votes
					plugin.votes1 = 0;
					plugin.votes2 = 0;
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						plugin.playerContainer.get(p.getName()).setVoted(false);
					}
					
					//Load next world
					plugin.loadNextWorld(nextMap);
				}
				
				//Time to switch between maps
				else if (plugin.breakTime > 0) {
					announceTime(plugin.breakTime , "Break");
					
					plugin.breakTime--;
				} 
				
				//Switch to hide timer, unload old map, teleport everybody!
				else if (plugin.breakTime == 0) {
					plugin.switchPhases("hide");
					plugin.breakTime = -1;
					
					Player[] players = Bukkit.getOnlinePlayers();
					for (Player p : players) {
						if (!p.getWorld().equals(plugin.currentWorld)){
							p.teleport(plugin.currentWorld.getSpawnLocation());
							p.sendMessage(plugin.prefixWorld + "Catch you on the flip-side ;)");
							plugin.playerContainer.get(p.getName()).setHuman();
						}
					}
					
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							if (plugin.lastWorld != null) {
								Bukkit.unloadWorld(plugin.lastWorld, false);
								plugin.lastWorld = null;
							}
						}
						
					});
					
					plugin.getServer().broadcastMessage(plugin.prefixRound + plugin.hideTime + " secs till a Zombie is chosen!");
					
				}
				
				//Time to hide, can break blocks
				else if (plugin.hideTime > 0) {
					announceTime(plugin.hideTime , "Hide");
					
					plugin.hideTime--;
				}
				
				//Switch to round timer
				else if (plugin.hideTime == 0) {
					if (plugin.enoughHumans()) {
						plugin.switchPhases("round");
						plugin.hideTime = -1;
						
						int random = plugin.randomGenerator.nextInt(Bukkit.getOnlinePlayers().length);
						Player p = Bukkit.getOnlinePlayers()[random];
						plugin.playerContainer.get(p.getName()).setInfected();
						plugin.getServer().broadcastMessage(plugin.prefixRound + p.getName() + " has been chosen!");
					} else {
						plugin.hideTime = plugin.currentConfig.getInt("HidingTime");
						plugin.getServer().broadcastMessage(plugin.prefixRound + ChatColor.DARK_PURPLE + "Not enough humans online!");
						plugin.getServer().broadcastMessage(plugin.prefixRound + plugin.hideTime + " secs till a Zombie is chosen!");
					}
				}
				
				//Round time, infections will occur :D
				else if (plugin.roundTime > 0) {
					announceTime(plugin.roundTime , "Round");
					
					if (!plugin.enoughHumans()) {
						plugin.endRound();
					}
					
					plugin.roundTime--;
				}
				
				//Humans won and round ended :)
				else if (plugin.roundTime == 0) {
					plugin.endRound();
				}
				
			}
		},0,20);
		
	}
	
	//Announces the time at the apprioriate intervals
	public void announceTime(int time, String phaseName) {
		if (!phaseName.equals("Voting")) {
			List<Integer> times = Utilities.parseTime(time);
			if (time%60 == 0) {
				plugin.getServer().broadcastMessage(plugin.prefixRound + phaseName + " - " + ChatColor.GOLD + times.get(0).toString() + ":0" + times.get(1).toString());
			}
			
			if (times.get(0) == 0 && times.get(1) <= 30){
				if (times.get(1)%10 == 0) {
					plugin.getServer().broadcastMessage(plugin.prefixRound + phaseName + " - " + ChatColor.GOLD + times.get(0).toString() + ":" + times.get(1).toString());
				}
				if (times.get(1) <= 5) {
					plugin.getServer().broadcastMessage(plugin.prefixRound + phaseName + " - " + ChatColor.GOLD + times.get(0).toString() + ":0" + times.get(1).toString());
				}
			}
		} else {
			List<Integer> times = Utilities.parseTime(time);
			if (time%60 == 0) {
				plugin.getServer().broadcastMessage(plugin.prefixVoting + phaseName + " - " + ChatColor.GOLD + times.get(0).toString() + ":0" + times.get(1).toString());
			}
			
			if (times.get(0) == 0 && times.get(1) <= 30){
				if (times.get(1)%10 == 0) {
					plugin.getServer().broadcastMessage(plugin.prefixVoting + phaseName + " - " + ChatColor.GOLD + times.get(0).toString() + ":" + times.get(1).toString());
				}
				if (times.get(1) <= 5) {
					plugin.getServer().broadcastMessage(plugin.prefixVoting + phaseName + " - " + ChatColor.GOLD + times.get(0).toString() + ":0" + times.get(1).toString());
				}
			}
		}
		
	}
	
	public void startVoting() {
		plugin.getServer().broadcastMessage(plugin.prefixVoting + "Voting has started!");
		
		//Makes votingworldslist = worldslist... had to do it this way so worldsList wouldn't be affected
		plugin.votingWorldsList.clear();
		for (String world : plugin.worldsList){
			plugin.votingWorldsList.add(world);
		}
		
		//Removes the currentworld from the list... can't get same world twice
		if (plugin.currentWorld != null){
			plugin.votingWorldsList.remove(plugin.currentWorld.getName());
		}
		
		//Choose first World
		int index = plugin.randomGenerator.nextInt(plugin.votingWorldsList.size());
		plugin.map1 = plugin.votingWorldsList.get(index);
		plugin.votingWorldsList.remove(index);
		
		//Choose second world
		index = plugin.randomGenerator.nextInt(plugin.votingWorldsList.size());
		plugin.map2 = plugin.votingWorldsList.get(index);
		
		//Start vote!
		plugin.getServer().broadcastMessage(ChatColor.YELLOW + " 1 - " + ChatColor.WHITE + plugin.map1 + "\n"
									+ ChatColor.YELLOW + "2 - " + ChatColor.WHITE + plugin.map2 + "\n"
									+ plugin.prefixVoting + "Type in the number to vote!"
				);
	}
	
	//Tallies votes :)
	public String tallyVotes() {
		if (plugin.votes1 > plugin.votes2) {
			return plugin.map1;
		} else if (plugin.votes2 > plugin.votes1){
			return plugin.map2;
		} else {
			int map3 = plugin.randomGenerator.nextInt(1);
			if (map3 == 1) {
				return plugin.map1;
			} else {
				return plugin.map2;
			}
		}
	}
	
}
