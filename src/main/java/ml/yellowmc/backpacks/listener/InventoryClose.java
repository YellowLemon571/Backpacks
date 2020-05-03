package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryClose implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Backpack")) {
            FileConfiguration backpacks = Main.bAPI.getBackpackYml();
            Player player = (Player) event.getPlayer();
            Inventory backpack = event.getInventory();
            String inventoryString = BackpacksAPI.invToBase64(backpack);
            ItemStack curr_item = player.getInventory().getItemInMainHand();
            ItemMeta curr_meta = curr_item.getItemMeta();
            if (curr_meta == null) return;
            List<String> curr_lore = curr_meta.getLore();
            if (curr_lore == null) return;
            String id = curr_lore.get(0);
            backpacks.set(id, inventoryString);
            Main.bAPI.saveBackpackYml(backpacks);
        } else if (event.getView().getTitle().equals("Set Crafting Recipe")) {
            Player player = (Player) event.getPlayer();
            List<ItemStack> recipeItems = new ArrayList<ItemStack>();
            for (int a = 0; a <= 2; a++) {
                for (int b = 3 + a; b <= 21 + a; b = b + 9) {
                    ItemStack recipeItem = event.getInventory().getItem(b);
                    if (recipeItem != null) {
                        recipeItems.add(recipeItem);
                    }
                }
            }
            for (ItemStack item : recipeItems) {
                player.getInventory().addItem(item);
            }
        }
    }
}
