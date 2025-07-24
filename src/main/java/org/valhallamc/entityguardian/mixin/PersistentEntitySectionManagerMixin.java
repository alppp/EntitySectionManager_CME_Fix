package org.valhallamc.entityguardian.mixin;

import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin {

    @Redirect(
            method = {
                    "updateChunkStatus(Lnet/minecraft/world/level/ChunkPos;" +
                            "Lnet/minecraft/world/level/entity/Visibility;)V",
                    "lambda$updateChunkStatus$6"                         // outer lambda
            },
            at = @At(
                    value   = "INVOKE",
                    target  = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private static <T> void cme_fix$safeForEach(
            Stream<T> stream, Consumer<? super T> consumer) {

        // Snapshot first, then iterate
        java.util.List<T> snapshot = stream.toList();
        snapshot.forEach(consumer);
    }
}