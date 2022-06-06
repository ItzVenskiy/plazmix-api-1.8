package net.plazmix.protocollib.entity;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public abstract class FakeBaseEntityMob
        extends FakeBaseEntityLiving implements FakeEntityMob {

    private boolean noAI;

    public FakeBaseEntityMob(@NonNull EntityType entityType, @NonNull Location location) {
        super(entityType, location);
    }

    public void setNoAI(boolean noAI) {
        this.noAI = noAI;

        broadcastDataWatcherObject(15, generateBitMask());
    }

    private byte generateBitMask() {
        return (byte) ((noAI ? 0x01 : 0));
    }
}
