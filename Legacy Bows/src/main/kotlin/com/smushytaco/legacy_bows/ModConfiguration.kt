package com.smushytaco.legacy_bows
import io.wispforest.owo.config.Option
import io.wispforest.owo.config.annotation.Config
import io.wispforest.owo.config.annotation.Modmenu
import io.wispforest.owo.config.annotation.Sync
@Modmenu(modId = LegacyBows.MOD_ID)
@Config(name = LegacyBows.MOD_ID, wrapperName = "ModConfig")
@Suppress("UNUSED")
class ModConfiguration {
    @JvmField
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    var enableLegacyBows = true
    @JvmField
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    var enableLegacyCrossbows = true
}