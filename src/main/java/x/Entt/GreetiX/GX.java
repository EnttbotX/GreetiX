package x.Entt.GreetiX;

import x.Entt.GreetiX.cmds.MainCMD;
import x.Entt.GreetiX.listeners.Listener;
import x.Entt.GreetiX.config.MCM;
import x.Entt.GreetiX.utils.MSGU;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import x.Entt.GreetiX.utils.UpdateLogger;

import java.io.File;
import java.util.Objects;

public class GX extends JavaPlugin {
    public MCM mcm;
    private int resourceId = 114316;
    private UpdateLogger updateChecker;
    FileConfiguration config = getConfig();
    public static String prefix = "&3&l[GreetiX] &f";
    private final String version = getDescription().getVersion();

    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getConsoleSender().sendMessage
                (MSGU.color(prefix + "&av" + version + " &2Enabled!"));

        this.mcm = new MCM(this);

        registerCommands();
        registerEvents();
        registerFiles();
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage
                (MSGU.color(prefix + "&av" + version + " &cDisabled"));
    }

    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("greetix")).setExecutor(new MainCMD(this));
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listener(this), this);
    }

    public void registerFiles() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            config.options().copyDefaults(true);
        }
    }

    public void updateCheck() {
        updateChecker = new UpdateLogger(this, resourceId);

        try {
            if (updateChecker.isUpdateAvailable()) {
                getLogger().info(MSGU.color(prefix + "&cThere is an new update of the plugin"));
            } else {
                getLogger().info(MSGU.color(prefix + "&2Plugin updated"));
            }
        } catch (Exception e) {
            getLogger().warning(MSGU.color(prefix + "&4&lError searching newer versions: " + e.getMessage()));
        }
    }

    public MCM getMCM() {
        return mcm;
    }
}