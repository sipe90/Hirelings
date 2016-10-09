package net.sipe.hirelings.util.inventory;

import java.util.Set;
import java.util.function.Predicate;

import static net.sipe.hirelings.util.inventory.AbstractFilter.Type.BLACKLIST;
import static net.sipe.hirelings.util.inventory.AbstractFilter.Type.WHITELIST;


public abstract class AbstractFilter<T> implements Predicate<T> {

    public enum Type {
        WHITELIST,
        BLACKLIST
    }

    protected final Set<T> items;

    protected Type type;

    public AbstractFilter(Set<T> items, Type type) {
        this.items = items;
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Set<T> getItems() { return items; }

    public void clearItems() {
        items.clear();
    }

    public boolean allowNone() {
        return type == WHITELIST && items.isEmpty();
    }

    public boolean allowAll() {
        return  type == BLACKLIST && items.isEmpty();
    }
}
