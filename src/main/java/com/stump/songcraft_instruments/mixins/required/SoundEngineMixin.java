package com.stump.songcraft_instruments.mixins.required;

import com.stump.songcraft_instruments.sound.NoteSoundInstance;
import com.stump.songcraft_instruments.sound.held.HeldNoteSoundInstance;
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
    private void songcraft_instruments$expandNoteSoundPitch(
            SoundInstance sound,
            CallbackInfoReturnable<Float> cir
    ) {
        if (sound instanceof NoteSoundInstance || sound instanceof HeldNoteSoundInstance) {
            cir.setReturnValue(sound.getPitch());
        }
    }
}