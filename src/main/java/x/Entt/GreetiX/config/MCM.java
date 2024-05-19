package x.Entt.GreetiX.config;

import x.Entt.GreetiX.GX;

public class MCM {

    private final GX plugin;
    private final CC configFile;

    public MCM(GX plugin) {
        this.plugin = plugin;
        configFile = new CC("config.yml", null, plugin);
        configFile.registerConfig();
        loadConfig();
    }

    public void loadConfig() {
        configFile.getConfig();
    }

    public void reloadConfig() {
        configFile.reloadConfig();
        loadConfig();
    }
}