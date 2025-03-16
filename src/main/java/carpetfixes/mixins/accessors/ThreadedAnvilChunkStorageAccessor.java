package carpetfixes.mixins.accessors;

import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerChunkLoadingManager.class)
public interface ThreadedAnvilChunkStorageAccessor {
    @Accessor("pointOfInterestStorage")
    PointOfInterestStorage getPoiStorage();
}