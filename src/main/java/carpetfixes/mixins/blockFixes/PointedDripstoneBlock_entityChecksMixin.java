package carpetfixes.mixins.blockFixes;

import carpetfixes.CFSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.Thickness;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.PointedDripstoneBlock.THICKNESS;
import static net.minecraft.block.PointedDripstoneBlock.VERTICAL_DIRECTION;

/**
 * Prevents you from being able to place dripstone within an entity
 */

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlock_entityChecksMixin extends Block {

    @Shadow
    @Nullable
    private static Direction getDirectionToPlaceAt(WorldView world, BlockPos pos, Direction direction) {
        return null;
    }

    @Shadow
    private static Thickness getThickness(WorldView world, BlockPos pos, Direction direction, boolean tryMerge) {
        return null;
    }

    public PointedDripstoneBlock_entityChecksMixin(Settings settings) {
        super(settings);
    }


    @Inject(
            method = "getPlacementState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;" +
                            "Ljava/lang/Comparable;)Ljava/lang/Object;",
                    ordinal = 2
            ),
            cancellable = true
    )
    private void cf$canPlaceDripstone(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        WorldAccess world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getVerticalPlayerLookDirection().getOpposite();
        Direction dir2 = getDirectionToPlaceAt(world, blockPos, direction);
        var thickness = getThickness(world, blockPos, dir2, !ctx.shouldCancelInteraction());

        if (CFSettings.dripstoneSkipsEntityCheckFix && thickness.ordinal() < 4) {
            BlockPos moveDir = blockPos.offset(dir2.getOpposite());
            if (world.getBlockState(moveDir).isOf(Blocks.POINTED_DRIPSTONE)) {
                BlockState replaceState = this.getDefaultState().with(VERTICAL_DIRECTION, dir2)
                        .with(THICKNESS, Thickness.values()[thickness.ordinal() + 1]);
                if (!world.canPlace(replaceState, moveDir, ShapeContext.absent())) {
                    cir.setReturnValue(null);
                    return;
                }
                moveDir = moveDir.offset(dir2.getOpposite());
                if (thickness.ordinal() < 3 && world.getBlockState(moveDir).isOf(Blocks.POINTED_DRIPSTONE)) {
                    replaceState = this.getDefaultState().with(VERTICAL_DIRECTION, dir2)
                            .with(THICKNESS, Thickness.values()[thickness.ordinal() + 2]);
                    if (!world.canPlace(replaceState, moveDir, ShapeContext.absent())) {
                        cir.setReturnValue(null);
                    }
                }
            }
        }
    }
}
