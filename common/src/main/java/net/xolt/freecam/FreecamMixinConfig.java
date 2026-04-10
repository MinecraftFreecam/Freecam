package net.xolt.freecam;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FreecamMixinConfig implements IMixinConfigPlugin {

    private static final Map<String, String> MOD_MIXINS = Map.of(
            "net.xolt.freecam.mixins.IrisHandRendererMixin", "iris",
            "net.xolt.freecam.mixins.IrisShadowRendererMixin", "iris"
    );

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String modId = MOD_MIXINS.get(mixinClassName);
        return modId == null || ModPlatform.get().isModLoaded(modId);
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
}
