package net.xolt.freecam.forge;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;

@Mod(Freecam.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@SuppressWarnings("unused")
public class FreecamForge {

    public FreecamForge(ModContainer container) {
        // Register our config screen with Forge
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) ->
                AutoConfig.getConfigScreen(ModConfig.class, parent).get());
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModConfig.init();
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        ModBindings.forEach(event::register);
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GlobalEventHandler {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void preTick(ClientTickEvent.Pre event) {
            Freecam.preTick(Minecraft.getInstance());
        }
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void postTick(ClientTickEvent.Post event) {
            Freecam.postTick(Minecraft.getInstance());
        }
    }
}
