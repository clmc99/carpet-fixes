package carpetfixes.mixins.blockFixes;

import carpetfixes.CFSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes being able to eat cake from all sides when it obviously only gets eaten from the west
 */
@Mixin(CakeBlock.class)
public class CakeBlock_eatSideMixin {


    @Inject(
            method = "onUse",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/World;isClient:Z",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cf$onUseFromSpecificSide(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (CFSettings.eatCakeFromAllSidesFix && hit.getSide() != Direction.WEST) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
