package Utilities;

import interfaces.RepresentativeValue;

import java.util.HashMap;
import java.util.Map;

public class EnumValueMap<T, V extends Enum<V> & RepresentativeValue<T>> {
    private final Map<T, V> map = new HashMap<T, V>();
    public EnumValueMap(Class<V> valueType) {
        for (V v : valueType.getEnumConstants()) {
            map.put(v.getRepVal(), v);
        }
    }

    public V get(T val) {
        return map.get(val);
    }
}
