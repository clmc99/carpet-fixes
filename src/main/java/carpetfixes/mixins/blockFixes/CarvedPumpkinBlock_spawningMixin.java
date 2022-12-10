package carpetfixes.mixins.blockFixes;

import carpetfixes.CFSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

/**
 * Allows replaceable materials to be within the iron golem structure when the iron golem attempts to spawn.
 */
@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlock_spawningMixin {


    @Redirect(
            method = "getIronGolemPattern()Lnet/minecraft/block/pattern/BlockPattern;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/pattern/CachedBlockPosition;" +
                            "matchesBlockState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;",
                    ordinal = 2
            )
    )
    private Predicate<CachedBlockPosition> replaceableMaterialPredicate(Predicate<BlockState> state){
        if (CFSettings.witherGolemSpawningFix) state = CFSettings.IS_REPLACEABLE;
        return CachedBlockPosition.matchesBlockState(state);
    }


    @Redirect(
            method = "getIronGolemDispenserPattern()Lnet/minecraft/block/pattern/BlockPattern;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/pattern/CachedBlockPosition;" +
                            "matchesBlockState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;",
                    ordinal = 1
            )
    )
    private Predicate<CachedBlockPosition> replaceableMaterialPredicateDispenser(Predicate<BlockState> state){
        if (CFSettings.witherGolemSpawningFix) state = CFSettings.IS_REPLACEABLE;
        return CachedBlockPosition.matchesBlockState(state);
    }
}
