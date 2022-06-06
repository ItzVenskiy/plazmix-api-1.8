package net.plazmix.pvp;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class PvpAttributesListener implements Listener {

// ----------------------------- DAMAGE COOLDOWN ----------------------------------------------------------------------------------------------------------------------

    private final TIntLongMap entityCooldownsMap = new TIntLongHashMap();

    private boolean hasDamageCooldown(Entity entity) {
        int entityId = entity.getEntityId();

        return entityCooldownsMap.containsKey(entityId) && entityCooldownsMap.get(entityId) - System.currentTimeMillis() > 0;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageCooldownTarget(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        boolean canCooldownUse = PvpSettings.COOLDOWN_ALLOW.getValue();
        long takeDamageCooldown = PvpSettings.TAKE_TARGET_COOLDOWN.getValue();

        if (canCooldownUse && takeDamageCooldown > 0) {

            if (hasDamageCooldown(damager)) {
                event.setCancelled(true);

            } else {

                entityCooldownsMap.put(damager.getEntityId(), System.currentTimeMillis() + takeDamageCooldown);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageCooldownSelf(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        boolean canCooldownUse = PvpSettings.COOLDOWN_ALLOW.getValue();
        long takeDamageCooldown = PvpSettings.TAKE_SELF_COOLDOWN.getValue();

        if (canCooldownUse && takeDamageCooldown > 0) {

            if (hasDamageCooldown(entity)) {
                event.setCancelled(true);

            } else {

                entityCooldownsMap.put(entity.getEntityId(), System.currentTimeMillis() + takeDamageCooldown);
            }
        }
    }

// ----------------------------- DAMAGE VELOCITY ----------------------------------------------------------------------------------------------------------------------

    private void handleDamageVelocityEvent(EntityDamageByEntityEvent event, BiConsumer<LivingEntity, Boolean> isEmpty) {
        Entity damager = event.getDamager();

        if (!(damager instanceof LivingEntity)) {
            return;
        }

        LivingEntity livingDamager = ((LivingEntity) damager);
        EntityEquipment equipment = livingDamager.getEquipment();

        isEmpty.accept(livingDamager, equipment.getItemInHand() == null || equipment.getItemInHand().getType().getId() == 0);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageVelocityByEmptyHand(EntityDamageByEntityEvent event) {
        handleDamageVelocityEvent(event, (damager, handStatus) -> {
            if (handStatus) {
                return;
            }

            Vector velocity = PvpSettings.EMPTY_HAND_VELOCITY.getValue();
            if (velocity == null) {
                return;
            }

            event.getEntity().setVelocity(velocity);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageVelocityByItemHand(EntityDamageByEntityEvent event) {
        handleDamageVelocityEvent(event, (damager, handStatus) -> {
            if (!handStatus) {
                return;
            }

            Vector velocity = PvpSettings.ITEM_HAND_VELOCITY.getValue(damager.getEquipment().getItemInHand());
            if (velocity == null) {
                return;
            }

            event.getEntity().setVelocity(velocity);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageVelocityByEntity(EntityDamageByEntityEvent event) {
        Vector velocity = PvpSettings.ENTITY_TARGET_VELOCITY.getValue(
                event.getDamager().getType()
        );

        if (velocity != null) {
            event.getEntity().setVelocity(velocity);
        }
    }

}
