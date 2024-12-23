package pl.magzik.picture_comparer_fx.base;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A utility class for resolving and managing application directories, including configuration, logs, and data directories.
 * <p>
 * This class provides methods to retrieve paths for important application directories such as:
 * <ul>
 *     <li>Configuration directory</li>
 *     <li>Log directory</li>
 *     <li>Data directory</li>
 * </ul>
 * It automatically determines the correct directory paths based on the user's operating system (Windows, macOS, or Linux).
 * </p>
 * <p>
 * The directories are created if they do not already exist, and are stored for later use by the application.
 * </p>
 */
public class PathResolver {

    private static class InstanceHolder {
        private static final PathResolver INSTANCE = new PathResolver();
    }

    /**
     * Returns the singleton instance of the {@link PathResolver} class.
     *
     * @return the singleton {@link PathResolver} instance
     */
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

    /**
     * Private constructor that initializes the application directories.
     * It detects the user's home directory and operating system, then creates the necessary directories.
     *
     * @throws IllegalStateException if the user home directory is not available
     */
    private PathResolver() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) throw new IllegalStateException("User home directory is not available.");

        String operatingSystem = System.getProperty("os.name").toLowerCase();
        Path applicationPath = getApplicationPath(userHome, operatingSystem);

        this.configDirectory = createDirectories(applicationPath, CONFIG_FOLDER);
        this.logDirectory = createDirectories(applicationPath, LOG_FOLDER);
        this.dataDirectory = createDirectories(applicationPath, DATA_FOLDER);
    }

    /**
     * Determines the application path based on the user's home directory and operating system.
     *
     * @param userHome the user's home directory path
     * @param operatingSystem the name of the user's operating system
     * @return the determined application path
     */
    private @NotNull Path getApplicationPath(@NotNull String userHome, @NotNull String operatingSystem) {
        if (operatingSystem.contains("win")) return Paths.get(userHome, WIN_PATH);
        else if (operatingSystem.contains("mac")) return Paths.get(userHome, MAC_PATH);
        else return Paths.get(userHome, LINUX_PATH);
    }

    /**
     * Creates the specified directory and its parent directories if they do not exist.
     *
     * @param base the base path where the directory will be created
     * @param folder the name of the folder to create
     * @return the path to the created directory
     * @throws DirectoryCreationException if an error occurs while creating the directory
     */
    private @NotNull Path createDirectories(@NotNull Path base, @NotNull String folder) {
        Path folderPath = base.resolve(folder);
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new DirectoryCreationException("Failed to create directory: " + folderPath, e);
        }
        return folderPath;
    }

    /**
     * Returns the path to the configuration directory.
     *
     * @return the path to the configuration directory
     */
    public Path getConfigDirectory() {
        return configDirectory;
    }

    /**
     * Returns the path to the log directory.
     *
     * @return the path to the log directory
     */
    public Path getLogDirectory() {
        return logDirectory;
    }

    /**
     * Returns the path to the data directory.
     *
     * @return the path to the data directory
     */
    public Path getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Exception thrown when there is a failure in creating a directory.
     */
    public static class DirectoryCreationException extends RuntimeException {
        public DirectoryCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
