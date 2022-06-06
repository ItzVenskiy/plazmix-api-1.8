package net.plazmix.listener;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.NonNull;
import net.plazmix.PlazmixApiPlugin;
import net.plazmix.coreconnector.module.type.coloredprefix.PlayerPrefixColorChangeEvent;
import net.plazmix.coreconnector.module.type.coloredprefix.PlayerPrefixColorResetEvent;
import net.plazmix.coreconnector.utility.StringUtils;
import net.plazmix.protocollib.team.ProtocolTeam;
import net.plazmix.utility.leveling.LevelSqlHandler;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;

public final class PlayerListener implements Listener {

    private ProtocolTeam getPlayerTeam(@NonNull Player player) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);
        String teamName = ((plazmixUser.getGroup() != null ? plazmixUser.getGroup().getTagPriority() : "") + plazmixUser.getName());

        ProtocolTeam protocolTeam = ProtocolTeam.get(StringUtils.fixLength(16, teamName));
        protocolTeam.setPrefix(plazmixUser.getPrefix());

        if (!protocolTeam.hasAutoReceived()) {
            protocolTeam.addAutoReceived();
        }

        return protocolTeam;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPrefixColorReset(PlayerPrefixColorResetEvent event) {
        String playerName = event.getPlayerName();
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            getPlayerTeam(player).broadcast();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPrefixColorChange(PlayerPrefixColorChangeEvent event) {
        String playerName = event.getPlayerName();
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            getPlayerTeam(player).broadcast();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // entity collision
        player.spigot().setCollidesWithEntities(false);

        // Add team
        ProtocolTeam protocolTeam = getPlayerTeam(player);
        protocolTeam.addPlayerEntry(player);
        protocolTeam.broadcastToServer();

        // Так надо :(
        event.setJoinMessage(null);

        // фикс обновления игрока данных на новых версиях
        Bukkit.getScheduler().runTaskLater(PlazmixApiPlugin.getPlugin(PlazmixApiPlugin.class), () -> {

            try {
                MinecraftReflection.getCraftPlayerClass().getMethod("updateScaledHealth")
                        .invoke(player);
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }, 2);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlazmixUser.of(player).getDatabasesValuesCacheTable().clear();

        // Player level injection
        LevelSqlHandler.INSTANCE.playerExperienceMap.remove(PlazmixUser.of(player).getPlayerId());

        // Remove team
        for (ProtocolTeam protocolTeam : ProtocolTeam.findEntryList(player)) {
            protocolTeam.removePlayerEntry(player);
        }

        // Так надо :(
        event.setQuitMessage(null);
    }

}
