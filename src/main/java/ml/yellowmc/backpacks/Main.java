package ml.yellowmc.backpacks;

import ml.yellowmc.backpacks.api.BackpacksAPI;
import ml.yellowmc.backpacks.cmd.GiveBackpack;
import ml.yellowmc.backpacks.listener.BackpackListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    public static BackpacksAPI bAPI;
    public static FileConfiguration config;
    public static final String msgPrefix = ChatColor.YELLOW + "[Backpacks] " + ChatColor.WHITE;

    @Override
    public void onEnable() {
        bAPI = new BackpacksAPI(this);
        saveDefaultConfig();
        config = getConfig();
        getLogger().info("Config loaded.");
        bAPI.createBackpackRecipe();
        getLogger().info("Backpack recipe registered.");
        getServer().getPluginManager().registerEvents(new BackpackListener(), this);
        getCommand("givebackpack").setExecutor(new GiveBackpack());
        getLogger().info("Listeners and commands registered.");
    }
    @Override
    public void onDisable() {
        getLogger().info("Disabled Backpacks v1.0a");
    }
}
