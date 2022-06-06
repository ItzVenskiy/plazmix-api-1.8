package net.plazmix.pvp.knockback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@AllArgsConstructor
public class KnockbackHit {

    private Player attacker;
    private Vector direction;

    private boolean sprintKb;
    private int kbEnchantLevel;

    private long time;
}
