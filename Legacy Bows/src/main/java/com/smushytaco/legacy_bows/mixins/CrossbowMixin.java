package com.smushytaco.legacy_bows.mixins;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.smushytaco.legacy_bows.LegacyBows;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {
    @Shadow
    public abstract void shootAll(World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence, @Nullable LivingEntity target);

    @Shadow
    private static float getSpeed(ChargedProjectilesComponent stack) { return 0; }

    @Shadow
    public abstract void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks);

    @ModifyReturnValue(method = "getPullProgress", at = @At("RETURN"))
    private static float hookGetPullProgress(float original, int useTicks) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows() ? original : 1.0F; }
    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    public ActionResult hookUse(ActionResult original, World world, PlayerEntity user, Hand hand) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows()) return original;
        ItemStack itemStack = user.getStackInHand(hand);
        ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) usageTick(world, user, itemStack, 0);
        shootAll(world, user, hand, itemStack, getSpeed(chargedProjectilesComponent), 1.0F, null);
        return ActionResult.FAIL;
    }
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean hookUseIsEmpty(boolean original) { return LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows() || original; }
}