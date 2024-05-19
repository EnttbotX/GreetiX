package x.Entt.GreetiX.listeners;

import x.Entt.GreetiX.GX;
import x.Entt.GreetiX.config.MCM;
import x.Entt.GreetiX.utils.MSGU;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listener implements org.bukkit.event.Listener {
    private final GX plugin;
    private final Map<String, Long> playerCooldowns;
    private final MCM mcm;

    public Listener(GX plugin) {
        this.plugin = plugin;
        this.playerCooldowns = new HashMap<>();
        this.mcm = plugin.getMCM();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ConfigurationSection messagesSection = config.getConfigurationSection("messages");
            if (messagesSection == null) return;

            for (String key : messagesSection.getKeys(false)) {
                ConfigurationSection messageSection = messagesSection.getConfigurationSection(key);
                if (messageSection == null) continue;

                if (messageSection.getBoolean("enabled")) {
                    String type = messageSection.getString("type");
                    if (type == null) continue;

                    if (type.equalsIgnoreCase("on_join")) {
                        sendMessage(player, messageSection);
                    } else if (type.equalsIgnoreCase("repetitive")) {
                        boolean useCooldown = config.getBoolean("messages." + key + ".cooldown");
                        long cooldown = useCooldown ? getCooldownTicks(messageSection.getString("cooldown_time")) : 0;
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            plugin.reloadConfig();
                            if (!useCooldown || !playerCooldowns.containsKey(player.getName()) ||
                                    System.currentTimeMillis() - playerCooldowns.get(player.getName()) >= cooldown) {
                                sendMessage(player, messageSection);
                                playerCooldowns.put(player.getName(), System.currentTimeMillis());
                            }
                        }, 0L, cooldown);
                    }
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerCooldowns.remove(player.getName());
    }

    private void sendMessage(Player player, ConfigurationSection messageSection) {
        if (messageSection == null || !messageSection.getBoolean("enabled")) {
            return;
        }

        List<String> messages = messageSection.getStringList("message");
        for (String message : messages) {
            player.sendMessage(MSGU.color(message));
        }
    }

    private long getCooldownTicks(String cooldown) {
        if (cooldown == null || cooldown.isEmpty()) return 0;

        String unit = cooldown.substring(cooldown.length() - 1);
        int time = Integer.parseInt(cooldown.substring(0, cooldown.length() - 1));
        switch (unit.toLowerCase()) {
            case "s":
                return time * 20;
            case "mi":
                return time * 1200;
            case "m":
                return time * 1200 * 60;
            case "h":
                return time * 1200 * 60 * 60;
            case "d":
                return time * 1200 * 60 * 60 * 24;
            default:
                return 0;
        }
    }
}