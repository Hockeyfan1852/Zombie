package gmail.theultimatebudgie.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import gmail.theultimatebudgie.ZombieSurvival.Utilities;
import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class BlockListener implements Listener {
	ZombieCore plugin;
	
	public BlockListener (ZombieCore plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace (BlockPlaceEvent event) {
		if (Utilities.hasDisplayName(event.getItemInHand(), "Block Picker")){
			event.setCancelled(true);
			return;
		}
		
		//Increments block counter :)
		if (!plugin.playerContainer.get(event.getPlayer().getName()).getState().equals("ref")){
			plugin.playerContainer.get(event.getPlayer().getName()).incrementBlocksPlaced();
			if (plugin.playerContainer.get(event.getPlayer().getName()).getBlocksPlaced() >= 50) {
				event.setCancelled(true);
				return;
			}
		}
		
		//Makes sure block is automatically put back into inventory!
		int held = event.getPlayer().getInventory().getHeldItemSlot();
		event.getPlayer().getInventory().setItem(held, event.getItemInHand());
		event.getPlayer().updateInventory();
	}
	
}

