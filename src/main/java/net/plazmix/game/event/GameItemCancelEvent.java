package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomEvent;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.user.GameUser;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class GameItemCancelEvent extends BaseCustomEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final GameUser gameUser;
    private final GameItem gameItem;

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
