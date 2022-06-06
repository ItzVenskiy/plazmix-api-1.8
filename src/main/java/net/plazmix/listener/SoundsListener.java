package net.plazmix.listener;

import lombok.Getter;
import lombok.Setter;
import net.plazmix.PlazmixApi;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public final class SoundsListener implements Listener {

    @Setter
    @Getter
    private static boolean enable = true;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isEnable() || event.getClick() == null || event.getCurrentItem() == null || event.getClickedInventory() == null) {
            return;
        }

        if (PlazmixApi.INVENTORY_MANAGER.getOpenInventory(event.getWhoClicked().getName()) != null) {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.NOTE_PIANO, 1, 2);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!isEnable()) {
            return;
        }

        Player player = (Player) event.getPlayer();
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (!isEnable()) {
            return;
        }

        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.CLICK, 0.3f, 2);
    }

}
