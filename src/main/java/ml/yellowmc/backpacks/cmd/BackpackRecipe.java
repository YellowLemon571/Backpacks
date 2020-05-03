package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BackpackRecipe {
    public static void command(Player player) {
        Inventory recipeMenu = BackpacksAPI.getRecipeInventory();
        player.openInventory(recipeMenu);
        return;
    }
}
