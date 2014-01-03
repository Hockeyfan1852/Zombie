package gmail.theultimatebudgie.ZombieSurvival;

import org.bukkit.configuration.ConfigurationSection;

public class Stats {
	public int kills;
	public int wins;
	public String name;
	public Stats(ConfigurationSection section,String name){
		if(!section.contains("kills")){
			kills = 0;
		}else{
			kills = section.getInt("kills");
		}
		if(!section.contains("wins")){
			wins = 0;
		}else{
			wins = section.getInt("wins");
		}
		this.name = name;
	}
	public void save(ConfigurationSection statsC) {
		if(!statsC.contains(name)){
			statsC.createSection(name);
		}
		ConfigurationSection stats = statsC.getConfigurationSection(name);
		stats.set("kills", kills);
		stats.set("wins", wins);
	}
}
