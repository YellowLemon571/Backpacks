package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackMaterial implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(Main.errPrefix + "Please specify a material.");
            return true;
        }
        Material material = Material.getMaterial(args[0].toUpperCase());
        if (material == null) {
            player.sendMessage(Main.errPrefix + "Material not found.");
            return true;
        }
        Main.config.set("backpackMaterial", args[0].toUpperCase());
        Main.bAPI.saveConfig();
        player.sendMessage(Main.msgPrefix + "Backpack's material changed successfully.");
        return true;
    }
}
