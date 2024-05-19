package x.Entt.GreetiX.cmds;

import x.Entt.GreetiX.GX;
import x.Entt.GreetiX.config.MCM;
import x.Entt.GreetiX.utils.MSGU;
import x.Entt.GreetiX.listeners.Listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainCMD implements CommandExecutor, TabCompleter {
    private GX plugin;
    private Listener listener;
    private MCM mcm;

    public MainCMD(GX plugin) {
        this.plugin = plugin;
        this.mcm = new MCM(plugin);
        this.listener = new Listener(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("greetix.commands")) {
            sender.sendMessage(MSGU.color(GX.prefix + "&cYou don't have permissions to use this command"));
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                Player player = (Player) sender;
                mcm.reloadConfig();
            } else {
                sender.sendMessage(MSGU.color(GX.prefix + "&c&lUSE: &f/gx reload"));
            }
        } else {
            sender.sendMessage(MSGU.color(GX.prefix + "&c&lUSE: &f/gx reload"));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // logic completer
        List<String> completer = new ArrayList<>();

        if (args.length == 1) {
            String arg0 = args[0];

            String[] words = new String[]{"reload"};
            for (String search : words) {
                if (search.startsWith(arg0)) {
                    completer.add(search);
                }
            }
        }
        return completer;
    }
}