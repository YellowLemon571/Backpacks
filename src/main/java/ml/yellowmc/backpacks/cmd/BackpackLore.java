package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackpackLore implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(Main.errPrefix + "Please specify a lore.");
            return true;
        }
        String lore = "";
        for (int i = 0; i < args.length; i++) {
            lore = lore + args[i] + ((args.length - 1 > 1) ? " " : "");
        }
        String formattedLore = lore.replace('&', '§');
        Main.config.set("backpackLore", formattedLore);
        Main.bAPI.saveConfig();
        player.sendMessage(Main.msgPrefix + "Backpack's lore successfully changed to " + formattedLore);
        return true;
    }
}
