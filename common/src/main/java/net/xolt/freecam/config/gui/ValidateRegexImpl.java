package net.xolt.freecam.config.gui;

import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static net.xolt.freecam.config.gui.AutoConfigExtensions.isArrayOrListOfType;
import static org.apache.commons.lang3.StringUtils.substringBefore;

class ValidateRegexImpl {
    private ValidateRegexImpl() {}

    static void apply(GuiRegistry registry) {

        registry.registerAnnotationTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    guis.stream()
                            .filter(StringListEntry.class::isInstance)
                            .map(StringListEntry.class::cast)
                            .forEach(entry -> entry.setErrorSupplier(() -> regexCompileError(entry.getValue())));
                    return guis;
                },
                field -> Objects.equals(String.class, field.getType()),
                ValidateRegex.class
        );

        registry.registerAnnotationTransformer(
                (guis, i18n, field, config, defaults, guiProvider) -> {
                    guis.stream()
                            .filter(StringListListEntry.class::isInstance)
                            .map(StringListListEntry.class::cast)
                            .forEach(entry -> entry.setCellErrorSupplier(ValidateRegexImpl::regexCompileError));
                    return guis;
                },
                isArrayOrListOfType(String.class),
                ValidateRegex.class
        );
    }

    /**
     * Supplies an error message when the text is not a valid {@link Pattern regex pattern}.
     *
     * @param text the text that should compile to a regex.
     * @return an optional error message.
     */
    private static Optional<Component> regexCompileError(String text) {
        try {
            Pattern.compile(text);
            return Optional.empty();
        } catch (PatternSyntaxException e) {
            String message = substringBefore(
                    e.getLocalizedMessage(),
                    //? if >1.17.1 {
                    '\n'
                    //? } else
                    //"\n"
            );
            return Optional.of(Component.translatable("text.autoconfig.freecam.error.invalidRegex", message));
        }
    }
}
