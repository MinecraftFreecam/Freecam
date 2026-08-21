plugins {
    id("freecam.i18n")
}

i18n {
    transform("${meta.id}.mod.description") {
        // Fabric ModMenu — https://github.com/TerraformersMC/ModMenu#translation-api
        rename("modmenu.descriptionTranslation.${meta.id}")

        // NeoForge 20.4.179 — https://github.com/neoforged/NeoForge/pull/649
        // https://docs.neoforged.net/docs/1.21.1/resources/client/i18n#translating-mod-metadata
        rename("fml.menu.mods.info.description.${meta.id}")

        // NeoForge 26.2.0.50-beta — https://github.com/neoforged/NeoForge/pull/3073
        rename("neoforge.screen.mods.info.description.${meta.id}")
    }
}
