package net.plazmix.protocollib.entity;

public interface FakeEntityMob extends FakeEntityLiving {

    void setNoAI(boolean noAI);

    boolean isNoAI();
}
