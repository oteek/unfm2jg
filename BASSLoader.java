//package nfm.territories.music;

import jouvieje.bass.BassInit;
import jouvieje.bass.exceptions.BassException;
//import nfm.territories.GameSparker;

import java.lang.reflect.Field;

/**
 * The Class BASSLoader. This class loads the BASS library. <br>
 * <br>
 * This is a utility class, so it can't be inherited.
 */
public final class BASSLoader {

    /**
     * Don't let anyone instantiate this class.
     */
    private BASSLoader() {

    }

    /**
     * Initialize NativeBASS. This method loads all necessary DLLs by modifying
     * the java.library.path property.<br>
     * Yes, it's a dirty hack. But it works.
     */
    public static void initializeBASS() {

        // System.out.println("%PATH% is " +
        // System.getProperty("java.library.path"));

        try {
            if (GameSparker.IS_UNIX) {
                appendToPath(GameSparker.WORKING_DIRECTORY + "/lib/dlls/linux" + GameSparker.IS_64_BIT + "/");
            } else if (GameSparker.IS_MAC) {
                appendToPath(GameSparker.WORKING_DIRECTORY + "/lib/dlls/mac/");
            } else if (GameSparker.IS_WINDOWS) {
                appendToPath(GameSparker.WORKING_DIRECTORY + "\\lib\\dlls\\win" + GameSparker.IS_64_BIT + "\\");
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            /**/
        }

        init();
    }

    // private static final String workingDirectory =
    // System.getProperty("user.dir");

    /**
     * Append to path.
     *
     * @param s the s
     * @throws NoSuchFieldException     the no such field exception
     * @throws SecurityException        the security exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException   the illegal access exception
     */
    private static void appendToPath(final String s)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        System.setProperty("java.library.path",
                System.getProperty("java.library.path") + (GameSparker.IS_WINDOWS ? ";" : ":") + s);

        final Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }

    private static void init() {
		/*
		 * NativeBass Init
		 */
        try {
            BassInit.DEBUG = GameSparker.DEBUG;
            BassInit.loadLibraries();
        } catch (final BassException e) {
            printfExit("NativeBass error! %s\n", e.getMessage());
            return;
        }

		/*
		 * Checking NativeBass version
		 */
        if (BassInit.NATIVEBASS_LIBRARY_VERSION() != BassInit.NATIVEBASS_JAR_VERSION()) {
            printfExit("Error!  NativeBass library version (%08x) is different to jar version (%08x)\n",
                    BassInit.NATIVEBASS_LIBRARY_VERSION(), BassInit.NATIVEBASS_JAR_VERSION());
            return;
        }

		/* ================================================== */

        RadicalBASS.init = true;
    }

    /**
     * Printf exit.
     *
     * @param format the format
     * @param args   the args
     */
    private static final void printfExit(final String format, final Object... args) {
        final String s = String.format(format, args);
        System.out.println(s);
    }
}
