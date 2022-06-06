package net.plazmix.actionitem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class ActionItemListener implements Listener {

    private final Map<Player, ItemStack> playerProjectileLaunchItemMap = new HashMap<>();
    private final ListMultimap<Player, ItemStack> playerProjectileHitItemMap = ArrayListMultimap.create();

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        handleItem(event.getPlayer(), event.getItemDrop().getItemStack(), (player, actionItem) -> {

            if (actionItem.getDropHandler() != null) {
                actionItem.getDropHandler().handleEvent(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPickup(PlayerPickupItemEvent event) {
        handleItem(event.getPlayer(), event.getItem().getItemStack(), (player, actionItem) -> {

            if (actionItem.getPickupHandler() != null) {
                actionItem.getPickupHandler().handleEvent(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        handleMainHand((Player) event.getDamager(), (player, actionItem) -> {

            if (actionItem.getAttackHandler() != null) {
                actionItem.getAttackHandler().handleEvent(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        handleItem(event.getPlayer(), event.getItem(), (player, actionItem) -> {

            if (actionItem.getInteractHandler() != null) {
                actionItem.getInteractHandler().handleEvent(event);
                if (actionItem.getProjectileLaunchHandler() != null) {
                    playerProjectileLaunchItemMap.put(event.getPlayer(), event.getItem());
                }
                if (actionItem.getProjectileHitHandler() != null) {
                    playerProjectileHitItemMap.put(event.getPlayer(), event.getItem());
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getPlaceHandler() != null) {
                actionItem.getPlaceHandler().handleEvent(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getBreakHandler() != null) {
                actionItem.getBreakHandler().handleEvent(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldChanged(PlayerChangedWorldEvent event) {
        handleMainHand(event.getPlayer(), (player, actionItem) -> {

            if (actionItem.getWorldChangedHandler() != null) {
                actionItem.getWorldChangedHandler().handleEvent(event);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player shooter = (Player) event.getEntity();
        handleItem(shooter, event.getBow(), (player, actionItem) -> {

            if (actionItem.getShootBowHandler() != null) {
                actionItem.getShootBowHandler().handleEvent(event);
                if (actionItem.getProjectileLaunchHandler() != null) {
                    playerProjectileLaunchItemMap.put(shooter, event.getBow());
                }
                if (actionItem.getProjectileHitHandler() != null) {
                    playerProjectileHitItemMap.put(shooter, event.getBow());
                }
            }
        });
    }

    @EventHandler
    public void on(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player shooter = (Player) event.getEntity().getShooter();
        if (!playerProjectileLaunchItemMap.containsKey(shooter))
            return;

        handleItem(shooter, playerProjectileLaunchItemMap.get(shooter), (player, actionItem) -> {
            if (actionItem.getProjectileLaunchHandler() != null) {
                actionItem.getProjectileLaunchHandler().handleEvent(event);
            }
        });
    }

    @EventHandler
    public void on(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        Player shooter = (Player) event.getEntity().getShooter();
        if (!playerProjectileHitItemMap.containsKey(shooter))
            return;

        List<ItemStack> itemStacks = playerProjectileHitItemMap.get(shooter);
        if (itemStacks.isEmpty())
            return;

        handleItem(shooter, itemStacks.get(0), (player, actionItem) -> {
            if (actionItem.getProjectileHitHandler() != null) {
                actionItem.getProjectileHitHandler().handleEvent(event);
            }
        });

        itemStacks.remove(0);
    }

    private void handleItem(@NonNull Player player, @NonNull ItemStack itemStack,
                            @NonNull BiConsumer<Player, ActionItem> itemConsumer) {

        if (ActionItem.hasActionItem(itemStack)) {
            itemConsumer.accept(player, ActionItem.fromItem(itemStack));
        }
    }

    private void handleMainHand(@NonNull Player player, @NonNull BiConsumer<Player, ActionItem> itemConsumer) {
        ItemStack mainHandItem = player.getInventory().getItemInHand();

        if (mainHandItem == null) {
            return;
        }

        if (ActionItem.hasActionItem(mainHandItem)) {
            itemConsumer.accept(player, ActionItem.fromItem(mainHandItem));
        }
    }

}
