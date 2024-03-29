package net.plazmix;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.SneakyThrows;
import net.plazmix.actionitem.ActionItemListener;
import net.plazmix.addon.PluginAddonListener;
import net.plazmix.command.impl.*;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.core.language.LanguageType;
import net.plazmix.game.utility.hotbar.GameHotbarListener;
import net.plazmix.holographic.manager.ProtocolHolographicManager;
import net.plazmix.inventory.listener.SimpleInventoryListener;
import net.plazmix.inventory.manager.BukkitInventoryManager;
import net.plazmix.listener.LevelListener;
import net.plazmix.listener.PlayerListener;
import net.plazmix.listener.RegionEnterExitListener;
import net.plazmix.listener.SoundsListener;
import net.plazmix.lobby.npc.listener.ServerNPCListener;
import net.plazmix.protocollib.entity.listener.FakeEntityListener;
import net.plazmix.protocollib.team.ProtocolTeam;
import net.plazmix.pvp.PvpAttributesListener;
import net.plazmix.pvp.knockback.Knockback;
import net.plazmix.pvp.knockback.KnockbackListener;
import net.plazmix.schematic.command.SchematicCommand;
import net.plazmix.scoreboard.listener.BaseScoreboardListener;
import net.plazmix.skin.command.SkinHistoryCommand;
import net.plazmix.skin.command.SkinSetCommand;
import net.plazmix.spacepass.SpacePassRewardsRegistry;
import net.plazmix.spacepass.SpacePassSqlHandler;
import net.plazmix.utility.custom.listener.CustomMobListener;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class PlazmixApiPlugin extends JavaPlugin {

    @SneakyThrows
    @Override
    public void onEnable() {

        // Messaging.
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // ProtocolLib.
        registerFakeEntityClicker();

        // Load languages.
        for (LanguageType languageType : LanguageType.VALUES) {
            languageType.getResource().initResources();

            System.out.println("[LanguageManager] :: Type " + languageType + " was success loaded!");
        }

        // Events.
        registerListeners();

        // Commands.
        registerCommands();

        // Inventories.
        BukkitInventoryManager.INSTANCE.startInventoryUpdaters(this);

        // Holographics.
        ProtocolHolographicManager.INSTANCE.runLocalizedLinesTaskUpdate(this);

        // MySQL.
        CoreConnector.getInstance().getMysqlConnection().createTable(true, "PlayerGamesData", "`Id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `Game` TEXT NOT NULL, `StartDate` TIMESTAMP, `EndDate` TIMESTAMP, `Map` TEXT NOT NULL, `Server` TEXT NOT NULL, `Players` TEXT NOT NULL, `Winner` VARCHAR(16) NOT NULL");
        CoreConnector.getInstance().getMysqlConnection().createTable(true, "PlayerPass", "`Id` INT NOT NULL, `Date` TIMESTAMP, `Experience` INT NOT NULL, `Activation` BOOLEAN NOT NULL");
        CoreConnector.getInstance().getMysqlConnection().createTable(true, "PlayerSkins", "`Id` INT NOT NULL, `Skin` VARCHAR(16) NOT NULL, `Date` TIMESTAMP");
        CoreConnector.getInstance().getMysqlConnection().createTable(true, "PlayerLeveling", "`Id` INT NOT NULL PRIMARY KEY, `Experience` INT NOT NULL");

        // Load all worlds.
        loadBukkitWorlds();

        // Register SpacePass.
        registerSpacePass();

        // Change game rules.
        for (World world : getServer().getWorlds()) {
            world.setGameRuleValue("announceAdvancements", "false");
        }
    }


    /**
     * Регистрация листенера для фейковых Entity
     * (иначе говоря, пакетных, созданных на основе ProtocolLib)
     */
    private void registerFakeEntityClicker() {
        FakeEntityListener fakeEntityListener = new FakeEntityListener();

        ProtocolLibrary.getProtocolManager().addPacketListener(fakeEntityListener);
        getServer().getPluginManager().registerEvents(fakeEntityListener, this);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(ProtocolTeam.TEAM_LISTENER, this);

        getServer().getPluginManager().registerEvents(new ActionItemListener(), this);
        getServer().getPluginManager().registerEvents(new PluginAddonListener(this), this);

        getServer().getPluginManager().registerEvents(new ServerNPCListener(), this);
        getServer().getPluginManager().registerEvents(new SimpleInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new BaseScoreboardListener(), this);

        getServer().getPluginManager().registerEvents(new GameHotbarListener(), this);

        getServer().getPluginManager().registerEvents(new LevelListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new SoundsListener(), this);

        //getServer().getPluginManager().registerEvents(new CustomBlockListener(), this);
        //getServer().getPluginManager().registerEvents(new CustomItemListener(), this);
        getServer().getPluginManager().registerEvents(new CustomMobListener(), this);
        //getServer().getPluginManager().registerEvents(new CustomRecipeListener(), this);

        getServer().getPluginManager().registerEvents(new RegionEnterExitListener(), this);

        // getServer().getPluginManager().registerEvents(new PlayerSkinListener(), this);
        // getServer().getPluginManager().registerEvents(new PvpAttributesListener(), this); // TODO - Убрал для тестов, посмотрим че будет дальше..
        // getServer().getPluginManager().registerEvents(new KnockbackListener(this, new Knockback()), this);
    }

    private void registerCommands() {
        PlazmixApi.registerCommand(new CrashCommand());
        PlazmixApi.registerCommand(new SchematicCommand());
        PlazmixApi.registerCommand(new PluginManageCommand());
        PlazmixApi.registerCommand(new ApiCommand());

        PlazmixApi.registerCommand(new GivePassCommand());
        PlazmixApi.registerCommand(new SpacePassCommand());
        PlazmixApi.registerCommand(new FireworkCommand());
        PlazmixApi.registerCommand(new StatsCommand());

        PlazmixApi.registerCommand(new ExperienceCommand());
        PlazmixApi.registerCommand(new LevelConvertCommand());

        PlazmixApi.registerCommand(new SkinSetCommand());
        PlazmixApi.registerCommand(new SkinHistoryCommand());

        //TynixCloudApi.registerCommand(new TestCommand());
    }

    private void loadBukkitWorlds() {
        for (File worldDirectory : Objects.requireNonNull(getServer().getWorldContainer().listFiles())) {
            if (!worldDirectory.isDirectory())
                continue;

            String worldName = worldDirectory.getName();
            switch (worldName) {

                // Add SlimeWorld-Manager services.
                case "slime_worlds":

                // Add default CloudNet folders.
                case ".wrapper":
                case "config":

                // Add SkyBlock worlds.
                case "IslandWorld":

                // Add default server folders.
                case "plugins":
                case "cache":
                case "logs": {
                    continue;
                }

                // Load other folders as worlds.
                default: {

                    if (getServer().getWorld(worldName) != null) {
                        continue;
                    }

                    World loadedWorld = new WorldCreator(worldName).createWorld();
                    getServer().getWorlds().add(loadedWorld);
                }
            }
        }
    }

    private void registerSpacePass() {
        SpacePassSqlHandler.INSTANCE.cleanDatabase();

        /* SEASON 1: Бонусные награды */
        {
            // 1 Stage
            SpacePassRewardsRegistry.registerReward(true, 0, "§e300 монет", new MaterialData(Material.APPLE), player -> PlazmixUser.of(player).addCoins(300));
            SpacePassRewardsRegistry.registerReward(true, 0, "§e950 монет", new MaterialData(Material.APPLE), player -> PlazmixUser.of(player).addCoins(950));
            SpacePassRewardsRegistry.registerReward(true, 0, "§e1200 монет", new MaterialData(Material.APPLE), player -> PlazmixUser.of(player).addCoins(1200));

            // 2 Stage
            SpacePassRewardsRegistry.registerReward(true, 0, "§e2650 монет", new MaterialData(Material.APPLE), player -> PlazmixUser.of(player).addCoins(2650));
            SpacePassRewardsRegistry.registerReward(true, 0, "§e3400 монет", new MaterialData(Material.APPLE), player -> PlazmixUser.of(player).addCoins(3400));
            SpacePassRewardsRegistry.registerReward(true, 0, "§e4500 монет", new MaterialData(Material.APPLE), player -> PlazmixUser.of(player).addCoins(4500));
        }

        /* SEASON 1: Основные награды за опыт */ {

            // 1 Stage
            SpacePassRewardsRegistry.registerReward(false, 1350, "§610 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(10));
            SpacePassRewardsRegistry.registerReward(false, 1550, "§615 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(15));
            SpacePassRewardsRegistry.registerReward(false, 1800, "§620 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(20));
            SpacePassRewardsRegistry.registerReward(false, 2300, "§625 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(25));
            SpacePassRewardsRegistry.registerReward(false, 3400, "§630 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(30));
            SpacePassRewardsRegistry.registerReward(false, 4600, "§640 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(40));
            SpacePassRewardsRegistry.registerReward(false, 5300, "§655 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(55));

            // 2 Stage
            SpacePassRewardsRegistry.registerReward(false, 1350, "§610 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(10));
            SpacePassRewardsRegistry.registerReward(false, 1550, "§615 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(15));
            SpacePassRewardsRegistry.registerReward(false, 1800, "§620 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(20));
            SpacePassRewardsRegistry.registerReward(false, 2300, "§625 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(25));
            SpacePassRewardsRegistry.registerReward(false, 3400, "§630 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(30));
            SpacePassRewardsRegistry.registerReward(false, 4600, "§640 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(40));
            SpacePassRewardsRegistry.registerReward(false, 5300, "§655 золота", new MaterialData(Material.GOLD_INGOT), player -> PlazmixUser.of(player).addGolds(55));
        }
    }

}
