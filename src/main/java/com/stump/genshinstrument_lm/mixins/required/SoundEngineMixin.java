package com.stump.genshinstrument_lm.mixins.required;

import com.stump.genshinstrument_lm.sound.NoteSoundInstance;
import com.stump.genshinstrument_lm.sound.held.HeldNoteSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(
            method = "calculatePitch",
            at = @At("RETURN"),
            cancellable = true
    )
    private void genshinstrument_lm$expandNoteSoundPitch(
            SoundInstance sound,
            CallbackInfoReturnable<Float> cir
    ) {
        if (sound instanceof NoteSoundInstance || sound instanceof HeldNoteSoundInstance) {
            cir.setReturnValue(sound.getPitch());
        }
    }
}