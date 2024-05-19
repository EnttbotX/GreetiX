package x.Entt.GreetiX.utils;

import net.md_5.bungee.api.ChatColor;

public class MSGU {
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}