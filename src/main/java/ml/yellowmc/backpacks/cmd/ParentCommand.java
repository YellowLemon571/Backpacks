package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ParentCommand implements CommandExecutor {

    private Map<String, String> helpMessageList = new HashMap<String, String>();

    public ParentCommand() {
        helpMessageList.put("give", "Gives a backpack to a specified player.");
        helpMessageList.put("lore", "Changes the backpack's lore.");
        helpMessageList.put("material", "Changes the backpack's material.");
        helpMessageList.put("name", "Changes the backpack's name.");
        helpMessageList.put("recipe", "Changes the backpack's crafting recipe.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(helpMessage());
        } else {
            if (args[0].equalsIgnoreCase("give") && checkPermission(player, "backpacks.give")) {
                BackpackGive.command(player, args);
            } else if (args[0].equalsIgnoreCase("lore") && checkPermission(player, "backpacks.modify")) {
                BackpackLore.command(player, args);
            } else if (args[0].equalsIgnoreCase("material") && checkPermission(player, "backpacks.modify")) {
                BackpackMaterial.command(player, args);
            } else if (args[0].equalsIgnoreCase("name") && checkPermission(player, "backpacks.modify")) {
                BackpackName.command(player, args);
            } else if (args[0].equalsIgnoreCase("recipe") && checkPermission(player, "backpacks.modify")) {
                BackpackRecipe.command(player);
            } else {
                player.sendMessage(helpMessage());
            }
        }
        return true;
    }

    private boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(Main.errPrefix + "You do not have the correct permission to perform this command (§c" + permission + "§f).");
            return false;
        } else {
            return true;
        }
    }

    private String helpMessage() {
        String helpMessage = Main.msgPrefix + "List of subcommands:\n";
        for (Map.Entry<String, String> entry : helpMessageList.entrySet()) {
            String name = entry.getKey();
            String description = entry.getValue();
            helpMessage += "§7- §e" + name + "§7: " + description + "\n";
        }
        helpMessage += "§rIf you're attempting do execute a subcommand and are seeing this help message, you do not have permission to use that subcommand.";
        return helpMessage;
    }
}
