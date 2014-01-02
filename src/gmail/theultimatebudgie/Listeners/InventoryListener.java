package gmail.theultimatebudgie.Listeners;

import gmail.theultimatebudgie.ZombieSurvival.Utilities;
import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
	ZombieCore plugin;
	
	public InventoryListener (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	//Cancels taking off armour (and being invisible :O )
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if(event.getSlotType().equals(InventoryType.SlotType.ARMOR)){
			event.setCancelled(true);
		}
	}
	
}
