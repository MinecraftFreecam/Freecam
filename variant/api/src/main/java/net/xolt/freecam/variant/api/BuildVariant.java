package net.xolt.freecam.variant.api;

public interface BuildVariant {

    String name();

    boolean cheatsPermitted();

    static BuildVariant getInstance() {
        return SingleInstanceServiceLoader.get(BuildVariant.class);
    }
}
