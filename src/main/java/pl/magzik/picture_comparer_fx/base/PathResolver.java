package pl.magzik.picture_comparer_fx.base;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/* TODO: ADD JAVADOC */

public class PathResolver {

    private static class InstanceHolder {
        private static final PathResolver INSTANCE = new PathResolver();
    }

    public static PathResolver getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final Path configDirectory;
    private final Path logDirectory;
    private final Path dataDirectory;

    private static final String CONFIG_FOLDER = "config",
                                LOG_FOLDER = "logs",
                                DATA_FOLDER = "data";

    private static final String WIN_PATH = "AppData/Roaming/PictureComparerFX/",
                                MAC_PATH = "Library/Application Support/PictureComparerFX/",
                                LINUX_PATH = ".config/PictureComparerFX/";

    private PathResolver() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) throw new IllegalStateException("User home directory is not available.");

        String operatingSystem = System.getProperty("os.name").toLowerCase();
        Path applicationPath = getApplicationPath(userHome, operatingSystem);

        this.configDirectory = createDirectories(applicationPath, CONFIG_FOLDER);
        this.logDirectory = createDirectories(applicationPath, LOG_FOLDER);
        this.dataDirectory = createDirectories(applicationPath, DATA_FOLDER);
    }

    private @NotNull Path getApplicationPath(@NotNull String userHome, @NotNull String operatingSystem) {
        if (operatingSystem.contains("win")) return Paths.get(userHome, WIN_PATH);
        else if (operatingSystem.contains("mac")) return Paths.get(userHome, MAC_PATH);
        else return Paths.get(userHome, LINUX_PATH);
    }

    private @NotNull Path createDirectories(@NotNull Path base, @NotNull String folder) {
        Path folderPath = base.resolve(folder);
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new DirectoryCreationException("Failed to create directory: " + folderPath, e);
        }
        return folderPath;
    }

    public Path getConfigDirectory() {
        return configDirectory;
    }

    public Path getLogDirectory() {
        return logDirectory;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public static class DirectoryCreationException extends RuntimeException {
        public DirectoryCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
