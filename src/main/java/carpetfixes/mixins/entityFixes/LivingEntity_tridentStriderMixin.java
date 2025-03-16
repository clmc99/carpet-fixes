package carpetfixes.mixins.entityFixes;

import carpetfixes.CFSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes depth strider making riptide slower
 */

@Mixin(LivingEntity.class)
public abstract class LivingEntity_tridentStriderMixin extends Entity {

    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    public LivingEntity_tridentStriderMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D",
                    ordinal = 0
            )
    )
    private double cf$modifyDepthStriderIfUsingRiptide(LivingEntity entity, RegistryEntry<EntityAttribute> attribute) {
        if (CFSettings.depthStriderSlowsRiptideFix && entity.isUsingRiptide()) {
            return 0;
        }
        return this.getAttributeValue(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY);
    }
}
