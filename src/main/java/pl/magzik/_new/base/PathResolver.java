package pl.magzik._new.base;

import java.io.File;

public class PathResolver {

    private final File configDirectory;
    private final File logDirectory;

    private static final String CONFIG_FOLDER = "config",
                                LOG_FOLDER = "logs";

    private static final String WIN_PATH = "AppData/Roaming/PictureComparerFX/",
                                MAC_PATH = "Library/Application Support/PictureComparerFX/",
                                LINUX_PATH = ".config/PictureComparerFX/";

    public PathResolver(boolean portable) {
        if (portable) {
            File appDirectory = new File(System.getProperty("user.dir"));
            this.configDirectory = new File(appDirectory, CONFIG_FOLDER);
            this.logDirectory = new File(appDirectory, LOG_FOLDER);
        } else {
            String operatingSystem = System.getProperty("os.name").toLowerCase();
            String userHome = System.getProperty("user.home");

            if (operatingSystem.contains("win")) {
                this.configDirectory = new File(userHome, WIN_PATH + CONFIG_FOLDER);
                this.logDirectory = new File(userHome, WIN_PATH + LOG_FOLDER);
            } else if (operatingSystem.contains("mac")) {
                this.configDirectory = new File(userHome, MAC_PATH + CONFIG_FOLDER);
                this.logDirectory = new File(userHome, MAC_PATH + LOG_FOLDER);
            } else { // LINUX
                this.configDirectory = new File(userHome, LINUX_PATH + CONFIG_FOLDER);
                this.logDirectory = new File(userHome, LINUX_PATH + LOG_FOLDER);
            }

            if (!this.configDirectory.exists())
                this.configDirectory.mkdirs();
            if (!this.logDirectory.exists())
                this.logDirectory.mkdirs();
        }
    }

    public File getLogDirectory() {
        return logDirectory;
    }
}
