package com.stump.genshinstrument_lm.sound.held;

import com.stump.genshinstrument_lm.client.ClientInstrumentData;
import com.stump.genshinstrument_lm.client.config.ModClientConfigs;
import com.stump.genshinstrument_lm.client.util.ClientUtil;
import com.stump.genshinstrument_lm.particle.ModParticles;
import com.stump.genshinstrument_lm.sound.NoteSound;
import com.stump.genshinstrument_lm.sound.held.HeldNoteSound.Phase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class HeldNoteSoundInstance extends AbstractTickableSoundInstance {
    public final HeldNoteSound heldSoundContainer;
    public final HeldNoteSound.Phase phase;
    private int particleTimer = 10;

    public final ResourceLocation instrumentId;

    public final Optional<Entity> initiator;
    public final InitiatorID initiatorId;

    /**
     * The origin of the sound. May be empty
     * for the initiator's position.
     */
    public final Optional<BlockPos> soundOrigin;
    public final int notePitch;
    private final float startVolume;

    private boolean released;

    /**
     * @param initiator The initiator of the sound. Empty for a non-player initiator.
     *                  Value must be present if {@code soundOrigin} is empty.
     * @param soundOrigin The block position of where the sound was originated from.
     *                    Value must be present if {@code initiator} is empty.
     */
    protected HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                    int notePitch, float startVolume, float volume,
                                    @Nullable Entity initiator, @Nullable BlockPos soundOrigin,
                                    InitiatorID initiatorId, ResourceLocation instrumentId,
                                    int timeAlive, boolean released) {
        super(
            heldSoundContainer.getSound(phase).getByPreference(distFromSourceSqr(soundOrigin, initiator)),
            NoteSound.INSTRUMENT_SOUND_SOURCE,
            SoundInstance.createUnseededRandom()
        );

        this.initiatorId = initiatorId;
        this.instrumentId = instrumentId;

        this.heldSoundContainer = heldSoundContainer;
        this.phase = phase;
        this.overallTimeAlive = timeAlive;

        this.initiator = Optional.ofNullable(initiator);
        this.soundOrigin = Optional.ofNullable(soundOrigin);

        this.startVolume = startVolume;
        this.volume = volume;
        this.notePitch = notePitch;
        this.pitch = NoteSound.getPitchByNoteOffset(notePitch);

        this.released = released;


        if (distFromSourceSqr() < Mth.square(NoteSound.LOCAL_RANGE)) {
            // Very close; play relative
            attenuation = Attenuation.NONE;
            relative = true;
            x = y = z = 0;
        } else {
            // Not close; play local
            attenuation = Attenuation.LINEAR;
            relative = false;

            this.soundOrigin.ifPresentOrElse(
                (loc) -> {
                    x = loc.getX();
                    y = loc.getY();
                    z = loc.getZ();
                },
                this::toInitiatorPos
            );
        }
    }

    /**
     * A held note sound instance for 3rd party trigger
     * @param initiator The initiator of the sound. Empty for a non-player initiator.
     *                  Value must be present if {@code soundOrigin} is empty.
     * @param soundOrigin The block position of where the sound was originated from.
     *                    Value must be present if {@code initiator} is empty.
     */
    public HeldNoteSoundInstance(HeldNoteSound heldSoundContainer, HeldNoteSound.Phase phase,
                                 int notePitch, float startVolume, float volume,
                                 @Nullable Entity initiator, @Nullable BlockPos soundOrigin,
                                 InitiatorID initiatorId, ResourceLocation instrumentId) {
        this(
            heldSoundContainer,
            phase, notePitch, startVolume, volume,
            initiator, soundOrigin, initiatorId, instrumentId,
            0, false
        );
    }


    public void queueAndAddInstance() {
        Minecraft.getInstance().getSoundManager().queueTickingSound(this);
        ClientUtil.stopMusicIfClose(
            soundOrigin.orElseGet(initiator.map(Entity::blockPosition)::get)
        );
        addSoundInstance();
    }

    /**
     * Adds a new held sound to the cached held sounds.
     * Its identifier will either be the initiator's UUID
     * or the block position string.
     */
    public void addSoundInstance() {
        HeldNoteSounds.put(initiatorId, heldSoundContainer, notePitch, this);
    }
    protected void removeSoundInstance() {
        HeldNoteSounds.release(initiatorId, heldSoundContainer, notePitch, this);
    }

    /**
     * Marks this held sound as being released
     */
    public void setReleased() {
        if (released)
            return;

        this.released = true;

        // Play release sound, if applicable.
        // Only a 'hold' sound type may play a release.
        if (phase == Phase.HOLD) {

            if (heldSoundContainer.release() != null) {
                final Vec3 pos = getSourcePos();

                heldSoundContainer.release().playLocally(
                    pitch, volume,
                    new BlockPos((int) pos.x, (int) pos.y, (int) pos.z)
                );
            }

        }
    }
    public boolean isReleased() {
        return released;
    }


    protected static double distFromSourceSqr(@Nullable BlockPos soundOrigin, @Nullable Entity initiator) {
        return Minecraft.getInstance().player.position().distanceToSqr(getSourcePos(soundOrigin, initiator));
    }
    public double distFromSourceSqr() {
        return Minecraft.getInstance().player.position().distanceToSqr(getSourcePos());
    }

    protected static Vec3 getSourcePos(@Nullable BlockPos soundOrigin, @Nullable Entity initiator) {
        return (soundOrigin == null) ? initiator.position() : soundOrigin.getCenter();
    }
    protected Vec3 getSourcePos() {
        return getSourcePos(soundOrigin.orElse(null), initiator.orElse(null));
    }


    protected int timeAlive = 0, overallTimeAlive;
    @Override
    public void tick() {
        toInitiatorPos();

        handleChainHolding();

        if (released) {
            float fadeOutMultiplier = 1;
            float fhft = heldSoundContainer.fullHoldFadeoutTime() * 20;

            // Lesser the significance of hold in the first FULL_HOLD_FADE_OUT_TIME ticks
            // Basically fade in the fade out
            if ((phase == Phase.HOLD) && (fhft != 0)) {
                if (overallTimeAlive < fhft) {
                    fadeOutMultiplier = 1 / ((overallTimeAlive + 1) / fhft);
                }
            }

            volume -= heldSoundContainer.releaseFadeOut() * fadeOutMultiplier;
            if (volume <= 0)
                stopHeld();
        } else {
            if (phase == Phase.HOLD) {
                if (++particleTimer >= 10) {
                    particleTimer = 0;
                    spawnNoteParticle();
                }
            }
        }

        timeAlive++;
        overallTimeAlive++;
    }

    protected boolean chainedHolding = false;
    protected void handleChainHolding() {
        if (chainedHolding || (pitch == 0)) // if, for some reason, ig
            return;

        switch (phase) {
            case ATTACK: {
                // Attack wants to chain the first hold:
                if (timeAlive == (int)(heldSoundContainer.holdDelay() * 20)) {
                    queueHoldPhase(false);
                    chainedHolding = true;
                }
                break;
            }
            case HOLD: {
                // Hold wants to chain the next hold:
                if ((timeAlive * pitch) >= (int)((heldSoundContainer.holdDuration() + heldSoundContainer.chainedHoldDelay()) * 20)) {
                    queueHoldPhase(heldSoundContainer.decay() > 0);
                    chainedHolding = true;

                    // We now don't need to cache it anymore.
                    removeSoundInstance();
                }
                break;
            }
        }
    }

    protected void queueHoldPhase(final boolean decreaseVol) {
        float decay = decreaseVol ? (heldSoundContainer.decay() * startVolume) : 0;
        float nextVolume = volume - decay;
        if (nextVolume <= 0)
            return;

        new HeldNoteSoundInstance(
            heldSoundContainer, Phase.HOLD, notePitch, startVolume, nextVolume,
            initiator.orElse(null), soundOrigin.orElse(null),
            initiatorId, instrumentId,
            overallTimeAlive, released
        ).queueAndAddInstance();
    }

    protected void toInitiatorPos() {
        if (relative)
            return;
        if (soundOrigin.isPresent() || initiator.isEmpty())
            return;
        // "Blown air" at the same location
        if (released)
            return;

        x = initiator.get().getX();
        y = initiator.get().getY();
        z = initiator.get().getZ();
    }

    // We don't want to randomly distort this stuff unlike the parent
    @Override
    public float getVolume() {
        return volume;
    }
    @Override
    public float getPitch() {
        return pitch;
    }

    // For some reason 'stop' is final...
    public void stopHeld() {
        stop();
        removeSoundInstance();
    }

    private void spawnNoteParticle() {
        if (initiator.isEmpty())
            return;
        Entity entity = initiator.get();
        var level = Minecraft.getInstance().level;
        if (level == null)
            return;

        double noteIndex = heldSoundContainer.index() + notePitch;
        final double MIN_NOTE = -12;
        final double MAX_NOTE = 30; // should be 32, but color sets of 6 align better with octaves this way
        double particleColor = (noteIndex - MIN_NOTE) / (MAX_NOTE - MIN_NOTE);
        particleColor = net.minecraft.util.Mth.clamp(particleColor, 0.0, 1.0);

        double xOffset = (level.random.nextDouble() - 0.5) * 0.30;
        double yOffset = (level.random.nextDouble() - 0.5) * 0.30;
        double zOffset = (level.random.nextDouble() - 0.5) * 0.30;

        float bodyYaw = entity.getYRot(); // body rotation in degrees
        double radians = Math.toRadians(bodyYaw);
        double forwardX = -Math.sin(radians);
        double forwardZ = Math.cos(radians);

        int colorSet = ClientInstrumentData.getParticleSet(entity.getUUID());

        level.addParticle(
                ModParticles.CUSTOM_NOTE.get(),
                entity.getX() + forwardX * 0.6 + xOffset,
                entity.getY() + 1.3 + yOffset,
                entity.getZ() + forwardZ * 0.6 + zOffset,
                particleColor,
                0.15,
                colorSet
        );
    }
}
