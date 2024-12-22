package pl.magzik.picture_comparer_fx;

import pl.magzik.picture_comparer_fx.base.PathResolver;

public class Main {
    public static void main(String[] args) {
        PathResolver pathResolver = PathResolver.getInstance();
        System.setProperty("logPath", pathResolver.getLogDirectory().toString());

        PictureComparerFX.main(args);
    }
}
