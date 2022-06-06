package net.plazmix.utility.achievement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementGroup {

    MAIN("Общие"),

    SKYWARS("SkyWars"),

    BEDWARS("BedWars"),

    GUNGAME("GunGame"),

    PRISON("Prison"),

    ONEBLOCK("OneBlock"),

    SKYBLOCK("SkyBlock"),

    SPACE_PORTALS("SpacePortals"),

    PRACTICE("Practice"),

    BUILD_BATTLE("BuildBattle"),

    SPEED_BUILDERS("SpeedBuilders"),

    TNT_GAMES("TNT Games"),
    ;

    private final String title;
}
