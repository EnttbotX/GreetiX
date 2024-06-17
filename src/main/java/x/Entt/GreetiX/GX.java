package x.Entt.GreetiX;

import x.Entt.GreetiX.cmds.CMDs;
import x.Entt.GreetiX.config.MCM;
import x.Entt.GreetiX.listeners.Listener;
import x.Entt.GreetiX.utils.MSGU;
import x.Entt.GreetiX.utils.UpdateLogger;
import x.Entt.GreetiX.utils.bStats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class GX extends JavaPlugin {
    private final int resourceId = 114316;
    private UpdateLogger updateChecker;
    public static String prefix = "&3&l[GreetiX] &f";
    private String version;
    private MCM mcm;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
        saveDefaultConfig();
        bStats stats = new bStats(this, 22097);

        Bukkit.getConsoleSender().sendMessage(MSGU.color(prefix + "&av" + version + " &2Enabled!"));

        registerCommands();
        registerEvents();
        registerFiles();
        updateCheck();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(MSGU.color(prefix + "&av" + version + " &cDisabled"));
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("greetix")).setExecutor(new CMDs(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listener(this), this);
    }

    private void registerFiles() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    private void updateCheck() {
        updateChecker = new UpdateLogger(this, resourceId);
        try {
            if (updateChecker.isUpdateAvailable()) {
                getLogger().info(MSGU.color(prefix + "&cThere is a new update of the plugin"));
            } else {
                getLogger().info(MSGU.color(prefix + "&2Plugin is up to date"));
            }
        } catch (Exception e) {
            getLogger().warning(MSGU.color(prefix + "&4&lError checking for updates: " + e.getMessage()));
        }
    }

    public MCM getMCM() {
        return mcm;
    }
}