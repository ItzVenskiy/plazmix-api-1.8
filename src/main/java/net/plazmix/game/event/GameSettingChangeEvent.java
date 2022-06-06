package net.plazmix.game.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.event.BaseCustomCancellableEvent;
import net.plazmix.game.setting.GameSetting;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class GameSettingChangeEvent extends BaseCustomCancellableEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final GameSetting gameSetting;
    private final Object previousValue, currentValue;

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
