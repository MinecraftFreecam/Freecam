package net.xolt.freecam;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
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

    /// Most target versions come with MixinExtras, but for those that don't we must ensure it is bootstrapped.
    /// - fabric-loader [added](https://github.com/FabricMC/fabric-loader/pull/854) MixinExtras in 0.15.0 (~1.20.4).
    /// - NeoForge [added](https://github.com/neoforged/NeoForge/pull/303) MixinExtras in 20.2.84-beta (1.20.2).
    /// - Forge [added](https://github.com/MinecraftForge/MinecraftForge/pull/10709) MixinExtras in 60.0.16 (1.21.10).
    ///
    /// [MixinExtrasBootstrap#init()] is a no-op on subsequent calls, so we simply _always_ call it.
    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

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
