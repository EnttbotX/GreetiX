package x.Entt.GreetiX.Utils;

import x.Entt.GreetiX.GX;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class FileHandler {
    private final GX plugin;
    private FileConfiguration fileConfiguration;
    private File file;

    public FileHandler(GX plugin) {
        this.plugin = plugin;
        registerConfig();
    }

    private void registerConfig() {
        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource("config.yml", false);
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            loadConfig();
        }
        return fileConfiguration;
    }

    public void loadConfig() {
        file = new File(plugin.getDataFolder(), "config.yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        if (fileConfiguration == null || file == null) return;
        try {
            fileConfiguration.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Cannot save config!: " + e.getMessage());
        }
    }
}