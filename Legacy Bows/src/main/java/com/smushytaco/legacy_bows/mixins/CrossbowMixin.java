package com.smushytaco.legacy_bows.mixins;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.smushytaco.legacy_bows.LegacyBows;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {
    @Shadow
    public abstract void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float power, float uncertainty, @Nullable LivingEntity targetOverride);

    @Shadow
    private static float getShootingPower(ChargedProjectiles projectiles) { return 0; }

    @Shadow
    public abstract void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int ticksRemaining);

    @ModifyReturnValue(method = "getPowerForTime", at = @At("RETURN"))
    private static float hookGetPullProgress(float original, int timeHeld) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows() ? original : 1.0F; }
    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    public InteractionResult hookUse(InteractionResult original, Level level, Player player, InteractionHand hand) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows()) return original;
        ItemStack itemStack = player.getItemInHand(hand);
        ChargedProjectiles chargedProjectilesComponent = itemStack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) onUseTick(level, player, itemStack, 0);
        performShooting(level, player, hand, itemStack, getShootingPower(chargedProjectilesComponent), 1.0F, null);
        return InteractionResult.FAIL;
    }
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean hookUseIsEmpty(boolean original) { return LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows() || original; }
}