package com.smushytaco.legacy_bows.mixins;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.smushytaco.legacy_bows.LegacyBows;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {
    @Shadow
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {}
    @ModifyReturnValue(method = "getPullProgress", at = @At("RETURN"))
    private static float hookGetPullProgress(float original, int useTicks) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows() ? original : 1.0F; }
    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    public TypedActionResult<ItemStack> hookUse(TypedActionResult<ItemStack> original, World world, PlayerEntity user, Hand hand) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows()) return original;
        ItemStack itemStack = user.getStackInHand(hand);
        onStoppedUsing(itemStack, world, user, 0);
        return TypedActionResult.fail(itemStack);
    }
    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean hookUseIsEmpty(boolean original) { return LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows() || original; }
}