//package nfm.territories.music;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SuperClip.java

//import nfm.territories.GameSparker;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;

/**
 * The Class SuperClip.
 */
class SuperClip implements Runnable {

    int stoped;
    ByteArrayInputStream stream;
    int rollBackPos;
    int rollBackTrig;
    private int skiprate;
    private Thread cliper;
    private SourceDataLine source;

    /**
     * Instantiates a new super clip.
     *
     * @param abyte0 the abyte0
     * @param i      the i
     * @param j      the j
     */
    SuperClip(final byte abyte0[], final int i, final int j) {
        skiprate = 0;
        stoped = 1;
        source = null;
        rollBackPos = 0;
        rollBackTrig = 0;
        // changeGain = false;
        stoped = 2;
        skiprate = j;
        stream = new ByteArrayInputStream(abyte0, 0, i);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            final AudioFormat audioformat = new AudioFormat(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                    skiprate, 16, 1, 2, skiprate, false);
            final javax.sound.sampled.DataLine.Info info = new javax.sound.sampled.DataLine.Info(SourceDataLine.class,
                    audioformat);
            source = (SourceDataLine) AudioSystem.getLine(info);
            source.open(audioformat);
            source.start();
        } catch (final Exception exception) {
            stoped = 1;
        }
        boolean flag = false;
        while (stoped == 0) {
            try {
                final int i = skiprate;
                int j = stream.available();
                if (j % 2 != 0) {
                    j++;
                }
                byte abyte0[] = new byte[j <= i ? j : i];
                final int l = stream.read(abyte0, 0, abyte0.length);
                if (l == -1 || rollBackPos != 0 && j < rollBackTrig) {
                    flag = true;
                }
                if (flag) {
                    if (l != -1) {
                        source.write(abyte0, 0, abyte0.length);
                    }
                    stream.reset();
                    if (rollBackPos != 0) {
                        stream.skip(rollBackPos);
                    }
                    int k = stream.available();
                    if (k % 2 != 0) {
                        k++;
                    }
                    abyte0 = new byte[k <= i ? k : i];
                    stream.read(abyte0, 0, abyte0.length);
                    flag = false;
                }
                source.write(abyte0, 0, abyte0.length);
            } catch (final Exception exception1) {
                if (GameSparker.DEBUG) {
                    System.out.println("Play error: " + exception1);
                }
                stoped = 1;
            }
            try {
                Thread.sleep(200L);
            } catch (final InterruptedException interruptedexception) {
            }
        }
        source.stop();
        source.close();
        source = null;
        stoped = 2;
    }

    /**
     * Play.
     */
    public void play() {
        if (stoped == 2) {
            stoped = 0;
            try {
                stream.reset();
            } catch (final Exception exception) {
            }
            cliper = new Thread(this);
            cliper.start();
        }
    }

    /**
     * Resume.
     */
    public void resume() {
        if (stoped == 2) {
            stoped = 0;
            cliper = new Thread(this);
            cliper.start();
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        if (stoped == 0) {
            stoped = 1;
            if (source != null) {
                source.stop();
            }
        }
    }

    /**
     * Close.
     */
    public void close() {
        try {
            stream.close();
            stream = null;
        } catch (final Exception exception) {
        }
    }
    // Removed unused code found by UCDetector
    // private boolean changeGain;
}
