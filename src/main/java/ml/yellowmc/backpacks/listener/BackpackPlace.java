package ml.yellowmc.backpacks.listener;

import ml.yellowmc.backpacks.api.BackpacksAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BackpackPlace implements Listener {
    @EventHandler
    public void onBackpackPlace(BlockPlaceEvent event) {
        if (BackpacksAPI.compareItems(event.getItemInHand(), BackpacksAPI.getBackpack())) {
            event.setCancelled(true);
        }
    }
}
