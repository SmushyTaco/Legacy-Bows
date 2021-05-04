package com.smushytaco.legacy_bows.mixins;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {
    @Shadow
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {}
    @Shadow
    private static float getSpeed(ItemStack stack) { throw new AssertionError(); }
    @Shadow
    public static boolean isCharged(ItemStack stack) { throw new AssertionError(); }
    @Shadow
    public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {}
    @Shadow
    public static void setCharged(ItemStack stack, boolean charged) {}
    @Inject(method = "getPullProgress", at = @At("HEAD"), cancellable = true)
    private static void hookGetPullProgress(int useTicks, ItemStack stack, CallbackInfoReturnable<Float> cir) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows()) return;
        cir.setReturnValue(1.0F);
    }
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void hookUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!LegacyBows.INSTANCE.getConfig().getEnableLegacyCrossbows()) return;
        ItemStack itemStack = user.getStackInHand(hand);
        if (isCharged(itemStack)) {
            shootAll(world, user, hand, itemStack, getSpeed(itemStack), 1.0F);
            setCharged(itemStack, false);
        }
        onStoppedUsing(itemStack, world, user, 0);
        cir.setReturnValue(TypedActionResult.fail(itemStack));
    }
}