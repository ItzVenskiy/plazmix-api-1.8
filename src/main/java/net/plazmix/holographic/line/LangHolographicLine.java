package net.plazmix.holographic.line;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.plazmix.coreconnector.utility.localization.LocalizationResource;
import net.plazmix.holographic.ProtocolHolographic;
import net.plazmix.holographic.ProtocolHolographicLine;
import net.plazmix.holographic.manager.ProtocolHolographicManager;
import net.plazmix.protocollib.entity.impl.FakeArmorStand;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

@Getter
public class LangHolographicLine extends FakeArmorStand implements ProtocolHolographicLine {

    protected final int lineIndex;

    protected final boolean localized = true;

    protected final ProtocolHolographic holographic;

    @Setter
    protected String lineText;

    @Setter
    private BiFunction<Player, LocalizationResource, String> messageHandler;


    public LangHolographicLine(int lineIndex, BiFunction<Player, LocalizationResource, String> messageHandler, ProtocolHolographic holographic) {
        super(holographic.getLocation().clone().add(0, -(0.25 * lineIndex), 0));

        this.lineText = (ChatColor.RESET.toString());

        this.lineIndex = lineIndex;
        this.holographic = holographic;
        this.messageHandler = messageHandler;
    }

    @Override
    public void initialize() {
        setBasePlate(false);
        setInvisible(true);

        setCustomName(lineText);

        updateLine(viewerCollection.toArray(new Player[0]));
    }

    @Override
    public void update() {
        updateLine(viewerCollection.toArray(new Player[0]));
    }

    @Override
    public void addViewers(@NonNull Player... viewers) {
        super.addViewers(viewers);

        updateLine(viewers);
    }

    @Override
    public void teleport(@NonNull Location location) {
        this.location = location.clone().add(0, -(0.25 * lineIndex), 0);

        super.teleport(this.location);
    }

    @Override
    public FakeArmorStand getFakeArmorStand() {
        return this;
    }

    protected void updateLine(Player... playersTo) {
        for (Player player : playersTo) {

            if (!ProtocolHolographicManager.INSTANCE.getProtocolHolographics(player).contains(holographic)) {
                ProtocolHolographicManager.INSTANCE.addProtocolHolographic(player, holographic);
            }

            LocalizationResource localizationResource = PlazmixUser.of(player).localization().getLocalizationResource();
            setCustomName(player, messageHandler.apply(player, localizationResource));
        }
    }

}
