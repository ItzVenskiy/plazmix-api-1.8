package net.plazmix.pvp.knockback;

import java.util.concurrent.*;
import com.comphenix.protocol.*;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.protocollib.packet.entity.WrapperPlayServerEntityVelocity;
import com.comphenix.protocol.events.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.event.entity.*;
import org.bukkit.enchantments.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public final class KnockbackListener implements Listener {

    private final Knockback knockback;
    private final Map<UUID, KnockbackHit> damaged;
    
    public KnockbackListener(PlazmixApiPlugin plugin, Knockback knockback) {
        knockback.setDefaults();

        this.knockback = knockback;
        this.damaged = new ConcurrentHashMap<>();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_VELOCITY) {

            @Override
            public void onPacketSending(final PacketEvent event) {
                velocityHandler(event);
            }
        });

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long currTime = System.currentTimeMillis();

            for (Player p : Bukkit.getOnlinePlayers()) {
                KnockbackHit hit = KnockbackListener.this.damaged.get(p.getUniqueId());

                if (hit == null) {
                    continue;
                }

                if (currTime - hit.getTime() <= 1000L) {
                    continue;
                }

                KnockbackListener.this.damaged.remove(p.getUniqueId());
            }
        }, 1L, 1L);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
            return;
        }
        Player attacker = (Player)e.getDamager();
        Player victim = (Player)e.getEntity();

        Vector direction = new Vector(victim.getLocation().getX() - attacker.getLocation().getX(), 0.0, victim.getLocation().getZ() - attacker.getLocation().getZ());

        int kbEnchantLevel = attacker.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK);

        KnockbackHit hit = new KnockbackHit(attacker, direction.normalize(), attacker.isSprinting(), kbEnchantLevel, System.currentTimeMillis());

        this.damaged.put(victim.getUniqueId(), hit);
    }
    
    private void velocityHandler(PacketEvent event) {
        WrapperPlayServerEntityVelocity velPacketWrapped = new WrapperPlayServerEntityVelocity(event.getPacket());
        Player player = null;

        int idFromPacket = velPacketWrapped.getEntityID();

        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.getEntityId() == idFromPacket) {
                player = p;
                break;
            }
        }
        
        if (player == null) {
            return;
        }
        
        if (!this.damaged.containsKey(player.getUniqueId())) {
            return;
        }
        
        KnockbackHit hit = this.damaged.get(player.getUniqueId());
        this.damaged.remove(player.getUniqueId());
        
        if (Knockback.getInstance().isKnockbackEnabled() && hit != null) {

            Vector initKb = hit.getDirection().setY(1);
            Vector attackerYaw = hit.getAttacker().getLocation().getDirection().normalize().setY(1);

            Vector resultKb;

            if (knockback.isSprintMultiplierEnabled() && hit.isSprintKb()) {
                resultKb = initKb.clone().multiply(1.0 - knockback.getSprintYawFactor()).add(attackerYaw.clone().multiply(knockback.getSprintYawFactor()));

                resultKb.setX(resultKb.getX() * knockback.getSprintMultiplierHoriz());
                resultKb.setY(resultKb.getY() * knockback.getSprintMultiplierVert());
                resultKb.setZ(resultKb.getZ() * knockback.getSprintMultiplierHoriz());
            }
            else {
                resultKb = initKb;
            }

            double horizMultiplier = ((Entity)player).isOnGround() ? knockback.getGroundHorizMultiplier() : knockback.getAirHorizMultiplier();
            double vertMultiplier = ((Entity)player).isOnGround() ? knockback.getGroundVertMultiplier() : knockback.getAirVertMultiplier();

            resultKb = new Vector(resultKb.getX() * horizMultiplier, resultKb.getY() * vertMultiplier, resultKb.getZ() * horizMultiplier);

            if (hit.getKbEnchantLevel() < 0) {
                double KBEnchAdder = hit.getKbEnchantLevel() * 0.45;
                double distance = Math.sqrt(Math.pow(resultKb.getX(), 2.0) + Math.pow(resultKb.getZ(), 2.0));

                double ratioX = resultKb.getX() / distance;
                ratioX = ratioX * KBEnchAdder + resultKb.getX();

                double ratioZ = resultKb.getZ() / distance;
                ratioZ = ratioZ * KBEnchAdder + resultKb.getZ();

                resultKb = new Vector(ratioX, resultKb.getY(), ratioZ);
            }

            velPacketWrapped.setVelocityX(resultKb.getX());
            velPacketWrapped.setVelocityY(resultKb.getY());
            velPacketWrapped.setVelocityZ(resultKb.getZ());
        }
    }
    
    @EventHandler
    public void quitHandler(final PlayerQuitEvent event) {
        this.damaged.remove(event.getPlayer().getUniqueId());
    }
}