plugins {
    id("freecam.i18n")
}

i18n {
    transform("${meta.id}.mod.description") {
        rename("modmenu.descriptionTranslation.${meta.id}")
        rename("fml.menu.mods.info.description.${meta.id}")
    }
}
