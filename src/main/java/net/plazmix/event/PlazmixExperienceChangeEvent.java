package net.plazmix.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlazmixExperienceChangeEvent extends BaseCustomCancellableEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final Player player;
    private final int previousExp, currentExp;

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
