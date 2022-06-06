package net.plazmix.utility.custom.listener;

import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import net.plazmix.utility.custom.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class CustomItemListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        ItemStack itemStack = player.getInventory().getItemInHand();

        if (itemStack != null) {
            itemStack = itemStack.clone();
            itemStack.setAmount(1);

            CustomItem customItem = CustomItem.of(itemStack);

            if (customItem == null)                                                   return;
            if (PlayerCooldownUtil.hasCooldown("custom_click", player))  return;

            customItem.onInteract(player, action, event.hasBlock() ? event.getClickedBlock().getLocation() : null, event);

            PlayerCooldownUtil.putCooldown("custom_click", player, 100);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPickup(PlayerPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();

        if (itemStack != null) {
            itemStack = itemStack.clone();
            itemStack.setAmount(1);

            CustomItem customItem = CustomItem.of(itemStack);

            if (customItem == null) return;
            customItem.onPickup(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();

        if (itemStack != null) {
            itemStack = itemStack.clone();
            itemStack.setAmount(1);

            CustomItem customItem = CustomItem.of(itemStack);

            if (customItem == null) return;
            customItem.onDrop(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInHand();

        if (itemStack != null) {
            itemStack = itemStack.clone();
            itemStack.setAmount(1);

            CustomItem customItem = CustomItem.of(itemStack);

            if (customItem == null) return;
            customItem.onBlockBreak(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInHand();

        if (itemStack != null) {
            itemStack = itemStack.clone();
            itemStack.setAmount(1);

            CustomItem customItem = CustomItem.of(itemStack);

            if (customItem == null) return;
            customItem.onBlockPlace(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        ItemStack bowItem = event.getBow();

        if (bowItem != null) {
            CustomItem customItem = CustomItem.of(bowItem);

            if (customItem != null) {
                customItem.onBowShoot(event);
            }
        }
    }

}
