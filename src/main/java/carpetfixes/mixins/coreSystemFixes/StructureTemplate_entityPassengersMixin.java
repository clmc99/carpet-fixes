package carpetfixes.mixins.coreSystemFixes;

import carpetfixes.CFSettings;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Structures don't create passenger entities since they call the wrong method.
 */

@Mixin(StructureTemplate.class)
public class StructureTemplate_entityPassengersMixin {


    @Inject(
            method = "spawnEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/structure/StructureTemplate;" +
                            "getEntity(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/nbt/NbtCompound;)" +
                            "Ljava/util/Optional;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cf$getEntityWithPassengers(ServerWorldAccess world, BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot, BlockBox area, boolean initializeMobs, CallbackInfo ci, @Local NbtCompound nbtCompound, @Local(ordinal = 1) Vec3d vec3d2) {
        if (CFSettings.structuresIgnorePassengersFix) {
            Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world.toServerWorld(), e -> e);
            if (entity != null) {
                float f = entity.applyRotation(rotation);
                f += entity.applyMirror(mirror) - entity.getYaw();
                entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.getPitch());
                if (initializeMobs && entity instanceof MobEntity) {
                    ((MobEntity) entity).initialize(
                            world,
                            world.getLocalDifficulty(BlockPos.ofFloored(vec3d2)),
                            SpawnReason.STRUCTURE,
                            null
                    );
                }
                world.spawnEntityAndPassengers(entity);
            }
            ci.cancel();
        }
    }
}
