package com.smushytaco.legacy_bows.configuration_support
import com.smushytaco.legacy_bows.LegacyBows
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment
@Config(name = LegacyBows.MOD_ID)
class ModConfiguration: ConfigData {
    @Comment("Default value is yes. If set to yes bows will shoot like machine guns like they did in the past. If set to no they won't.")
    val enableLegacyBows = true
    @Comment("Default value is yes. If set to yes crossbows will shoot like machine guns like bows did in the past. If set to no they won't.")
    val enableLegacyCrossbows = true
}