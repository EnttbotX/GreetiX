package x.Entt.GreetiX.Events;

import x.Entt.GreetiX.GX;
import x.Entt.GreetiX.Utils.FileHandler;
import x.Entt.GreetiX.Utils.MSG;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static x.Entt.GreetiX.GX.prefix;

public class Events implements Listener {
    private final GX plugin;
    private final Map<String, Map<String, Long>> playerCooldowns;

    public Events(GX plugin) {
        this.plugin = plugin;
        this.playerCooldowns = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FileHandler fh = plugin.getFH();
        FileConfiguration config = fh.getConfig();
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ConfigurationSection messagesSection = config.getConfigurationSection("messages");
            if (messagesSection == null) return;

            for (String key : messagesSection.getKeys(false)) {
                ConfigurationSection messageSection = messagesSection.getConfigurationSection(key);
                if (messageSection == null || !messageSection.getBoolean("enabled", false)) continue;

                String type = messageSection.getString("type", "");
                boolean cooldownEnabled = messageSection.getBoolean("cooldown", false);
                long cooldownMillis = getCooldownMillis(messageSection.getString("cooldown_time"));

                if (type.equalsIgnoreCase("on_first_join")) {
                    if (!player.hasPlayedBefore() && (!cooldownEnabled || checkCooldown(player.getName(), key, cooldownMillis))) {
                        processActions(player, messageSection);
                    }
                }

                else if (type.equalsIgnoreCase("on_join")) {
                    if (!cooldownEnabled || checkCooldown(player.getName(), key, cooldownMillis)) {
                        processActions(player, messageSection);
                    }
                }

                else if (type.equalsIgnoreCase("repetitive")) {
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        if (!cooldownEnabled || checkCooldown(player.getName(), key, cooldownMillis)) {
                            processActions(player, messageSection);
                        }
                    }, 0L, cooldownMillis > 0 ? cooldownMillis / 50 : 20L);
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FileHandler fh = plugin.getFH();
        FileConfiguration config = fh.getConfig();
        String playerName = player.getName();

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                ConfigurationSection messageSection = messagesSection.getConfigurationSection(key);
                if (messageSection == null || !messageSection.getBoolean("enabled", false)) continue;

                String type = messageSection.getString("type", "");
                boolean cooldownEnabled = messageSection.getBoolean("cooldown", false);
                long cooldownMillis = getCooldownMillis(messageSection.getString("cooldown_time"));

                if (type.equalsIgnoreCase("on_leave")) {
                    if (!cooldownEnabled || checkCooldown(playerName, key, cooldownMillis)) {
                        processActions(player, messageSection);
                    }
                }
            }
        }

        playerCooldowns.remove(playerName);
    }

    private void processActions(Player player, ConfigurationSection messageSection) {
        ConfigurationSection actionsSection = messageSection.getConfigurationSection("actions");
        if (actionsSection == null) return;

        for (String actionKey : actionsSection.getKeys(false)) {
            List<String> actions = actionsSection.getStringList(actionKey);

            if (actionKey.equalsIgnoreCase("message")) {
                for (String msg : actions) {
                    player.sendMessage(MSG.color(prefix + msg));
                }
            }

            else if (actionKey.equalsIgnoreCase("message_for_all")) {
                for (String msg : actions) {
                    Bukkit.broadcastMessage(MSG.color(prefix + msg));
                }
            }

            else if (actionKey.toLowerCase().startsWith("message_for_permission_")) {
                String permission = actionKey.substring("message_for_permission_".length());

                permission = permission.replace("$", "");
                for (String msg : actions) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.hasPermission(permission)) {
                            p.sendMessage(MSG.color(prefix + msg));
                        }
                    }
                }
            }

            else if (actionKey.equalsIgnoreCase("command")) {
                for (String cmd : actions) {
                    player.performCommand(cmd);
                }
            }

            else if (actionKey.equalsIgnoreCase("console_command")) {
                ConsoleCommandSender console = Bukkit.getConsoleSender();
                for (String cmd : actions) {
                    Bukkit.dispatchCommand(console, cmd);
                }
            }
        }
    }

    private boolean checkCooldown(String playerName, String messageKey, long cooldownMillis) {
        Map<String, Long> cooldowns = playerCooldowns.computeIfAbsent(playerName, k -> new HashMap<>());
        long last = cooldowns.getOrDefault(messageKey, 0L);
        long now = System.currentTimeMillis();
        if (now - last >= cooldownMillis) {
            cooldowns.put(messageKey, now);
            return true;
        }
        return false;
    }

    private long getCooldownMillis(String cooldown) {
        if (cooldown == null || cooldown.isEmpty()) return 0;
        String numberPart = cooldown.replaceAll("[^0-9]", "");
        String unit = cooldown.replaceAll("[0-9]", "");
        int time;
        try {
            time = Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            return 0;
        }
        return switch (unit.toLowerCase()) {
            case "s" -> time * 1000L;
            case "mi" -> time * 60 * 1000L;
            case "m" -> time * 30L * 24 * 60 * 60 * 1000;
            case "w" -> time * 7L * 24 * 60 * 60 * 1000;
            case "y" -> time * 365L * 24 * 60 * 60 * 1000;
            default -> 0;
        };
    }
}