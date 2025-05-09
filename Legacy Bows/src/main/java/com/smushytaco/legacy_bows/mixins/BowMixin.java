package com.smushytaco.legacy_bows.mixins;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smushytaco.legacy_bows.LegacyBows;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(BowItem.class)
public abstract class BowMixin {
    @Shadow
    public abstract boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);
    @ModifyReturnValue(method = "getPullProgress", at = @At("RETURN"))
    private static float hookGetPullProgress(float original, int useTicks) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyBows() ? original : 1.0F; }
    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    public ActionResult hookUse(ActionResult original) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyBows() ? original : ActionResult.FAIL; }
    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setCurrentHand(Lnet/minecraft/util/Hand;)V"))
    public void hookUseSetCurrentHand(PlayerEntity instance, Hand hand, Operation<Void> original, World world, PlayerEntity user, Hand handTwo) {
        if (LegacyBows.INSTANCE.getConfig().getEnableLegacyBows()) {
            onStoppedUsing(user.getStackInHand(handTwo), world, user, 0);
        } else {
            original.call(instance, hand);
        }
    }
}