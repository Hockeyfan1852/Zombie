package gmail.theultimatebudgie.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gmail.theultimatebudgie.ZombieSurvival.ZombieCore;

public class TestCommands implements CommandExecutor {
	ZombieCore plugin;
	
	public TestCommands (ZombieCore plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("StartTimer")) {
			plugin.startTimer();
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("endround")){
			plugin.endRound();
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("saveworld")){
			plugin.currentWorld.save();
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("ref")){
			Player player = (Player) sender;
			if (plugin.playerContainer.get(player.getName()).getState().equals("human") || plugin.playerContainer.get(player.getName()).getState().equals("infected")) {
				plugin.playerContainer.get(player.getName()).setRef();
			} else {
				if (plugin.getPhase().equalsIgnoreCase("round")){
					plugin.playerContainer.get(player.getName()).setInfected();
				} else {
					plugin.playerContainer.get(player.getName()).setHuman();
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("starteverything")){
			plugin.startTimer();
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("stats")){
			for(String string:plugin.showStats(sender.getName())){
				sender.sendMessage(string);
			}
			return true;
		}
		return false;
	}
}
