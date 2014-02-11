package gmail.theultimatebudgie.ZombieSurvival;

import gmail.theultimatebudgie.Commands.TestCommands;
import gmail.theultimatebudgie.Listeners.BlockListener;
import gmail.theultimatebudgie.Listeners.ChatListener;
import gmail.theultimatebudgie.Listeners.InventoryListener;
import gmail.theultimatebudgie.Listeners.JoinQuitListener;
import gmail.theultimatebudgie.Listeners.MobListener;
import gmail.theultimatebudgie.Listeners.MovementListener;
import gmail.theultimatebudgie.Listeners.PlayerListener;
import gmail.theultimatebudgie.Listeners.WorldListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ZombieCore extends JavaPlugin {
	//Non-static classes used:
	public InfectionDetection infectionDetection = null;
	public RoundTimer roundTimer = null;
	public Random randomGenerator = new Random();
	public ConfigAccessor configAccessor = null;

	//Command classes
	public TestCommands testCommands = null;

	//Listeners
	public JoinQuitListener joinQuitListener = null;
	public InventoryListener inventoryListener = null;
	public MobListener mobListener = null;
	public WorldListener worldListener = null;
	public PlayerListener playerListener = null;
	public MovementListener movementListener = null;
	public BlockListener blockListener = null;
	public ChatListener chatListener = null;

	//Phases: vote, break, hide, round
	String currentPhase;

	//Holds all PlayerContainer objects
	public HashMap<String, PlayerContainer> playerContainer = new HashMap<String, PlayerContainer>();

	//Worlds:
	public World currentWorld = null, lastWorld = null, nextWorld = null;
	public String curentWorldName;

	//For randomly choosing voting maps :)
	public List<String> votingWorldsList = new ArrayList<String>();
	public String map1, map2;
	public Integer votes1 = 0, votes2 = 0;

	//Chat message prefixes:
	public String prefixWorld = ChatColor.DARK_GRAY + "(" + ChatColor.BLUE + "Maps" + ChatColor.DARK_GRAY + ")" + ChatColor.GRAY + ": ";
	public String prefixRound = ChatColor.DARK_GRAY + "(" + ChatColor.DARK_RED + "Round" + ChatColor.DARK_GRAY + ")" + ChatColor.GRAY + ": ";
	public String prefixVoting = ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + "Voting" + ChatColor.DARK_GRAY + ")" + ChatColor.GRAY + ": ";
	public String prefixRules = ChatColor.DARK_GRAY + "(" + ChatColor.DARK_RED + "Rules" + ChatColor.DARK_GRAY + ")" + ChatColor.GRAY + ": ";

	//Loaded from main config! ################
	public int breakOfficialTime, risingTime, votingOfficialTime;
	public Integer breakTime = null, votingTime = null;
	public List<String> worldsList = new ArrayList<String>();
	public static HashMap<String,Stats> stats = new HashMap<String,Stats>();
	//#################################


	//Curent Map config variables! ######
	FileConfiguration currentConfig;

	public String mapName, author;
	public List<String> coAuthors = new ArrayList<String>();
	public Integer hideTime = null, roundTime = null;
	public boolean disableShop;
	//###################################

	@Override
	public void onEnable(){

		createInstances();

		configAccessor.readMainConfig(this);

		registerCommands();

		registerListeners();

		startTimer();
		
		loadStats();

		infectionDetection.MainTask();

	}

	private void loadStats() {
		FileConfiguration config = this.getConfig();
		if(!config.contains("stats")){
			config.createSection("stats");
		}
		ConfigurationSection statsC = config.getConfigurationSection("stats");
		Set<String> keys = statsC.getKeys(false);
		for(String key:keys){
			stats.put(key, new Stats(statsC.getConfigurationSection(key),key));
		}
	}
	
	private void saveStats() {
		FileConfiguration config = this.getConfig();
		if(!config.contains("stats")){
			config.createSection("stats");
		}
		ConfigurationSection statsC = config.getConfigurationSection("stats");
		Set<String> keys = stats.keySet();
		for(String key:keys){
			stats.get(key).save(statsC);
		}
		this.saveConfig();
	}
	
	public void humansWin() {
		Set<String> keys = playerContainer.keySet();
		for(String key:keys){
			PlayerContainer cont = playerContainer.get(key);
			if(cont.getState().equalsIgnoreCase("human")){
				stats.get(key).wins++;
			}
		}
	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer("Server restarting... See you in a few minutes!");
		}

		//Make sure the worlds don't save :D
		if (currentWorld != null){
			Bukkit.unloadWorld(currentWorld, false);
		}
		if (lastWorld != null){
			Bukkit.unloadWorld(lastWorld, false);
		}
		
		saveStats();
		// TODO Insert logic to be performed when the plugin is disabled
	}

	public void createInstances() {
		infectionDetection = new InfectionDetection(this);

		roundTimer = new RoundTimer(this);

		configAccessor = new ConfigAccessor(this);
	}

	public void registerListeners() {
		joinQuitListener = new JoinQuitListener(this);
		this.getServer().getPluginManager().registerEvents(joinQuitListener, this);

		inventoryListener = new InventoryListener(this);
		this.getServer().getPluginManager().registerEvents(inventoryListener, this);

		mobListener = new MobListener(this);
		this.getServer().getPluginManager().registerEvents(mobListener, this);

		playerListener = new PlayerListener(this);
		this.getServer().getPluginManager().registerEvents(playerListener, this);

		worldListener = new WorldListener(this);
		this.getServer().getPluginManager().registerEvents(worldListener, this);

		movementListener = new MovementListener(this);
		this.getServer().getPluginManager().registerEvents(movementListener, this);

		blockListener = new BlockListener(this);
		this.getServer().getPluginManager().registerEvents(blockListener, this);

		chatListener = new ChatListener(this);
		this.getServer().getPluginManager().registerEvents(chatListener, this);

	}

	public void registerCommands() {
		testCommands = new TestCommands(this);

		getCommand("StartTimer").setExecutor(testCommands);
		getCommand("endround").setExecutor(testCommands);
		getCommand("saveworld").setExecutor(testCommands);
		getCommand("ref").setExecutor(testCommands);
	}

	//This starts the main timer which will run continously further on.
	public void startTimer(){

		//Load "lobby" world configuraiton. Lobby must be default world!
		configAccessor.loadMapConfig(this, getServer().getWorld("Lobby"));

		votingTime = -1;
		breakTime = -1;
		switchPhases("hide");

		roundTimer.MainTimer();
	}

	//Switch phases of the server. These phases control what can/can't be done.
	public void switchPhases(String phase) {
		this.currentPhase = phase;
	}


	public String getPhase() {
		return currentPhase;
	}

	public void endRound(){
		//Switch to vote phase
		votingTime = votingOfficialTime;
		breakTime = breakOfficialTime;
		switchPhases("vote");

		//Announce to all!
		getServer().broadcastMessage(prefixRound + ChatColor.RED + "" + ChatColor.BOLD + "The round has ended!");

		//Reset different player counters
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player p : players) {
			playerContainer.get(p.getName()).resetBlocksPlaced();
		}

	}

	public void loadNextWorld(String nextMap) {
		lastWorld = currentWorld;
		Bukkit.getServer().broadcastMessage(prefixWorld + "Loading next map: " + nextMap);
		loadWorld(nextMap);
		configAccessor.loadMapConfig(ZombieCore.this, getServer().getWorld(currentWorld.getName()));
	}

	//Checks to see whether there enough humans to continue (Depends on phase)
	public boolean enoughHumans() {
		int number = 0;
		for (int i = 0; i < playerContainer.size(); i++) {
			PlayerContainer pc = (PlayerContainer) playerContainer.values().toArray()[i];
			if (pc.state.equalsIgnoreCase("human")){
				number++;
			}
		}

		//Enough humans to start a round
		if (currentPhase.equalsIgnoreCase("hide")) {
			if (number > 2) {
				return true;
			} else {
				return false;
			}
		}

		//Whether all the humans are dead.
		if (currentPhase.equalsIgnoreCase("round")) {
			if (number > 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean loadWorld (String worldName) {
		File file = new File(Bukkit.getServer().getWorldContainer(), worldName);
		if(file.isDirectory()) {
			currentWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName));
			currentWorld.setAutoSave(false);

			return true;
		} else {
			file.mkdir();
			currentWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName));
			currentWorld.setAutoSave(false);
			getLogger().severe(currentWorld.getName() + " was missing. Creating blank world instead :(");
			return false;
		}
	}

	public List<String> showStats(String name) {
		List<String> message = Arrays.asList(new String[]{"Stats of "+name});
		if(!stats.containsKey(name)){
			message.add("Stats not found");
			return message;
		}
		message.add("Kills: " + stats.get(name).kills);
		message.add("Wins: " + stats.get(name).wins);
		return message;
	}


	//Not sure if needed yet :) if the above code doesn't work then yes!
	/*public void loadWorldFromTemplate(String worldName) {
    	File template = new File(Bukkit.getServer().getWorldContainer(), worldName);
        /*if(file.isDirectory()) {
        	currentWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName));
        	currentWorld.setAutoSave(false);
        	return true;
        } else {
    		file.mkdir();
    		currentWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName));
    		currentWorld.setAutoSave(false);
    		getLogger().severe(currentWorld.getName() + " was missing. Creating blank world instead :(");
        	return false;
        }//
    	if (!template.isDirectory()){
    		getLogger().severe("Had to create the world... failed round :(");
    		template.mkdir();
    	}

    	Path templatePath = Paths.get(template.getPath());
    	Path targetPath = Paths.get(template.getPath() + " - Current");
    	try {
			Files.copy(templatePath, targetPath);
		} catch (IOException e) {
			getLogger().severe("Error copying template map :(... Close server!");
			e.printStackTrace();
			Bukkit.shutdown();
		}

    	currentWorld = Bukkit.getServer().createWorld(new WorldCreator(worldName + " - Current"));
		currentWorld.setAutoSave(false);
    }*/

}
