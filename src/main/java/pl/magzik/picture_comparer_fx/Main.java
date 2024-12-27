package pl.magzik.picture_comparer_fx;

import pl.magzik.picture_comparer_fx.base.PathResolver;

/**
 * Main launcher class for the PictureComparerFX application.
 * <p>
 *     This class serves as a wrapper for launching the application.
 *     Its primary purpose is to circumvent issues related to module usage (at least for now).
 * </p>
 * */
public class Main {
    public static void main(String[] args) {
        PathResolver pathResolver = PathResolver.getInstance();
        System.setProperty("logPath", pathResolver.getLogDirectory().toString());

        PictureComparerFX.main(args);
    }
}
