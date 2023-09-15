package dev.vanutp.suppress_sodium_xaero_error.mixin;

import net.minecraft.client.gl.GlDebug;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlDebug.class)
public abstract class GlDebugMixin {
    @Unique
    private static boolean hasPostedMessage = false;

    @Unique
    private static Logger LOGGER = LoggerFactory.getLogger("Suppress Sodium & Xaero OpenGL Error");

    @Inject(at = @At(value = "HEAD"), method = "info(IIIIIJJ)V", cancellable = true)
    private static void suppressMessage(int source, int type, int id, int severity, int messageLength, long message,
                                        long l,
                                        CallbackInfo ci) {
        String messageText = GLDebugMessageCallback.getMessage(messageLength, message);

        if (id == 1 && messageText.equals("GL_INVALID_ENUM in glGetIntegerv(pname=GL_TEXTURE_FREE_MEMORY_ATI)")) {
            if (hasPostedMessage) {
                ci.cancel();
            } else {
                LOGGER.info("The error below will not be shown again for this run of Minecraft.");
                hasPostedMessage = true;
            }
        }
    }
}
