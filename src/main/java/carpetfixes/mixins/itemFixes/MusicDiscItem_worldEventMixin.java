package carpetfixes.mixins.itemFixes;

import carpetfixes.CFSettings;
import carpetfixes.helpers.DelayedWorldEventManager;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes being able to play multiple music disks at the same time.
 * We do this by delaying the world events and synchronizing them to all happen at the end of the tick
 */

@Mixin(JukeboxPlayableComponent.class)
public class MusicDiscItem_worldEventMixin {
    @Redirect(
            method = "tryPlayStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/event/GameEvent$Emitter;)V"
            )
    )
    private static void cf$worldEvent(World world, RegistryEntry<GameEvent> gameEvent, BlockPos blockPos, GameEvent.Emitter emitter) {
        if (CFSettings.recordWorldEventFix) {
            DelayedWorldEventManager.addDelayedWorldEvent(world, gameEvent, Vec3d.ofCenter(blockPos), emitter);
        } else {
            world.emitGameEvent(gameEvent, blockPos, emitter);
        }
    }
}
