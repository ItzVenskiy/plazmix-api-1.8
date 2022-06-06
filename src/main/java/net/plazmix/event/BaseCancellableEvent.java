package net.plazmix.event;

import lombok.Data;
import org.bukkit.event.Cancellable;

@Data
public abstract class BaseCancellableEvent extends BaseCustomEvent implements Cancellable {

    private boolean cancelled;
}
