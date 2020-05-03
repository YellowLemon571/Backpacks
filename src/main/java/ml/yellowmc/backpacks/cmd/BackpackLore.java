package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BackpackLore {
    public static void command(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Main.errPrefix + "Please specify a lore.");
            return;
        }
        String lore = "";
        for (int i = 1; i < args.length; i++) {
            lore += args[i] + ((args.length - 1 > 1) ? " " : "");
        }
        String formattedLore = lore.replace('&', '§');
        Main.config.set("backpackLore", formattedLore);
        Main.bAPI.saveConfig();
        Bukkit.removeRecipe(Main.bAPI.getBackpackKey());
        Main.bAPI.createBackpackRecipe();
        player.sendMessage(Main.msgPrefix + "Backpack's lore successfully changed to " + formattedLore);
        return;
    }
}
