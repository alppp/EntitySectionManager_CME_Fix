package org.valhallamc.cme_fix.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.entity.PersistentEntitySectionManager.Watcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Set;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerMixin {

    @Shadow
    @Final
    private Object2ObjectOpenHashMap<Long, Set<Watcher>> sectionWatchers;

    @Inject(method = "updateChunkStatus", at = @At("HEAD"), cancellable = true)
    private void fixConcurrentModificationException(CallbackInfo ci) {
        // Replace the unsafe forEach + remove pattern with a safe iterator
        // This prevents ConcurrentModificationException when removing entries during iteration
        for (Iterator<Object2ObjectOpenHashMap.Entry<Long, Set<Watcher>>> it = sectionWatchers.object2ObjectEntrySet().fastIterator(); it.hasNext(); ) {
            Object2ObjectOpenHashMap.Entry<Long, Set<Watcher>> entry = it.next();
            // Update all watchers in this section
            entry.getValue().forEach(Watcher::update);
            // Safely remove empty watcher sets
            if (entry.getValue().isEmpty()) {
                it.remove();
            }
        }
        // Cancel the original method since we've handled it
        ci.cancel();
    }
}