package net.plazmix.holographic;

import net.plazmix.coreconnector.utility.localization.LocalizationResource;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface ProtocolHolographic extends ProtocolHolographicSpawnable {

    Location getLocation();


    Set<Player> getViewers();

    Set<Player> getReceivers();


    List<ProtocolHolographicLine> getHolographicLines();

    ProtocolHolographicUpdater getHolographicUpdater();

    ProtocolHolographicLine getHolographicLine(int lineIndex);


    void setHolographicLine(int lineIndex, ProtocolHolographicLine holographicLine);


    void setOriginalHolographicLine(int lineIndex, String holographicLine);

    void setLangHolographicLine(int lineIndex, BiFunction<Player, LocalizationResource, String> messageHandler);

    void setLangClickHolographicLine(int lineIndex, BiFunction<Player, LocalizationResource, String> messageHandler, Consumer<Player> clickAction);

    void setClickHolographicLine(int lineIndex, String holographicLine, Consumer<Player> clickAction);

    void setHeadHolographicLine(int lineIndex, String headTexture, boolean small);

    void setItemHolographicLine(int lineIndex, ItemStack itemStack);

    void setEmptyHolographicLine(int lineIndex);


    void addHolographicLine(ProtocolHolographicLine holographicLine);

    void addOriginalHolographicLine(String holographicLine);

    void addLangHolographicLine(BiFunction<Player, LocalizationResource, String> messageHandler);

    void addLangClickHolographicLine(BiFunction<Player, LocalizationResource, String> messageHandler, Consumer<Player> clickAction);

    void addClickHolographicLine(String holographicLine, Consumer<Player> clickAction);

    void addHeadHolographicLine(String headTexture, boolean small);

    void addItemHolographicLine(ItemStack itemStack);

    void addEmptyHolographicLine();


    void teleport(Location location);

    void setFullClickAction(Consumer<Player> clickAction);

    void setHolographicUpdater(long updateTicks, ProtocolHolographicUpdater holographicUpdater);
}
