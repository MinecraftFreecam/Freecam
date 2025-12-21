package net.xolt.freecam.forge;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;

@Mod(value = Freecam.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
@SuppressWarnings("unused")
public class FreecamForge {

    public FreecamForge(ModContainer container) {
        ModConfig.init();
        // Register our config screen with Forge
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) ->
                AutoConfigClient.getConfigScreen(ModConfig.class, parent).get());
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        ModBindings.forEach(event::register);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void preTick(ClientTickEvent.Pre event) {
        Freecam.preTick(Minecraft.getInstance());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void postTick(ClientTickEvent.Post event) {
        Freecam.postTick(Minecraft.getInstance());
    }
}
