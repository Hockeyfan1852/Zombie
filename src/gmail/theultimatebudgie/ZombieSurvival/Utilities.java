package gmail.theultimatebudgie.ZombieSurvival;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Utilities {
	
	//Change seconds into Minutes:Seconds!
    public static List<Integer> parseTime(int time) {
    	int minutes = (int) Math.floor(time / 60);
    	int seconds = time - (minutes * 60);
    	
    	List<Integer> results = new ArrayList<Integer>();
    	results.add(minutes);
    	results.add(seconds);
    	
    	return results;
    }
    
    //Convenience method for colouring armour .-.
    public static ItemStack colourLeatherArmour(Material mat, int r, int g, int b) {
    	ItemStack armour = new ItemStack(mat);
    	LeatherArmorMeta meta = (LeatherArmorMeta) armour.getItemMeta();
    	Color colour = Color.fromRGB(r,g,b);
    	meta.setColor(colour);
    	armour.setItemMeta(meta);
    	
    	return armour;
    }
	
    //Checks if an item has that displayname (good for block-picker, etc.)
    public static boolean hasDisplayName(ItemStack item, String name) {
    	if (item != null) {
	    	if (item.hasItemMeta()) {
	    		ItemMeta meta = item.getItemMeta();
	    		if (meta.hasDisplayName()) {
	    			if (meta.getDisplayName().contains(name)) {
	    				return true;
	    			}
	    		}
	    	}
    	}
    	return false;
    }
}
