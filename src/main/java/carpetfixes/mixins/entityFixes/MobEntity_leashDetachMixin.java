package carpetfixes.mixins.entityFixes;

import carpetfixes.CFSettings;
import carpetfixes.patches.LeashKnotDetach;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes leashes not removing themselves when a mob detaches from a leash. Leading to a leash knot on a fence
 * connected to nothing
 */

@Mixin(MobEntity.class)
public abstract class MobEntity_leashDetachMixin implements Leashable {
    @Inject(
            method = "detachLeash*",
            at = @At("HEAD")
    )
    private void cf$detachLeash(boolean sendPacket, boolean dropItem, CallbackInfo ci) {
        if (CFSettings.leashKnotNotUpdatingOnBreakFix && this.getLeashHolder() != null &&
                this.getLeashHolder() instanceof LeashKnotEntity leashKnotEntity) {
            ((LeashKnotDetach) leashKnotEntity).carpet_fixes$onDetachLeash((MobEntity)(Object)this);
        }
    }
}
