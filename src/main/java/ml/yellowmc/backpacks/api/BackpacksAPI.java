package ml.yellowmc.backpacks.api;

import ml.yellowmc.backpacks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackpacksAPI {

    private Plugin plugin;
    private static FileConfiguration config;

    public BackpacksAPI(Plugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    public FileConfiguration getBackpackYml() {
        File backpacksFile = new File(plugin.getDataFolder() + "/backpacks.yml");
        return YamlConfiguration.loadConfiguration(backpacksFile);
    }

    public void saveBackpackYml(FileConfiguration config) {
        try {
            File backpacksFile = new File(plugin.getDataFolder() + "/backpacks.yml");
            config.save(backpacksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createBackpackRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "backpack");
        ShapedRecipe recipe = new ShapedRecipe(key, getBackpack());
        recipe.shape("LGL", "LCL", "LGL");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('C', Material.CHEST);
        Bukkit.addRecipe(recipe);
    }

    public static ItemStack getBackpack() {
        Material backpackMaterial;
        try {
            backpackMaterial = Material.valueOf(Main.config.getString("backpackMaterial"));
        } catch (IllegalArgumentException e) {
            backpackMaterial = Material.CHEST;
        }
        List<String> backpackLore = new ArrayList<String>();
        backpackLore.add(config.getString("backpackLore"));
        ItemStack backpack = new ItemStack(backpackMaterial);
        ItemMeta meta = backpack.getItemMeta();
        meta.setDisplayName(config.getString("backpackName"));
        meta.setLore(backpackLore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        backpack.setItemMeta(meta);
        return backpack;
    }

    public static int getEmptySlots(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();
        int i = 0;
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        }
        return 36 - i;
    }

    public static String invToBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static Inventory base64ToInv(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt(), "Backpack");
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
