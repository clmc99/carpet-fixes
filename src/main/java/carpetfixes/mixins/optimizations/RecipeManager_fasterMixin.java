package carpetfixes.mixins.optimizations;

import carpetfixes.CFSettings;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * RecipeManager optimization.
 * Optimized by taking out streams & doing extra early checks to quickly remove unrelated recipes
 */

@Mixin(RecipeManager.class)
public abstract class RecipeManager_fasterMixin {

    @Shadow protected abstract <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeEntry<T>> getAllOfType(RecipeType<T> type);

    @Inject(
            method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/input/RecipeInput;Lnet/minecraft/world/World;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private <C extends RecipeInput, T extends Recipe<C>> void cf$getOptimizedFirstMatch(
            RecipeType<T> type, C inventory, World world, CallbackInfoReturnable<Optional<RecipeEntry<T>>> cir
    ) {
        Inventory inv = (Inventory)inventory;
        if (CFSettings.optimizedRecipeManager && type == RecipeType.CRAFTING) {
            int slots = 0;
            int count;
            //compare size to quickly remove recipes that are not even close. Plus remove streams
            for (int slot = 0; slot < inv.size(); slot++)
                if (!inv.getStack(slot).isEmpty()) {
                    slots++;
                }
            for (var recipe : this.getAllOfType(type)) {
                count = 0;
                if (recipe.value() instanceof SpecialCraftingRecipe) {
                    if (recipe.value().matches(inventory, world)) {
                        cir.setReturnValue(Optional.of(recipe));
                        return;
                    }
                } else {
                    for (Ingredient ingredient : recipe.value().getIngredients())
                        if (ingredient != Ingredient.EMPTY) {
                            count++;
                        }
                    if (count == slots && recipe.value().matches(inventory, world)) {
                        cir.setReturnValue(Optional.of(recipe));
                        return;
                    }
                }
            }
            cir.setReturnValue(Optional.empty());
        }
    }


    @Inject(
            method = "listAllOfType(Lnet/minecraft/recipe/RecipeType;)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true
    )
    private <C extends RecipeInput, T extends Recipe<C>> void cf$getOptimizedListAllOfType(
            RecipeType<T> type,
            CallbackInfoReturnable<List<RecipeEntry<T>>> cir
    ) {
        if (CFSettings.optimizedRecipeManager) { //Remove streams
            cir.setReturnValue(new ArrayList<>(this.getAllOfType(type)));
        }
    }
}
