package com.example.handmod.mixin;

import com.example.handmod.HandConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void handmod$pre(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand,
                             float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                             OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {
        HandConfig.Hand c = hand == Hand.MAIN_HAND ? HandConfig.get().main : HandConfig.get().off;
        float[] v = c.v;

        matrices.push();
        matrices.translate(v[HandConfig.X], v[HandConfig.Y], v[HandConfig.Z]);

        // вращаем/масштабируем вокруг примерного центра руки, а не вокруг камеры
        boolean rightSide = (hand == Hand.MAIN_HAND) == (player.getMainArm() == Arm.RIGHT);
        float px = rightSide ? 0.5f : -0.5f, py = -0.4f, pz = -0.7f;
        matrices.translate(px, py, pz);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(v[HandConfig.RX]));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v[HandConfig.RY]));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v[HandConfig.RZ]));
        matrices.scale(v[HandConfig.SCALE], v[HandConfig.SCALE], v[HandConfig.SCALE]);
        matrices.translate(-px, -py, -pz);
    }

    @Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
    private void handmod$post(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand,
                              float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                              OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {
        matrices.pop();
    }
}
