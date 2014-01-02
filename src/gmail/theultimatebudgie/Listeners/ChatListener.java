package gmail.theultimatebudgie.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class ChatListener implements Listener {
	ZombieCore plugin;
	
	public ChatListener (ZombieCore plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void playerChatEvent (AsyncPlayerChatEvent event) {
		String name = event.getPlayer().getName();
		//Pretty much a voting listener ;D
		if (plugin.getPhase().equalsIgnoreCase("vote")){
			if (!plugin.playerContainer.get(name).hasVoted()){
				if (event.getMessage().contains("1")) {
					plugin.votes1++;
					plugin.playerContainer.get(name).setVoted(true);
					event.getPlayer().sendMessage("Thanks for voting!");
					event.setCancelled(true);
				} else if (event.getMessage().contains("2")) {
					plugin.votes2++;
					plugin.playerContainer.get(name).setVoted(true);
					event.getPlayer().sendMessage("Thanks for voting!");
					event.setCancelled(true);
				}
			}
		}
		//Displays a nice chat format:
		event.setFormat(event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + ChatColor.WHITE + event.getMessage()); 
	}
}
