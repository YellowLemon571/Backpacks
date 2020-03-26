package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.io.IOException;

public class BackpackListener implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().isSimilar(BackpacksAPI.getBackpack()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            String playerUUID = player.getUniqueId().toString();
            FileConfiguration backpacks = Main.bAPI.getBackpackYml();
            Inventory backpack;
            if (backpacks.contains(playerUUID)) {
                try {
                    backpack = BackpacksAPI.base64ToInv(backpacks.getString(playerUUID));
                } catch (IOException e) {
                    backpack = Bukkit.getServer().createInventory(null, 27, "Backpack");
                }
            } else {
                backpack = Bukkit.getServer().createInventory(null, 27, "Backpack");
            }
            player.openInventory(backpack);
        }
    }

    @EventHandler
    public void onBackpackClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Backpack")) {
            FileConfiguration backpacks = Main.bAPI.getBackpackYml();
            Player player = (Player) event.getPlayer();
            String playerUUID = player.getUniqueId().toString();
            Inventory backpack = event.getInventory();
            String inventoryString = BackpacksAPI.invToBase64(backpack);
            backpacks.set(playerUUID, inventoryString);
            Main.bAPI.saveBackpackYml(backpacks);
        }
    }

    @EventHandler
    public void onBackpackClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView topView = player.getOpenInventory();
        if (topView.getTitle().equals("Backpack") && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(BackpacksAPI.getBackpack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBackpackPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().isSimilar(BackpacksAPI.getBackpack())) {
            event.setCancelled(true);
        }
    }
}
