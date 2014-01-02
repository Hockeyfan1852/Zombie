package gmail.theultimatebudgie.Listeners;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

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
