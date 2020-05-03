package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;

public class PlayerRightClick implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && BackpacksAPI.compareItems(event.getItem(), BackpacksAPI.getBackpack()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            if (player.hasPermission("backpacks.use")) {
                ItemMeta item_meta = event.getItem().getItemMeta();
                if (item_meta == null) return;
                List<String> item_lore = item_meta.getLore();
                if (item_lore == null) return;
                String backpackID = item_lore.get(0);
                FileConfiguration backpacks = Main.bAPI.getBackpackYml();
                Inventory backpack;
                if (backpacks.contains(backpackID)) {
                    try {
                        backpack = BackpacksAPI.base64ToInv(backpacks.getString(backpackID));
                    } catch (IOException e) {
                        backpack = Bukkit.getServer().createInventory(null, 27, "Backpack");
                    }
                } else {
                    backpack = Bukkit.getServer().createInventory(null, 27, "Backpack");
                }
                player.openInventory(backpack);
            } else {
                player.sendMessage(Main.errPrefix + "You do not have permission to open backpacks.");
                event.setCancelled(true);
            }
        }
    }
}
