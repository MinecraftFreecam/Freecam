package net.xolt.freecam;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FreecamMixinConfig implements IMixinConfigPlugin {

    private static final List<String> DYNAMIC_MIXINS = List.of(
            "net.xolt.freecam.mixins.IrisHandRendererMixin",
            "net.xolt.freecam.mixins.IrisShadowRendererMixin"
    );

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (DYNAMIC_MIXINS.contains(mixinClassName)) {
            return isClassLoaded(targetClassName);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
