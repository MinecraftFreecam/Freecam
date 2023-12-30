package net.xolt.freecam.forge;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.ModConfig;

@Mod(Freecam.MOD_ID)
@Mod.EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
@SuppressWarnings("unused")
public class FreecamForge {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModConfig.init();

        // Register our config screen with Forge
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) ->
                AutoConfig.getConfigScreen(ModConfig.class, parent).get()
        ));
    }

    @SubscribeEvent
    public static void registerKeymappings(RegisterKeyMappingsEvent event) {
        ModBindings.forEach(event::register);
    }

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
