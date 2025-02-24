package x.Entt.GreetiX.CMDs;

import org.jetbrains.annotations.NotNull;

import x.Entt.GreetiX.GX;
import x.Entt.GreetiX.Utils.FileHandler;
import x.Entt.GreetiX.Utils.MSG;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CMDs implements CommandExecutor, TabCompleter {
    private final GX plugin;

    public CMDs(GX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!sender.hasPermission("greetix.commands")) {
            sender.sendMessage(MSG.color(GX.prefix + "&cYou don't have permissions to use this command"));
            return true;
        }

        FileHandler fh = plugin.getFH();

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender instanceof Player) {
                    fh.loadConfig();
                    sender.sendMessage(MSG.color(GX.prefix + "&aConfigurations reloaded!"));
                } else {
                    sender.sendMessage(MSG.color(GX.prefix + "&cThis command can only be run by a player."));
                }
            } else {
                sender.sendMessage(MSG.color(GX.prefix + "&c&lUSE: &f/gx reload"));
            }
        } else {
            sender.sendMessage(MSG.color(GX.prefix + "&c&lUSE: &f/gx reload"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String arg0 = args[0];

            String[] words = {"reload"};
            for (String word : words) {
                if (word.startsWith(arg0)) {
                    completions.add(word);
                }
            }
        }

        return completions;
    }
}