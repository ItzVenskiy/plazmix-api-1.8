package net.plazmix.pvp;

import net.plazmix.pvp.attribute.PvpAttribute;
import net.plazmix.pvp.attribute.PvpTargetedAttribute;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class PvpSettings
{
    // Разрешение на установки задержек в пвп.
    public static final PvpAttribute<Boolean> COOLDOWN_ALLOW                            = new PvpAttribute<>("cooldown_allow", false);

    // Задержка (на цель) при получении любого урона (от падения, горения, и т.д.).
    public static final PvpAttribute<Long> TAKE_SELF_COOLDOWN                           = new PvpAttribute<>("take_self_cooldown", 0L);

    // Задержка (на дамагера) при получении урона от другого существа.
    public static final PvpAttribute<Long> TAKE_TARGET_COOLDOWN                         = new PvpAttribute<>("take_target_cooldown", 0L);

    // Значение вектора траектории, по которой будет отлетать игрок при уроне на него от другого существа с пустой рукой.
    public static final PvpAttribute<Vector> EMPTY_HAND_VELOCITY                        = new PvpAttribute<>("empty_hand_velocity");

    // Значение вектора траектории, по которой будет отлетать игрок при уроне на него от другого существа не с пустой рукой.
    public static final PvpTargetedAttribute<ItemStack, Vector> ITEM_HAND_VELOCITY      = new PvpTargetedAttribute<>("item_hand_velocity", ItemStack.class);

    // Значение вектора траектории, по которой будет отлетать игрок при уроне на него от другого существа.
    public static final PvpTargetedAttribute<EntityType, Vector> ENTITY_TARGET_VELOCITY = new PvpTargetedAttribute<>("entity_target_velocity", EntityType.class);

}
