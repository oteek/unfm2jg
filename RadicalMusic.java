//package nfm.territories.music;

/**
 * The Interface RadicalMusic.
 */
public interface RadicalMusic {

    /**
     * Gets the type.
     *
     * @return the type
     */
    Type getType();

    /**
     * Pauses the file playback. Deprecated: Use setPaused instead.
     */
    @Deprecated
    void stop();

    /**
     * Resumes playback of the file. Deprecated: Use setPaused instead.
     */
    @Deprecated
    void resume();

    /**
     * Unloads the file and forcefully stops playback.
     */
    void unload();

    /**
     * Begin playback of the file.
     */
    void play();

    /**
     * Returns the paused state.
     *
     * @return true, if is paused
     */
    boolean isPaused();

    /**
     * Sets the paused state. Music may not immediately pause.
     *
     * @param pause the new paused
     */
    void setPaused(boolean pause);

    enum Type {
        TYPE_BASS, TYPE_OGG, TYPE_MP3, TYPE_MIDI, TYPE_MOD, TYPE_UNKNOWN
    }
}
