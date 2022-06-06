package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.game.state.GameState;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class GameStateShutdownEvent extends BaseCustomEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final GameState state;

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
