package net.xolt.freecam.config.gui;

import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

final class FallbackConfigScreen extends GenericMessageScreen {

    private static final Component TITLE = Component.translatable("text.freecam.missingConfigGui.title");

    private final Screen parent;
    private final List<Component> requirementText;

    FallbackConfigScreen(Screen parent, Collection<OptionalProvider> supportedProviders) {
        super(TITLE);
        this.parent = parent;
        requirementText = supportedProviders.stream()
                .map(provider -> renderRequirement(provider.getRequirement()))
                .toList();
    }

    private static Component renderRequirement(DependencyDescription requirement) {
        //? if java: >=21 {
        return switch (requirement) {
            case DependencyDescription.Literal literal -> renderRequirement(literal);
            case DependencyDescription.Translatable translatable -> renderRequirement(translatable);
        };
        //? } else {
        /*if (requirement instanceof DependencyDescription.Literal literal) return renderRequirement(literal);
        if (requirement instanceof DependencyDescription.Translatable translatable) return renderRequirement(translatable);
        throw new IllegalStateException();
        *///? }
    }

    private static Component renderRequirement(DependencyDescription.Literal requirement) {
        return Component.literal(requirement.text());
    }

    private static Component renderRequirement(DependencyDescription.Translatable requirement) {
        return Component.translatable(requirement.key(), requirement.args().toArray());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
