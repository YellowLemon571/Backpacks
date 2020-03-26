package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveBackpack implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(Main.msgPrefix + "Please specify a target.");
            return true;
        }
        Player target = Bukkit.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Main.msgPrefix + "Target not found.");
            return true;
        }
        if (BackpacksAPI.getEmptySlots(target) >= 1) {
            target.getInventory().addItem(BackpacksAPI.getBackpack());
            player.sendMessage(Main.msgPrefix + "A backpack has been added to " + target.getDisplayName() + "'s inventory.");
        } else {
            player.sendMessage(Main.msgPrefix + "There isn't enough space in " + target.getDisplayName() + "'s inventory.");
            return true;
        }
        return true;
    }
}
