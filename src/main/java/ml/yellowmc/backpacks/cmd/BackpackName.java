package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackName implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(Main.errPrefix + "Please specify a name.");
            return true;
        }
        String name = "";
        for (int i = 0; i < args.length; i++) {
            name = name + args[i] + ((args.length - 1 > 1) ? " " : "");
        }
        String formattedName = name.replace('&', '§');
        Main.config.set("backpackName", formattedName);
        Main.bAPI.saveConfig();
        player.sendMessage(Main.msgPrefix + "Backpack's name successfully changed to " + formattedName);
        return true;
    }
}
