package net.xolt.freecam.forge;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) ->
                AutoConfig.getConfigScreen(ModConfig.class, parent).get()
        );

        // Register KeyBindings
        ModBindings.forEach(ClientRegistry::registerKeyBinding);
    }

    @Mod.EventBusSubscriber(bus = Bus.FORGE, value = Dist.CLIENT)
    public static class GlobalEventHandler {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onTick(TickEvent.ClientTickEvent event) {
            final Minecraft client = Minecraft.getInstance();
            switch (event.phase) {
                case START: Freecam.preTick(client); break;
                case END: Freecam.postTick(client); break;
            }
        }
    }
}
