package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackpackListener implements Listener {
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND && event.getItem() != null && event.getItem().isSimilar(BackpacksAPI.getBackpack()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            if (player.hasPermission("backpacks.use")) {
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
            } else {
                player.sendMessage(Main.errPrefix + "You do not have permission to open backpacks.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Backpack")) {
            FileConfiguration backpacks = Main.bAPI.getBackpackYml();
            Player player = (Player) event.getPlayer();
            String playerUUID = player.getUniqueId().toString();
            Inventory backpack = event.getInventory();
            String inventoryString = BackpacksAPI.invToBase64(backpack);
            backpacks.set(playerUUID, inventoryString);
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (inventoryView.getTitle().equals("Backpack") && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(BackpacksAPI.getBackpack())) {
            event.setCancelled(true);
        } else {
            if (clickedInventory != null && clickedInventory.getType() == InventoryType.CHEST && inventoryView.getTitle().equals("Set Crafting Recipe")) {
                if (event.getSlot() == 17) {
                    inventoryView.close();
                } else if (event.getSlot() == 26) {
                    char[] recipeCharacters = {};
                    int index = 0;
                    HashMap<Character, Material> ingredientMap = new HashMap<Character, Material>();
                    for (int a = 0; a <= 2; a++) {
                        for (int b = 3 + (a * 9); b <= 5 + (a * 9); b++) {
                            ItemStack recipeItem = clickedInventory.getItem(b);
                            if (recipeItem != null) {
                                if (!ingredientMap.containsValue(recipeItem.getType())) {
                                    ingredientMap.put(Character.forDigit(index, 10), recipeItem.getType());
                                    recipeCharacters = ArrayUtils.add(recipeCharacters, Character.forDigit(index, 10));
                                    index++;
                                } else {
                                    recipeCharacters = ArrayUtils.add(recipeCharacters, BackpacksAPI.getIngredientKey(ingredientMap, recipeItem.getType()));
                                }
                            } else {
                                recipeCharacters = ArrayUtils.add(recipeCharacters, ' ');
                            }
                        }
                    }
                    String[] recipeRows = {};
                    for (int a = 0; a <= 2; a++) {
                        String row = "";
                        for (int b = (a * 3); b <= 2 + (a * 3); b++) {
                            row = row + recipeCharacters[b];
                        }
                        recipeRows = (String[]) ArrayUtils.add(recipeRows, row);
                    }
                    Bukkit.removeRecipe(Main.bAPI.getBackpackKey());
                    ShapedRecipe newRecipe = new ShapedRecipe(Main.bAPI.getBackpackKey(), BackpacksAPI.getBackpack());
                    newRecipe.shape(recipeRows[0], recipeRows[1], recipeRows[2]);
                    for (Map.Entry<Character, Material> entry : ingredientMap.entrySet()) {
                        newRecipe.setIngredient(entry.getKey(), entry.getValue());
                    }
                    boolean addRecipe = Bukkit.addRecipe(newRecipe);
                    inventoryView.close();
                    if (addRecipe) {
                        Main.config.set("backpackRecipe.shape.top", recipeRows[0]);
                        Main.config.set("backpackRecipe.shape.middle", recipeRows[1]);
                        Main.config.set("backpackRecipe.shape.bottom", recipeRows[2]);
                        Main.config.set("backpackRecipe.materials", null);
                        for (Map.Entry<Character, Material> entry : ingredientMap.entrySet()) {
                            String key = entry.getKey().toString();
                            String value = entry.getValue().toString();
                            Main.config.set("backpackRecipe.materials." + key, value);
                        }
                        Main.bAPI.saveConfig();
                        player.sendMessage(Main.msgPrefix + "Backback's recipe successfully changed.");
                    } else {
                        player.sendMessage(Main.errPrefix + "Error changing backpack's recipe! Check console for any errors.");
                    }
                } else if (!ArrayUtils.contains(new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23}, event.getSlot())) {
                    event.setCancelled(true);
                }
            } else if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER && inventoryView.getTitle().equals("Set Crafting Recipe") && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBackpackPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().isSimilar(BackpacksAPI.getBackpack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBackpackCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("backpacks.craft") && event.getCurrentItem() != null && event.getCurrentItem().isSimilar(BackpacksAPI.getBackpack())) {
            player.sendMessage(Main.errPrefix + "You do not have permission to craft backpacks.");
            event.setCancelled(true);
        }
    }
}
