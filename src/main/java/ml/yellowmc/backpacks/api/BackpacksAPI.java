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

    public BackpacksAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration loadConfig() {
        File file = new File(plugin.getDataFolder() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getString("version") == null ||
                !config.getString("version").equals(plugin.getDescription().getVersion()) ||
                config.getString("backpackMaterial") == null ||
                Material.getMaterial(config.getString("backpackMaterial")) == null ||
                config.getString("backpackName") == null ||
                config.getString("backpackName").isEmpty() ||
                config.getString("backpackLore") == null ||
                config.getString("backpackLore").isEmpty() ||
                config.getConfigurationSection("backpackRecipe") == null ||
                config.getConfigurationSection("backpackRecipe.shape") == null ||
                config.getString("backpackRecipe.shape.top") == null ||
                config.getString("backpackRecipe.shape.middle") == null ||
                config.getString("backpackRecipe.shape.bottom") == null ||
                config.getConfigurationSection("backpackRecipe.materials") == null ||
                config.getConfigurationSection("backpackRecipe.materials").getValues(false).isEmpty()) {
            plugin.getLogger().warning("Config is outdated or broken. Creating new config.");
            config = resetConfig(file);
        }
        File file_storage = new File(plugin.getDataFolder() + "/backpacks.yml");
        FileConfiguration backpacks = YamlConfiguration.loadConfiguration(file_storage);
        if (!backpacks.contains("id-index")) {
            backpacks.set("id-index", 0);
            saveBackpackYml(backpacks);
        } else {
            Main.id_index = backpacks.getInt("id-index");
        }
        return config;
    }

    private FileConfiguration resetConfig(File file) {
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
        file.getAbsoluteFile().renameTo(oldFile.getAbsoluteFile());
        plugin.saveDefaultConfig();
        return plugin.getConfig();
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
        backpackLore.add(Main.config.getString("backpackLore"));
        ItemStack backpack = new ItemStack(backpackMaterial);
        ItemMeta meta = backpack.getItemMeta();
        meta.setDisplayName(Main.config.getString("backpackName"));
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

    public static boolean compareItems(ItemStack a, ItemStack b) {
        Material a_material = a.getType();
        Material b_material = b.getType();
        ItemMeta a_meta = a.getItemMeta();
        ItemMeta b_meta = b.getItemMeta();
        if (a_meta == null || b_meta == null) return false;
        String a_name = a_meta.getDisplayName();
        String b_name = b_meta.getDisplayName();
        return a_material.equals(b_material) && a_name.equals(b_name);
    }

    public void updateBackpackIDs() {
        File file = new File(plugin.getDataFolder() + "/backpacks.yml");
        FileConfiguration backpacks = YamlConfiguration.loadConfiguration(file);
        backpacks.set("id-index", Main.id_index);
        try {
            backpacks.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
