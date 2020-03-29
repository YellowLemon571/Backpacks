package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BackpackRecipe implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        Inventory recipeMenu = BackpacksAPI.getRecipeInventory();
        player.openInventory(recipeMenu);
        return true;
    }
}
