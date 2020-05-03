package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BackpackCraft implements Listener {
    @EventHandler
    public void onBackpackCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("backpacks.craft") && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(BackpacksAPI.getBackpack())) {
            player.sendMessage(Main.errPrefix + "You do not have permission to craft backpacks.");
            event.setCancelled(true);
        } else {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            ItemMeta item_meta = item.getItemMeta();
            if (item_meta == null) return;
            List<String> item_lore = item_meta.getLore();
            if (item_lore == null) return;
            Main.id_index++;
            item_lore.add(0, String.valueOf(Main.id_index));
            item_meta.setLore(item_lore);
            item.setItemMeta(item_meta);
            Main.bAPI.updateBackpackIDs();
        }
    }
}
