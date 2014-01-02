package gmail.theultimatebudgie.ZombieSurvival;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigAccessor {
	ZombieCore plugin;
	
	public ConfigAccessor(ZombieCore plugin){
		this.plugin = plugin;
	}
	
	
	public void readMainConfig(ZombieCore plugin) {
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveDefaultConfig();
		
		FileConfiguration config = plugin.getConfig();
		
		plugin.breakOfficialTime = config.getInt("BreakTime");
		plugin.votingOfficialTime = config.getInt("VoteTime");
		//*Converted to ticks
		plugin.risingTime = config.getInt("RisingTime") * 20;
		plugin.worldsList = config.getStringList("Worlds");
		
	}
	
	public void loadMapConfig(ZombieCore plugin, World world) {
		String worldName = world.getName();
		
		//Gets the world folder (to put all world files :D... for sorting purposes)
		File worldsConfigFolder = new File(plugin.getDataFolder(), "/worlds");
		if (!worldsConfigFolder.exists()){
			worldsConfigFolder.mkdir();
		}
		
		File file = new File(worldsConfigFolder, worldName + ".yml");
		
		//If file doesn't exist, create it!
		if (file.isFile()) {
			plugin.currentConfig = YamlConfiguration.loadConfiguration(file);
		} else {
			try {
				file.createNewFile();
				FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
	
				plugin.currentConfig = yml;
			} catch (IOException e) {
				plugin.getLogger().severe("Unable to create Map Config... close server!");
				e.printStackTrace();
			}
		}
		
		//Makes sure all config options are automatically added
		plugin.currentConfig.addDefault("MapName", worldName);
		plugin.currentConfig.addDefault("Author", "Unknown");
		plugin.currentConfig.addDefault("Co-Authors", null);
		plugin.currentConfig.addDefault("RoundTime", 600);
		plugin.currentConfig.addDefault("HidingTime", 50);
		plugin.currentConfig.addDefault("DisableShop", false);
		
		//Copies all the above values if they don't exist!
		plugin.currentConfig.options().copyDefaults(true);
		try {
			plugin.currentConfig.save(file);
		} catch (IOException e) {
			plugin.getLogger().severe("Unable to save Map Config... close server!");
			e.printStackTrace();
		}
		
		//Load all the values
		plugin.mapName = plugin.currentConfig.getString("MapName");
		plugin.author = plugin.currentConfig.getString("Author");
		plugin.coAuthors = plugin.currentConfig.getStringList("Co-Authors");
		plugin.roundTime = plugin.currentConfig.getInt("RoundTime");
		plugin.hideTime = plugin.currentConfig.getInt("HidingTime");
		plugin.disableShop = plugin.currentConfig.getBoolean("DisableShop");
		
	}
	
	public HashMap<String, HashMap<String, ArrayList<String>>> getPermissions(){
		FileConfiguration ranksYml = null;
		HashMap<String, ArrayList<String>> inGameMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> refMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, HashMap<String, ArrayList<String>>> mainMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
		
		//Gets the permissions folder
		File permsConfigFolder = new File(plugin.getDataFolder(), "/perms");
		if (!permsConfigFolder.exists()){
			permsConfigFolder.mkdir();
		}
		
		File file = new File(permsConfigFolder, "Ranks.yml");
		if (file.isFile()) {
			ranksYml = YamlConfiguration.loadConfiguration(file);
		} else {
			try {
				file.createNewFile();
				FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
	
				ranksYml = yml;
			} catch (IOException e) {
				plugin.getLogger().severe("Unable to create Perms Config... close server!");
				e.printStackTrace();
			}
		}
		
		//Makes sure all config options are automatically added
		ranksYml.addDefault("InGame.Player", Arrays.asList("perms.perm"));
		ranksYml.addDefault("InGame.Trusted", Arrays.asList("perms.perm"));
		ranksYml.addDefault("InGame.Operator", Arrays.asList("perms.perm"));
		ranksYml.addDefault("InGame.Veteran", Arrays.asList("perms.perm"));
		ranksYml.addDefault("InGame.Controller", Arrays.asList("perms.perm"));
		ranksYml.addDefault("InGame.Admin", Arrays.asList("perms.perm"));
		
		ranksYml.addDefault("Ref.Operator", Arrays.asList("perms.perm"));
		ranksYml.addDefault("Ref.Veteran", Arrays.asList("perms.perm"));
		ranksYml.addDefault("Ref.Controller", Arrays.asList("perms.perm"));
		ranksYml.addDefault("Ref.Admin", Arrays.asList("perms.perm"));
		
		
		//Copies all the above values if they don't exist!
		ranksYml.options().copyDefaults(true);
		try {
			ranksYml.save(file);
		} catch (IOException e) {
			plugin.getLogger().severe("Unable to save Perms Config... close server!");
			e.printStackTrace();
		}
		
		//Adds the permission strings to the HashMaps:
		ArrayList<String> permList = new ArrayList<String>();
		for (String key : ranksYml.getConfigurationSection("InGame").getKeys(false)){
			permList.clear();
			for (String perm : ranksYml.getConfigurationSection("InGame").getStringList(key)){
				plugin.getLogger().info("DEBUG : InGame: " + key + " - " + perm);
				permList.add(perm);
			}
			inGameMap.put(key, new ArrayList<String>(permList));
		}

		mainMap.put("InGame", inGameMap);

		for (String key : ranksYml.getConfigurationSection("Ref").getKeys(false)){
			permList.clear();
			for (String perm : ranksYml.getConfigurationSection("Ref").getStringList(key)){
				plugin.getLogger().info("DEBUG: Ref: " + key + "." + perm);
				permList.add(perm);
			}
			refMap.put(key, new ArrayList<String>(permList));
		}
		mainMap.put("Ref", refMap);
		
		return mainMap;
	}
	
	public FileConfiguration checkPlayerPermsFile(){
		FileConfiguration playersYml = null;
		File permsConfigFolder = new File(plugin.getDataFolder(), "/perms");
		
		File file = new File(permsConfigFolder, "Players.yml");
		if (file.isFile()) {
			playersYml = YamlConfiguration.loadConfiguration(file);
		} else {
			try {
				file.createNewFile();
				playersYml = YamlConfiguration.loadConfiguration(file);
			} catch (IOException e) {
				plugin.getLogger().severe("Unable to create Players Permission Config... close server!");
				e.printStackTrace();
			}
		}
		
		//Makes sure all config options are automatically added
		playersYml.addDefault("Players.ExampleUser.Rank", "Player");
		
		//Copies all the above values if they don't exist!
		playersYml.options().copyDefaults(true);
		try {
			playersYml.save(file);
		} catch (IOException e) {
			plugin.getLogger().severe("Unable to save Player Perms Config... close server!");
			e.printStackTrace();
		}
		
		return playersYml;
	}
	
}
