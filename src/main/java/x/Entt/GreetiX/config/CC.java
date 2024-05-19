package x.Entt.GreetiX.config;

import x.Entt.GreetiX.GX;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CC {
    private GX plugin;
    private String fileName;
    private FileConfiguration fileConfiguration = null;
    private File file = null;
    private String folderName;

    public CC(String fileName, String folderName, GX plugin) {
        this.fileName = fileName;
        this.folderName = folderName;
        this.plugin = plugin;
    }

    public void registerConfig() {
        file = new File(getFilePath());

        if (!file.exists()) {
            plugin.saveResource(getResourcePath(), false);
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            reloadConfig();
        }
        return fileConfiguration;
    }

    public void reloadConfig() {
        file = new File(getFilePath());
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    private String getResourcePath() {
        if (folderName != null) {
            return folderName + File.separator + fileName;
        } else {
            return fileName;
        }
    }

    private String getFilePath() {
        if (folderName != null) {
            return plugin.getDataFolder() + File.separator + folderName + File.separator + fileName;
        } else {
            return plugin.getDataFolder() + File.separator + fileName;
        }
    }
}