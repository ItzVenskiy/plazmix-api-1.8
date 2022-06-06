package net.plazmix.pvp.attribute;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PvpTargetedAttribute<T, V> {

    String id;
    Class<T> targetType;

    Map<T, V> targetValueMap = new HashMap<>();

    public void set(T target, V value) {
        targetValueMap.put(target, value);
    }

    public V getValue(T target) {
        return targetValueMap.get(target);
    }

}
