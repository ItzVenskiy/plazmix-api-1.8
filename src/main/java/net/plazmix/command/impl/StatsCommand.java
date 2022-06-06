package net.plazmix.command.impl;

import net.plazmix.command.BaseCommand;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StatsCommand extends BaseCommand<Player> {

    public StatsCommand() {
        super("stats", "статистика", "стата");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        new StatsMenu().openInventory(player);
    }

    public static class StatsMenu
            extends BaseSimpleInventory {

        public StatsMenu() {
            super("Игровая статистика", 5);
        }

        @Override
        public void drawInventory(Player player) {
            PlazmixUser plazmixUser = PlazmixUser.of(player);

            setOriginalItem(5, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull(plazmixUser.getName())

                    .setName("§bОбщая статистика §8§k§l|")

                    .addLore("")
                    .addLore("§8▪ §fСтатус: " + plazmixUser.getGroup().getColouredName())
                    .addLore("§8▪ §fSpacePass: " + (plazmixUser.getPass().isActivated() ? "§d§lПриобретен" : "§c§lНе приобретён"))
                    .addLore("")
                    .addLore("§8▪ §fМонет: §a" + NumberUtil.spaced(plazmixUser.getCoins()))
                    .addLore("§8▪ §fЗолота: §6" + NumberUtil.spaced(plazmixUser.getGolds()))
                    .addLore("§8▪ §fУровень: §d" + NumberUtil.spaced(plazmixUser.getLevel()))
                    .addLore("")
                    .addLore("§8▪ §fЯзык: §7" + plazmixUser.getLanguage().getDisplayName())
                    .build());

            setOriginalItem(21, ItemUtil.newBuilder(Material.FISHING_ROD)
                    .setName("§eDuels §8§k§l|")

                    .addLore("")
                    .addLore("§eStickFight:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SFDuels", "wins")))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SFDuels", "kills")))
                    .addLore("")
                    .addLore("§eSumo:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SumoDuels", "wins")))
                    .build());

            setOriginalItem(25, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setPlayerSkull("haohanklliu")

                    .setName("§aOneBlock §8§k§l|")
                    .addLore("")
                    .addLore("§8▪ §fУровень острова: §70")
                    .addLore("§8▪ §fСломано блоков: §70")
                    .addLore("")
                    .build());

            setOriginalItem(24, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ0MDhjNTY5OGYyZDdhOGExNDE1ZWY5NTkyYWViNGJmNjJjOWFlN2NjZjE4ODQ5NzUzMGJmM2M4Yjk2NDhlNSJ9fX0=")

                    .setName("§2ArcadeGames §8§k§l|")

                    .addLore("")
                    .addLore("§2Build Battle:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("")
                    .addLore("§2Speed Builders:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SpeedBuilders", "wins")))
                    .addLore("§8▪ §fЛучшее время: §7" + NumberUtil.spaced(plazmixUser.getDatabaseValue("SpeedBuilders", "bestTime")))
                    .addLore("")
                    .addLore("§2Squid Game:")
                    .addLore("§8▪ §fПобед: §70")
                    .build());

            setOriginalItem(23, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZiMjkwYTEzZGY4ODI2N2VhNWY1ZmNmNzk2YjYxNTdmZjY0Y2NlZTVjZDM5ZDQ2OTcyNDU5MWJhYmVlZDFmNiJ9fX0=")

                    .setName("§cBedWars §8§k§l|")

                    .addLore("")
                    .addLore("§cBedWars - §nОбщая статистика§c:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("§8▪ §fУбийств: §70")
                    .addLore("")
                    .addLore("§cBedWars Solo:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("§8▪ §fУбийств: §70")
                    .addLore("")
                    .addLore("§cBedWars Doubles:")
                    .addLore("§8▪ §fПобед: §70")
                    .addLore("§8▪ §fУбийств: §70")
                    .build());

            int skywarsSoloWins = (int) plazmixUser.getDatabaseValue("SWSI", "wins") + (int) plazmixUser.getDatabaseValue("SWSN", "wins");
            int skywarsSoloKills = (int) plazmixUser.getDatabaseValue("SWSI", "kills") + (int) plazmixUser.getDatabaseValue("SWSN", "kills");

            int skywarsDoublesWins = (int) plazmixUser.getDatabaseValue("SWDI", "wins") + (int) plazmixUser.getDatabaseValue("SWDN", "wins");
            int skywarsDoublesKills = (int) plazmixUser.getDatabaseValue("SWDI", "kills") + (int) plazmixUser.getDatabaseValue("SWDN", "kills");

            int skywarsRankedWins = plazmixUser.getDatabaseValue("SWR", "wins");
            int skywarsRankedKills = plazmixUser.getDatabaseValue("SWR", "kills");
            int skywarsRankedRating = plazmixUser.getDatabaseValue("SWR", "rating");


            setOriginalItem(22, ItemUtil.newBuilder(Material.SKULL_ITEM)
                    .setDurability(3)
                    .setTextureValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhiZThhYmQ2NmQwOWE1OGNlMTJkMzc3NTQ0ZDcyNmQyNWNhZDdlOTc5ZThjMjQ4MTg2NmJlOTRkM2IzMmYifX19")

                    .setName("§bSkyWars §8§k§l|")

                    .addLore("")
                    .addLore("§bSkyWars - §nОбщая статистика§b:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsSoloWins + skywarsDoublesWins + skywarsRankedWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsSoloKills + skywarsDoublesKills + skywarsRankedKills))
                    .addLore("§8▪ §fРейтиг: §7" + NumberUtil.spaced(skywarsRankedRating))
                    .addLore("")
                    .addLore("§bSkyWars Solo:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsSoloWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsSoloKills))
                    .addLore("")
                    .addLore("§bSkyWars Doubles:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsDoublesWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsDoublesKills))
                    .addLore("")
                    .addLore("§bSkyWars Ranked:")
                    .addLore("§8▪ §fПобед: §7" + NumberUtil.spaced(skywarsRankedWins))
                    .addLore("§8▪ §fУбийств: §7" + NumberUtil.spaced(skywarsRankedKills))
                    .addLore("§8▪ §fРейтиг: §7" + NumberUtil.spaced(skywarsRankedRating))
                    .build());
        }
    }

}
