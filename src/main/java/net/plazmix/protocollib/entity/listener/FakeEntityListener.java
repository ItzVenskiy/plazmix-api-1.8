package net.plazmix.protocollib.entity.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.protocollib.entity.FakeEntity;
import net.plazmix.protocollib.entity.FakeEntityLiving;
import net.plazmix.protocollib.entity.FakeEntityRegistry;
import net.plazmix.utility.cooldown.PlayerCooldownUtil;
import net.plazmix.utility.location.region.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

public final class FakeEntityListener
        extends PacketAdapter implements Listener {

    private static final int SERVER_VIEW_DISTANCE = 8;
    private static final long ENTITY_INTERACT_COOLDOWN = 50;

    private final Multimap<Player, Chunk> playersLoadedChunks
            = HashMultimap.create();

    public FakeEntityListener() {
        super(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();

        if (PlayerCooldownUtil.hasCooldown("fake_entity_interact", player)) {
            return;
        }

        FakeEntity fakeEntity = FakeEntityRegistry.INSTANCE.getEntityById(event.getPacket().getIntegers().read(0));
        if (!(fakeEntity instanceof FakeEntityLiving)) {
            return;
        }

        EnumWrappers.EntityUseAction entityUseAction = event.getPacket().getEntityUseActions().read(0);
        switch (entityUseAction) {

            case ATTACK: {
                Consumer<Player> attackAction = fakeEntity.getAttackAction();

                if (attackAction != null) {
                    Bukkit.getScheduler().runTask(getPlugin(), () -> attackAction.accept(player));
                }

                break;
            }

            case INTERACT_AT: {
                Consumer<Player> clickAction = fakeEntity.getClickAction();

                if (clickAction != null) {
                    Bukkit.getScheduler().runTask(getPlugin(), () -> clickAction.accept(player));
                }

                break;
            }
        }

        PlayerCooldownUtil.putCooldown("fake_entity_interact", player, ENTITY_INTERACT_COOLDOWN);
    }

    private void onChunkLoad(Player player, int x, int z) {
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {

            for (FakeEntity entity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
                Chunk chunk = entity.getLocation().getChunk();

                if (chunk.getX() == x && chunk.getZ() == z) {
                    if (entity.hasViewer(player)) {
                        continue;
                    }

                    entity.addViewers(player);
                }
            }

        }, 10L);
    }

    private void onChunkUnload(Player player, int x, int z) {
        for (FakeEntity entity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
            Chunk chunk = entity.getLocation().getChunk();

            if (chunk.getX() == x && chunk.getZ() == z) {
                if (!entity.hasViewer(player)) {
                    continue;
                }

                entity.removeViewers(player);
            }
        }
    }

    private void makeEntitiesTracking(Player player) {
        CuboidRegion cuboid = CuboidRegion.cubeRadius(player.getLocation(), SERVER_VIEW_DISTANCE * 8);
        Set<Chunk> cuboidChunks = cuboid.getChunks();

        // Load new chunks.
        for (Chunk chunk : cuboidChunks) {
            if (playersLoadedChunks.containsEntry(player, chunk)) {
                continue;
            }

            onChunkLoad(player, chunk.getX(), chunk.getZ());
            playersLoadedChunks.put(player, chunk);
        }

        // Unload already loaded chunks.         // fix ConcurrentModificationException
        for (Chunk loadedChunk : new ArrayList<>(playersLoadedChunks.get(player))) {

            if (!cuboidChunks.contains(loadedChunk)) {

                onChunkUnload(player, loadedChunk.getX(), loadedChunk.getZ());
                playersLoadedChunks.remove(player, loadedChunk);
            }
        }
    }

    private void makeEntitiesTrackingWithCooldown(String cooldown, Player player) {

        if (!PlayerCooldownUtil.hasCooldown(cooldown, player)) {
            this.makeEntitiesTracking(player);

            // Add cooldown
            PlayerCooldownUtil.putCooldown(cooldown, player, 1000);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        this.makeEntitiesTrackingWithCooldown("fakeEntity_track_move", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerWorldChanged(PlayerChangedWorldEvent event) {
        this.makeEntitiesTrackingWithCooldown("fakeEntity_track_world", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.makeEntitiesTrackingWithCooldown("fakeEntity_track_teleport", event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {

        Chunk chunk = event.getPlayer().getLocation().getChunk();
        this.onChunkLoad(event.getPlayer(), chunk.getX(), chunk.getZ());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (FakeEntity fakeEntity : FakeEntityRegistry.INSTANCE.getReceivableEntities(player)) {
            if (fakeEntity == null) {
                continue;
            }

            fakeEntity.removeReceivers(player);
        }
    }

}