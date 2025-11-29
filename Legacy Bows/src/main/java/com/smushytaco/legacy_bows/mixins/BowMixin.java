package com.smushytaco.legacy_bows.mixins;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smushytaco.legacy_bows.LegacyBows;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(BowItem.class)
public abstract class BowMixin {
    @Shadow
    public abstract boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks);
    @ModifyReturnValue(method = "getPowerForTime", at = @At("RETURN"))
    private static float hookGetPullProgress(float original, int useTicks) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyBows() ? original : 1.0F; }
    @ModifyReturnValue(method = "use", at = @At("RETURN"))
    public InteractionResult hookUse(InteractionResult original) { return !LegacyBows.INSTANCE.getConfig().getEnableLegacyBows() ? original : InteractionResult.FAIL; }
    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"))
    public void hookUseSetCurrentHand(Player instance, InteractionHand hand, Operation<Void> original, Level world, Player user, InteractionHand handTwo) {
        if (LegacyBows.INSTANCE.getConfig().getEnableLegacyBows()) {
            releaseUsing(user.getItemInHand(handTwo), world, user, 0);
        } else {
            original.call(instance, hand);
        }
    }
}