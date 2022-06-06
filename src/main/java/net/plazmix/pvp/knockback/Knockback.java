package net.plazmix.pvp.knockback;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Knockback {

    @Getter
    private static Knockback instance = new Knockback();

    private boolean knockbackEnabled;
    private boolean sprintMultiplierEnabled;

    private double groundHorizMultiplier;
    private double groundVertMultiplier;

    private double airHorizMultiplier;
    private double airVertMultiplier;

    private double sprintMultiplierHoriz;
    private double sprintMultiplierVert;
    private double sprintYawFactor;

    public void setDefaults() {
        this.knockbackEnabled = true;
        this.sprintMultiplierEnabled = false;

        this.groundHorizMultiplier = 0.42;
        this.groundVertMultiplier = 0.3;

        this.airHorizMultiplier = 0.42;
        this.airVertMultiplier = 0.3;

        this.sprintMultiplierHoriz = 2;
        this.sprintMultiplierVert = 1.3;
        this.sprintYawFactor = 0.5;
    }
}
