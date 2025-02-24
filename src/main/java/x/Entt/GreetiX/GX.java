package x.Entt.GreetiX;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import x.Entt.GreetiX.CMDs.CMDs;
import x.Entt.GreetiX.Utils.FileHandler;
import x.Entt.GreetiX.Events.Events;
import x.Entt.GreetiX.Utils.MSG;
import x.Entt.GreetiX.Utils.Updater;
import x.Entt.GreetiX.Utils.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public class GX extends JavaPlugin {
    private final String version = getDescription().getVersion();;
    public static String prefix;
    private Metrics metrics;
    private FileHandler fh;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        metrics = new Metrics(this, 22097);
        fh = new FileHandler(this);

        if (fh.getConfig().getString("prefix") == null) {
            prefix  = "&3&l[GreetiX] &f";
        } else {
            prefix = fh.getConfig().getString("prefix", "&3&l[GreetiX] &f");
        }

        registerCommands();
        registerEvents();
        registerFiles();

        if (fh.getConfig().getBoolean("update-log", true)) {
            searchUpdates();
        }

        Bukkit.getConsoleSender().sendMessage(MSG.color(prefix + "&av" + version + " &2Enabled!"));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(MSG.color(prefix + "&av" + version + " &cDisabled"));
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("greetix")).setExecutor(new CMDs(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    private void registerFiles() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    public void searchUpdates() {
        String downloadUrl = "https://www.spigotmc.org/resources/greetix-join-event-handler-1-8-1-21.116849/";
        TextComponent link = new TextComponent(MSG.color("&e&lClick here to download the update!"));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));

        boolean updateAvailable = false;
        String latestVersion = "unknown";

        try {
            Updater updater = new Updater(this, 116849);
            updateAvailable = updater.isUpdateAvailable();
            latestVersion = updater.getLatestVersion();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(MSG.color(prefix + "&cError checking for updates: " + e.getMessage()));
        }

        if (updateAvailable) {
            Bukkit.getConsoleSender().sendMessage(MSG.color("&2&l============= " + prefix + "&2&l============="));
            Bukkit.getConsoleSender().sendMessage(MSG.color("&6&lNEW VERSION AVAILABLE!"));
            Bukkit.getConsoleSender().sendMessage(MSG.color("&e&lCurrent Version: &f" + version));
            Bukkit.getConsoleSender().sendMessage(MSG.color("&e&lLatest Version: &f" + latestVersion));
            Bukkit.getConsoleSender().sendMessage(MSG.color("&e&lDownload it here: &f" + downloadUrl));
            Bukkit.getConsoleSender().sendMessage(MSG.color("&2&l============= " + prefix + "&2&l============="));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("greetix.updatelog")) {
                    player.sendMessage(MSG.color(prefix + "&e&lA new plugin update is available!"));
                    player.spigot().sendMessage(link);
                }
            }
        }
    }

    public FileHandler getFH() {
        return fh;
    }
}