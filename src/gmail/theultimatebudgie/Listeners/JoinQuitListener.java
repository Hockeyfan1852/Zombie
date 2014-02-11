package gmail.theultimatebudgie.Listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gmail.theultimatebudgie.ZombieSurvival.PlayerContainer;
import gmail.theultimatebudgie.ZombieSurvival.Stats;
import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class JoinQuitListener implements Listener {
	ZombieCore plugin;
	
	public JoinQuitListener (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if(!ZombieCore.stats.containsKey(playerName)){
			ZombieCore.stats.put(playerName, new Stats(playerName));
		}
		/*//I take this back
		if (event.getPlayer().getName().equals("UltimateBudgie")){
			plugin.startTimer();
		}*/
		
		//Add the player's container to the list so I can find it later :)
		plugin.playerContainer.put(playerName, new PlayerContainer(plugin, playerName));
		
		//Checks to see if player should spawn as human or zombie.
		if (plugin.getPhase().equalsIgnoreCase("round")) {
			plugin.playerContainer.get(playerName).setInfected();
		} else {
			plugin.playerContainer.get(playerName).setHuman();
		}
		
		//Makes sure player is teleported to right map
		if (plugin.currentWorld != null) {
			event.getPlayer().teleport(plugin.currentWorld.getSpawnLocation());
		}
		
		//Make sure player has the correct/default minecraft settings
		player.setMaxHealth(20D);
		player.setHealth(20D);
		player.setFoodLevel(6);
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		String playerName = event.getPlayer().getName();
		
		plugin.playerContainer.remove(playerName);
	}	
	
	//TODO: Disable clearing inventory - just make sure all perm items are set!
	public void givePermanentItems(Player player) {
		player.getInventory().clear();
		
		ItemStack blockPicker = new ItemStack(Material.ENDER_CHEST);
		ItemMeta blockPickerMeta = blockPicker.getItemMeta();
		blockPickerMeta.setDisplayName(ChatColor.AQUA + "Block Picker");
		blockPickerMeta.setLore(new ArrayList<String>());
		blockPicker.setItemMeta(blockPickerMeta);
		blockPicker.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 50);
		
		player.getInventory().setItem(0, blockPicker);
	}
}
