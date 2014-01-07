package gmail.theultimatebudgie.ZombieSurvival;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerContainer {
	ZombieCore plugin;
	String name;

	//States: human, infected, ref
	String state;

	//Whether they've voted during this current voting period
	boolean voted = false;

	//Blooccksss
	int blocksPlaced = 0;

	public PlayerContainer (ZombieCore plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}

	//###############Setting player states##########
	public String getState() {
		return state;
	}

	public void setInfected(){
		final Player player = Bukkit.getPlayer(name);

		//ItemStack leggings = plugin.colourLeatherArmour(Material.LEATHER_LEGGINGS, 60, 179, 113);
		ItemStack boots = Utilities.colourLeatherArmour(Material.LEATHER_BOOTS, 60, 179, 113);
		ItemStack chest = Utilities.colourLeatherArmour(Material.LEATHER_CHESTPLATE, 60, 179, 113);

		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		head.setDurability((short) SkullType.ZOMBIE.ordinal()); 

		ItemStack[] armour = {boots, null, chest, head};

		player.getInventory().setArmorContents(armour);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 0));

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run() {
				player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "You rise again!");
			}
		},100);

		player.setDisplayName(ChatColor.RED + name);

		player.setGameMode(GameMode.SURVIVAL);

		if (this.state == "ref"){
		}
		this.state = "infected";
	}

	public void setHuman(){
		Player player = Bukkit.getPlayer(name);

		player.getInventory().setArmorContents(null);

		player.removePotionEffect(PotionEffectType.INVISIBILITY);

		player.setDisplayName(ChatColor.WHITE + name);

		player.setGameMode(GameMode.SURVIVAL);

		if (this.state == "ref"){
		}

		this.state = "human";
	}

	public void setRef(){
		Player player = Bukkit.getPlayer(name);

		player.getInventory().setArmorContents(null);

		player.removePotionEffect(PotionEffectType.INVISIBILITY);

		player.setDisplayName(ChatColor.GREEN + name);

		player.setGameMode(GameMode.CREATIVE);

		this.state = "ref";
	}
	//############################################


	//#############Voting Controls############
	public boolean hasVoted() {
		return voted;
	}

	public void setVoted(boolean voted) {
		this.voted = voted;
	}
	//##############################

	//#############Block Counter############
	public void incrementBlocksPlaced(){
		blocksPlaced++;
		if (getState().equals("human")){
			if (blocksPlaced == 50){
				Bukkit.getPlayer(name).sendMessage(ChatColor.DARK_GRAY + "@" + name + ChatColor.GRAY + ", you have " + ChatColor.DARK_PURPLE + "no" + ChatColor.GRAY + " blocks left!" );
			} else if (blocksPlaced%10 == 0){
				Bukkit.getPlayer(name).sendMessage(ChatColor.DARK_GRAY + "@" + name + ChatColor.GRAY + ", you have " + ChatColor.DARK_PURPLE + (50 - blocksPlaced) + ChatColor.GRAY + " blocks left!" );
			}
		} else if (getState().equals("infected")){
			if (blocksPlaced == 3){
				Bukkit.getPlayer(name).sendMessage(ChatColor.DARK_GRAY + "@" + name + ChatColor.GRAY + ", you have " + ChatColor.DARK_PURPLE + "no" + ChatColor.GRAY + " blocks left!" );
			} else {
				Bukkit.getPlayer(name).sendMessage(ChatColor.DARK_GRAY + "@" + name + ChatColor.GRAY + ", you have " + ChatColor.DARK_PURPLE + (3 - blocksPlaced) + ChatColor.GRAY + " blocks left!" );
			}
		}
	}

	public int getBlocksPlaced() {
		return blocksPlaced;
	}

	public void resetBlocksPlaced(){
		blocksPlaced = 0;
	}
	//#############Block Counter############
}
