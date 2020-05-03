package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BackpackName {
    public static void command(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Main.errPrefix + "Please specify a name.");
            return;
        }
        String name = "";
        for (int i = 1; i < args.length; i++) {
            name += args[i] + ((args.length - 1 > 1) ? " " : "");
        }
        String formattedName = name.replace('&', '§');
        Main.config.set("backpackName", formattedName);
        Main.bAPI.saveConfig();
        Bukkit.removeRecipe(Main.bAPI.getBackpackKey());
        Main.bAPI.createBackpackRecipe();
        player.sendMessage(Main.msgPrefix + "Backpack's name successfully changed to " + formattedName);
        return;
    }
}
