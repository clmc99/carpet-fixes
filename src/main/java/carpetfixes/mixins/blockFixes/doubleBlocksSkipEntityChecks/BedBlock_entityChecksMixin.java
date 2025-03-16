package carpetfixes.mixins.blockFixes.doubleBlocksSkipEntityChecks;

import carpetfixes.CFSettings;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes being able to place a double block within an entity due to a missing check in the second block
 */
@Mixin(BedBlock.class)
public class BedBlock_entityChecksMixin {


    @Inject(
            method = "getPlacementState",
            at = @At("RETURN"),
            cancellable = true
    )
    private void cf$canPlaceBed(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        if (CFSettings.doubleBlocksSkipEntityCheckFix && cir.getReturnValue() != null &&
                !ctx.getWorld().canPlace(cir.getReturnValue(), ctx.getBlockPos().offset(ctx.getHorizontalPlayerFacing()), ShapeContext.absent())) {
            cir.setReturnValue(null);
        }
    }
}
