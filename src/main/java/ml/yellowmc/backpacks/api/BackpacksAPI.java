package ml.yellowmc.backpacks.api;

import ml.yellowmc.backpacks.Main;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BackpacksAPI {

    private Plugin plugin;
    private static FileConfiguration config;

    public BackpacksAPI(Plugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    public FileConfiguration loadConfig() {
        File file = new File(plugin.getDataFolder() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getString("version") == null || !config.getString("version").equals(plugin.getDescription().getVersion())) {
            plugin.getLogger().info("Config outdated. Creating new config.");
            File oldFile = null;
            boolean exists = true;
            int index = 0;
            while (exists) {
                oldFile = new File(plugin.getDataFolder() + "/config.yml.old" + String.valueOf(index));
                if (!oldFile.exists()) {
                    exists = false;
                } else {
                    index++;
                }
            }
            boolean rename = file.getAbsoluteFile().renameTo(oldFile.getAbsoluteFile());
            plugin.saveDefaultConfig();
        }
        return config;
    }

    public FileConfiguration getBackpackYml() {
        File backpacksFile = new File(plugin.getDataFolder() + "/backpacks.yml");
        return YamlConfiguration.loadConfiguration(backpacksFile);
    }

    public void saveConfig() {
        try {
            File configFile = new File(plugin.getDataFolder() + "/config.yml");
            Main.config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBackpackYml(FileConfiguration config) {
        try {
            File backpacksFile = new File(plugin.getDataFolder() + "/backpacks.yml");
            config.save(backpacksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NamespacedKey getBackpackKey() {
        return new NamespacedKey(plugin, "backpack");
    }

    public void createBackpackRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "backpack");
        ShapedRecipe recipe = new ShapedRecipe(key, getBackpack());
        String topRow = Main.config.getString("backpackRecipe.shape.top");
        String middleRow = Main.config.getString("backpackRecipe.shape.middle");
        String bottomRow = Main.config.getString("backpackRecipe.shape.bottom");
        recipe.shape(topRow, middleRow, bottomRow);
        Map<String, Object> recipeMaterials = Main.config.getConfigurationSection("backpackRecipe.materials").getValues(false);
        for (Map.Entry<String, Object> entry : recipeMaterials.entrySet()) {
            char character = entry.getKey().charAt(0);
            String string = (String) entry.getValue();
            recipe.setIngredient(character, Material.getMaterial(string.toUpperCase()));
        }
        boolean addRecipe = Bukkit.addRecipe(recipe);
        if (!addRecipe) {
            plugin.getLogger().severe("Failed to register recipe!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
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

    private static ItemStack getUIItemStack(Material material, String name, String lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName((name != null) ? name : " ");
        if (lore != null) {
            String[] itemLore = ChatPaginator.wordWrap(lore, 30);
            itemMeta.setLore(Arrays.asList(itemLore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static Inventory getRecipeInventory() {
        Inventory inventory = Bukkit.getServer().createInventory(null, 27, "Set Crafting Recipe");
        inventory.setItem(0, getUIItemStack(Material.PAPER, null, "§d§oArrange the new crafting recipe within the glass panes. Click Confirm to confirm the new crafting recipe."));
        inventory.setItem(2, getUIItemStack(Material.BLACK_STAINED_GLASS_PANE, null, null));
        inventory.setItem(6, getUIItemStack(Material.BLACK_STAINED_GLASS_PANE, null, null));
        inventory.setItem(11, getUIItemStack(Material.BLACK_STAINED_GLASS_PANE, null, null));
        inventory.setItem(15, getUIItemStack(Material.BLACK_STAINED_GLASS_PANE, null, null));
        inventory.setItem(20, getUIItemStack(Material.BLACK_STAINED_GLASS_PANE, null, null));
        inventory.setItem(24, getUIItemStack(Material.BLACK_STAINED_GLASS_PANE, null, null));
        inventory.setItem(17, getUIItemStack(Material.BARRIER, "§c§lCancel", null));
        inventory.setItem(26, getUIItemStack(Material.WRITABLE_BOOK, "§a§lConfirm", null));
        return inventory;
    }

    public static Character getIngredientKey(HashMap<Character, Material> map, Material value) {
        for (Map.Entry<Character, Material> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
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
