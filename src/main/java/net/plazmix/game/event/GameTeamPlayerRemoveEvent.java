package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomCancellableEvent;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class GameTeamPlayerRemoveEvent extends BaseCustomCancellableEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final GameUser gameUser;
    private final GameTeam gameTeam;

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
