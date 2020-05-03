package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BackpackMaterial {
    public static void command(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Main.errPrefix + "Please specify a material.");
            return;
        }
        Material material = Material.getMaterial(args[1].toUpperCase());
        if (material == null) {
            player.sendMessage(Main.errPrefix + "Material not found.");
            return;
        }
        Main.config.set("backpackMaterial", args[1].toUpperCase());
        Main.bAPI.saveConfig();
        Bukkit.removeRecipe(Main.bAPI.getBackpackKey());
        Main.bAPI.createBackpackRecipe();
        player.sendMessage(Main.msgPrefix + "Backpack's material changed successfully.");
        return;
    }
}
