package ml.yellowmc.backpacks;

import ml.yellowmc.backpacks.api.BackpacksAPI;
import ml.yellowmc.backpacks.cmd.ParentCommand;
import ml.yellowmc.backpacks.listener.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    public static BackpacksAPI bAPI;
    public static FileConfiguration config;
    public static int id_index;
    public static final String msgPrefix = "§7[§eBackpacks§7] §f";
    public static final String errPrefix = "§7[§cBackpacks§7] §c";

    @Override
    public void onEnable() {
        bAPI = new BackpacksAPI(this);
        saveDefaultConfig();
        config = bAPI.loadConfig();
        getLogger().info("Config loaded.");
        bAPI.createBackpackRecipe();
        getLogger().info("Backpack recipe registered.");
        getServer().getPluginManager().registerEvents(new PlayerRightClick(), this);
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new BackpackPlace(), this);
        getServer().getPluginManager().registerEvents(new BackpackCraft(), this);
        getCommand("backpacks").setExecutor(new ParentCommand());
        getLogger().info("Listeners and commands registered.");
    }
    @Override
    public void onDisable() {
        getLogger().info("Disabled Backpacks v" + getDescription().getVersion());
    }
}
