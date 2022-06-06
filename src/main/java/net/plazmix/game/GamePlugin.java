package net.plazmix.game;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.md_5.bungee.api.ChatMessageType;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.plazmix.PlazmixAPI;
import net.plazmix.PlazmixApi;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.game.command.LeaveCommand;
import net.plazmix.game.command.StartCommand;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.listener.GameSettingsListener;
import net.plazmix.game.listener.GhostListener;
import net.plazmix.game.listener.PlayerLoaderListener;
import net.plazmix.game.state.GameState;
import net.plazmix.minecraft.game.util.paper.GameWorldGenerator;
import net.plazmix.network.module.GameMapModule;
import net.plazmix.utility.Directories;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class GamePlugin extends JavaPlugin {

    @Getter
    private static GamePlugin instance;


    GameCache cache             = new GameCache();
    GamePluginService service   = new GamePluginService();

    @NonFinal
    PlayerGamesData gamesData;


    public abstract GameInstallerTask getInstallerTask();

    protected abstract void handleEnable();
    protected abstract void handleDisable();

    @Override
    public void onDisable() {
        instance = null;

        // Disable all stages.
        GameState currentState = service.getStateManager().getCurrentState();

        if (currentState != null && currentState.isEnabled()) {
            currentState.forceShutdown();
        }

        for (GameState gameState : service.getStateManager().getGameStates()) {
            if (!gameState.isEnabled())
                continue;

            gameState.forceShutdown();
        }

        // Game plugin disable process.
        handleDisable();

        // Initialize PlayerGamesData.
        if (gamesData != null && gamesData.canInsert()) {

            gamesData.insert(CoreConnector.getInstance().getMysqlConnection());
            gamesData = null;
        }

        GameWorldGenerator.unloadWorld(getService().getMapWorld());
    }

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        List<String> worlds = getConfig().getStringList("worlds");
        int index = ThreadLocalRandom.current().nextInt(0, worlds.size());
        String[] data = worlds.get(index).split(":");
        getService().setWorldName(data[0]);
        getService().setMapName(data[1]);

        this.loadMap(getService().getWorldName());
        getServer().getScheduler().runTaskLater(this, () -> {
            getLogger().info("Enabling game plugin");
            // Game plugin enable process.
            handleEnable();

            PlazmixApi.registerCommand(new LeaveCommand());
            PlazmixApi.registerCommand(new StartCommand());

            // Register listeners.
            getServer().getPluginManager().registerEvents(new PlayerLoaderListener(this), this);
            getServer().getPluginManager().registerEvents(new GameSettingsListener(this), this);
            getServer().getPluginManager().registerEvents(new GhostListener(this), this);

            // Initialize PlayerGamesData.
            if (gamesData == null) {
                gamesData = PlayerGamesData.create(service.getGameName(), service.getMapName(), PlazmixCoreApi.getCurrentServerName());
            }

            // Activate of the stages
            service.getStateManager().nextStage();
        }, 15L);
    }

    @SneakyThrows
    protected void createBackupFolder() {
        Path backupDirectory = getDataFolder().toPath().resolve("backup");

        if (!Files.exists(backupDirectory)) {
            Files.createDirectories(backupDirectory);

            getLogger().info(ChatColor.GREEN + "[Game Backup] Backup folder has been created!");
            getLogger().info(ChatColor.GREEN + "[Game Backup] Please, load the arena world file to the backup folder");
        }
    }

    private void loadMap(String worldName) {
        GameWorldGenerator.generate(this, PlazmixAPI.getNetwork().getModule(GameMapModule.class).get().slime().getPropertyMap(worldName), worldName);
    }

    @SneakyThrows
    private void loadBackupMap(String arenaName) {
        Server server = super.getServer();
        World world = server.getWorld(arenaName);

        server.unloadWorld(world, false);

        Path backupDirectory = getDataFolder().toPath().resolve("backup");
        Path worldBackupPath = backupDirectory.resolve(arenaName);

        if (!Files.exists(worldBackupPath)) {
            getLogger().info(ChatColor.RED + "[Game Backup] Backup arena world folder is`nt exists");
            getLogger().info(ChatColor.RED + "[Game Backup] Copy backup process is cancelled!");

            return;
        }

        if (Objects.requireNonNull(worldBackupPath.toFile().list()).length != 0) {
            File worldFile = world.getWorldFolder();

            Directories.copyDirectory(worldBackupPath, worldFile.toPath());
            getLogger().info(ChatColor.GREEN + "[Game Backup] Backup of the world map '" + worldFile.getName() + "' has been loaded!");
        }
    }

    protected boolean hasSetupMode(@NonNull String path) {
        saveDefaultConfig();
        return getConfig().getBoolean(path);
    }


    public void broadcastMessage(@NonNull ChatMessageType chatMessageType, @NonNull String message) {
        for (Player player : getServer().getOnlinePlayers()) {

            switch (chatMessageType) {
                case SYSTEM:
                case CHAT: {
                    player.sendMessage(message);
                    break;
                }

                case ACTION_BAR: {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(message), (byte) 2));
                    break;
                }
            }
        }
    }

    public void broadcastMessage(@NonNull String message) {
        broadcastMessage(ChatMessageType.CHAT, message);
    }

    public void broadcastLangMessage(@NonNull ChatMessageType chatMessageType, @NonNull String langKey) {
        for (Player player : getServer().getOnlinePlayers()) {
            String text = PlazmixUser.of(player).localization().getLocalizationResource().getText(langKey);

            switch (chatMessageType) {

                case SYSTEM:
                case CHAT: {
                    player.sendMessage(text);
                    break;
                }

                case ACTION_BAR: {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(text), (byte) 2));
                    break;
                }
            }
        }
    }

    public void broadcastLangMessage(@NonNull String langKey) {
        broadcastLangMessage(ChatMessageType.CHAT, langKey);
    }

}
