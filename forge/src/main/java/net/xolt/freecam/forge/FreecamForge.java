package net.xolt.freecam.forge;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;
//? forge: > 40.2.14 {
/*import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
*///? } else {
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
//? }

@Mod(value = Freecam.MOD_ID)
@Mod.EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
@SuppressWarnings("unused")
public class FreecamForge {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModConfig.init();
        // Register our config screen with Forge
        ModLoadingContext.get().registerExtensionPoint(
                //? forge: > 40.2.14 {
                /*ConfigScreenHandler.ConfigScreenFactory.class,
                *///? } else
                ConfigGuiHandler.ConfigGuiFactory.class,
                //? forge: > 40.2.14 {
                /*() -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) ->
                *///? } else
                () -> new ConfigGuiHandler.ConfigGuiFactory((client, parent) ->
                        AutoConfig.getConfigScreen(ModConfig.class, parent).get()
        ));
        //? forge: <= 40.2.14
        ModBindings.forEach(ClientRegistry::registerKeyBinding);
    }

    //? forge: > 40.2.14 {
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