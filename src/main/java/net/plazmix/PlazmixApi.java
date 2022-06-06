package net.plazmix;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.NonNull;
import net.plazmix.command.BaseCommand;
import net.plazmix.command.manager.CommandManager;
import net.plazmix.coreconnector.CoreConnector;
import net.plazmix.coreconnector.utility.server.ServerMode;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.impl.SimpleHolographic;
import net.plazmix.holographic.manager.ProtocolHolographicManager;
import net.plazmix.inventory.BaseInventory;
import net.plazmix.inventory.impl.BasePaginatedInventory;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.inventory.manager.BukkitInventoryManager;
import net.plazmix.scoreboard.BaseScoreboard;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.location.region.CuboidRegion;
import net.plazmix.utility.location.region.SpheroidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;

public interface PlazmixApi {

    CommandManager COMMAND_MANAGER                  = (CommandManager.INSTANCE);
    ProtocolHolographicManager HOLOGRAPHIC_MANAGER  = (ProtocolHolographicManager.INSTANCE);
    BukkitInventoryManager INVENTORY_MANAGER        = (BukkitInventoryManager.INSTANCE);


    static int getMinecraftVersionId() {
        return MinecraftProtocolVersion.getCurrentVersion();
    }

    static int getPlayerVersion(@NonNull Player player) {
        return ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
    }

    static ServerMode getCurrentServerMode() {
        return ServerMode.getMode(CoreConnector.getInstance().getServerName());
    }

    static void setProfileTextures(@NonNull Player player,
                                   @NonNull String value, @NonNull String signature) {

        GameProfile gameProfile = ((CraftPlayer) player).getProfile();

        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
    }

    static void setProfileTextures(@NonNull Player player, @NonNull String texture) {
        GameProfile gameProfile = ((CraftPlayer) player).getProfile();

        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", texture));
    }

    /**
     * Создать обыкновенную голограмму
     *
     * @param location - начальная локация голограммы
     */
    static ProtocolHolographic createSimpleHolographic(@NonNull Location location) {
        return new SimpleHolographic(location);
    }

    /**
     * Создать кубоид блоков из двух по
     * двум начальным локациям
     *
     * @param start - начальная локация №1
     * @param end - начальная локация №2
     */
    static CuboidRegion createCuboid(@NonNull Location start, @NonNull Location end) {
        return new CuboidRegion(start, end);
    }

    /**
     * Создать cathe блоков из двух по
     * двум начальным локациям
     *
     * @param start - начальная локация №1
     * @param end - начальная локация №2
     */
    static SpheroidRegion createSpheroid(@NonNull Location start, @NonNull Location end) {
        return new SpheroidRegion(start, end);
    }


    /**
     * Создание {@link ItemStack} по Builder паттерну
     *
     * @param material - начальный тип предмета
     */
    static ItemUtil.ItemBuilder newItemBuilder(@NonNull Material material) {
        return ItemUtil.newBuilder(material);
    }

    /**
     * Создание {@link ItemStack} по Builder паттерну
     *
     * @param materialData - начальная дата предмета
     */
    static ItemUtil.ItemBuilder newItemBuilder(@NonNull MaterialData materialData) {
        return ItemUtil.newBuilder(materialData);
    }

    /**
     * Создание {@link ItemStack} по Builder паттерну
     *
     * @param itemStack - готовый {@link ItemStack} на клонирование и переработку
     */
    static ItemUtil.ItemBuilder newItemBuilder(@NonNull ItemStack itemStack) {
        return ItemUtil.newBuilder(itemStack);
    }


    /**
     * Создать обыкновенный инвентарь без абстракции
     *
     * @param inventoryRows     - количество строк со слотами
     * @param inventoryTitle    - название инвентаря
     * @param inventoryConsumer - обработчик отрисовки предметов
     */
    static BaseInventory createSimpleInventory(int inventoryRows, @NonNull String inventoryTitle,
                                               @NonNull BiConsumer<Player, BaseInventory> inventoryConsumer) {

        return new BaseSimpleInventory(inventoryTitle, inventoryRows) {

            @Override
            public void drawInventory(Player player) {
                inventoryConsumer.accept(player, this);
            }
        };
    }

    /**
     * Создать страничный инвентарь без абстракции
     *
     * @param inventoryRows     - количество строк со слотами
     * @param inventoryTitle    - название инвентаря
     * @param inventoryConsumer - обработчик отрисовки предметов
     */
    static BasePaginatedInventory createPaginatedInventory(int inventoryRows, @NonNull String inventoryTitle,
                                                           @NonNull BiConsumer<Player, BasePaginatedInventory> inventoryConsumer) {

        return new BasePaginatedInventory(inventoryTitle, inventoryRows) {

            @Override
            public void drawInventory(Player player) {
                inventoryConsumer.accept(player, this);
            }
        };
    }


    /**
     * Зарегистрировать наследник {@link BaseCommand}
     * как bukkit команду на {@link PlazmixApiPlugin}
     *
     * @param baseCommand - команда
     */
    static void registerCommand(@NonNull BaseCommand<?> baseCommand) {
        COMMAND_MANAGER.registerCommand(baseCommand, baseCommand.getName(), baseCommand.getAliases().toArray(new String[0]));
    }

    /**
     * Зарегистрировать наследник {@link BaseCommand}
     * как bukkit команду на {@link PlazmixApiPlugin}
     *
     * @param baseCommand    - команда
     * @param commandName    - основная команда
     * @param commandAliases - дополнительные команды, обрабатывающие тот же класс (алиасы)
     */
    static void registerCommand(@NonNull BaseCommand<?> baseCommand, @NonNull String commandName, @NonNull String... commandAliases) {
        COMMAND_MANAGER.registerCommand(baseCommand, commandName, commandAliases);
    }

    /**
     * Зарегистрировать наследник {@link BaseCommand}
     * как bukkit команду
     *
     * @param plugin      - плагин, на который регистрировать команду
     * @param baseCommand - команда
     */
    static void registerCommand(@NonNull Plugin plugin, @NonNull BaseCommand<?> baseCommand) {
        COMMAND_MANAGER.registerCommand(plugin, baseCommand, baseCommand.getName(), baseCommand.getAliases().toArray(new String[0]));
    }

    /**
     * Зарегистрировать наследник {@link BaseCommand}
     * как bukkit команду
     *
     * @param plugin         - плагин, на который регистрировать команду
     * @param baseCommand    - команда
     * @param commandName    - основная команда
     * @param commandAliases - дополнительные команды, обрабатывающие тот же класс (алиасы)
     */
    static void registerCommand(@NonNull Plugin plugin, @NonNull BaseCommand<?> baseCommand, @NonNull String commandName, @NonNull String... commandAliases) {
        COMMAND_MANAGER.registerCommand(plugin, baseCommand, commandName, commandAliases);
    }


    /**
     * Создание {@link BaseScoreboard} по Builder паттерну
     * с рандомным названием objective
     */
    static BaseScoreboardBuilder newScoreboardBuilder() {
        return BaseScoreboardBuilder.newScoreboardBuilder();
    }

    /**
     * Создание {@link BaseScoreboard} по Builder паттерну
     *
     * @param objectiveName - название scoreboard objective
     */
    static BaseScoreboardBuilder newScoreboardBuilder(@NonNull String objectiveName) {
        return BaseScoreboardBuilder.newScoreboardBuilder(objectiveName);
    }

}
