package com.smushytaco.legacy_bows.mixins;
import com.smushytaco.legacy_bows.LegacyBows;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(BowItem.class)
public abstract class BowMixin {
    @Shadow
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {}
    @Inject(method = "getPullProgress", at = @At("HEAD"), cancellable = true)
    private static void hookGetPullProgress(int useTicks, CallbackInfoReturnable<Float> cir) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyBows()) return;
        cir.setReturnValue(1.0F);
    }
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void hookUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyBows()) return;
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getProjectileType(itemStack).isEmpty();
        if (!user.getAbilities().creativeMode && !bl) return;
        onStoppedUsing(user.getStackInHand(hand), world, user, 0);
        cir.setReturnValue(TypedActionResult.fail(itemStack));
    }
}