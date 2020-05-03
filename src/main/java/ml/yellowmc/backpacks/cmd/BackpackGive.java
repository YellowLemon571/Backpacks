package ml.yellowmc.backpacks.cmd;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BackpackGive {
    public static void command(CommandSender player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Main.errPrefix + "Please specify a target.");
            return;
        }
        Player target = Bukkit.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(Main.errPrefix + "Target not found.");
            return;
        }
        if (BackpacksAPI.getEmptySlots(target) >= 1) {
            ItemStack item = BackpacksAPI.getBackpack();
            ItemMeta item_meta = item.getItemMeta();
            if (item_meta == null) return;
            List<String> item_lore = item_meta.getLore();
            if (item_lore == null) return;
            Main.id_index++;
            item_lore.add(0, String.valueOf(Main.id_index));
            item_meta.setLore(item_lore);
            item.setItemMeta(item_meta);
            Main.bAPI.updateBackpackIDs();
            target.getInventory().addItem(item);
            player.sendMessage(Main.msgPrefix + "A backpack has been added to " + target.getDisplayName() + "'s inventory.");
        } else {
            player.sendMessage(Main.msgPrefix + "There isn't enough space in " + target.getDisplayName() + "'s inventory.");
        }
    }
}
