package net.xolt.freecam.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ConfigScreenProvider;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;
//? forge: >= 41 {
//import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//? } else {
import net.minecraftforge.fmlclient.registry.ClientRegistry;
//? }

@Mod(value = Freecam.MOD_ID)
@Mod.EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
@SuppressWarnings("unused")
public class FreecamForge {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModConfig.setup();
        // Register our config screen with Forge
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
            new ConfigGuiHandler.ConfigGuiFactory((mc, parent) -> ConfigScreenProvider.instance().getConfigScreen(parent))
        );
        //? forge: < 41 {
        ModBindings.forEach(ClientRegistry::registerKeyBinding);
        //? }
    }

    //? forge: >= 41 {
    /*@SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        ModBindings.forEach(event::register);
    }
    *///? }

    @Mod.EventBusSubscriber(bus = Bus.FORGE, value = Dist.CLIENT)
    public static class GlobalEventHandler {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onTick(TickEvent.ClientTickEvent event) {
            final Minecraft client = Minecraft.getInstance();
            switch (event.phase) {
                case START -> Freecam.preTick(client);
                case END -> Freecam.postTick(client);
            }
        }
    }
}