package carpetfixes.mixins.entityFixes;

import carpetfixes.CFSettings;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Fixes projectiles lossing piercing when not hitting an enderman
 */

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntity_piercingMixin {


    @Inject(
            method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;getPierceLevel()B",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            )
    )
    private void cf$onEndermanCheck(EntityHitResult entityHitResult, CallbackInfo ci, @Local(ordinal = 0) Entity entity, @Share("entity2") LocalRef<Entity> entityRef) {
        entityRef.set(entity);
    }

    @Redirect(
            method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;getPierceLevel()B",
                    ordinal = 0
            )
    )
    private byte cf$skipForEnderman(PersistentProjectileEntity instance,
                                    @Local(ordinal = 1) Entity lastEntityRef) {
        return CFSettings.endermanLowerPiercingFix ? lastEntityRef.getType() == EntityType.ENDERMAN ?
                0 :
                instance.getPierceLevel() : instance.getPierceLevel();
    }
}
