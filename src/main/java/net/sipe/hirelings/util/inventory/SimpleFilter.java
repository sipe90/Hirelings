package net.sipe.hirelings.util.inventory;

import java.util.HashSet;
import java.util.Set;

public class SimpleFilter<T> extends AbstractFilter<T> {

    public SimpleFilter(Set<T> items, Type type) {
        super(items, type);
    }

    public static <T>SimpleFilter<T> allowAllFilter() {
        return new SimpleFilter<>(new HashSet<T>(), SimpleFilter.Type.BLACKLIST);
    }

    @Override
    public boolean test(T toTest) {
        return (type == Type.WHITELIST) == items.contains(toTest);
    }
}
