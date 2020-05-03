package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.Main;
import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class InventoryClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();
        Inventory clickedInventory = event.getClickedInventory();
        if (inventoryView.getTitle().equals("Backpack") && event.getCurrentItem() != null && BackpacksAPI.compareItems(event.getCurrentItem(), BackpacksAPI.getBackpack())) {
            event.setCancelled(true);
        } else {

            // Recipe Menu

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
            }
            else if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER && inventoryView.getTitle().equals("Set Crafting Recipe") && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                event.setCancelled(true);
            }
        }
    }
}
