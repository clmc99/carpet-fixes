package carpetfixes.mixins.coreSystemFixes;

import carpetfixes.CFSettings;
import net.minecraft.server.world.ServerChunkLoadingManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Keeps track of chunk ticks phases for the chunk saving
 */

@Mixin(ServerChunkLoadingManager.class)
public class ThreadedAnvilChunkStorage_tickSaveMixin {


    @Inject(
            method = "unloadChunks",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/world/ServerChunkLoadingManager;" +
                            "chunkHolders:Lit/unimi/dsi/fastutil/longs/Long2ObjectLinkedOpenHashMap;",
                    opcode = Opcodes.GETFIELD,
                    shift = At.Shift.BEFORE
            )
    )
    private void cf$setTickSaveState(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CFSettings.IS_TICK_SAVE.set(true);
    }


    @Inject(
            method = "unloadChunks",
            at = @At("RETURN")
    )
    private void cf$unsetTickSaveState(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CFSettings.IS_TICK_SAVE.set(false);
    }
}
