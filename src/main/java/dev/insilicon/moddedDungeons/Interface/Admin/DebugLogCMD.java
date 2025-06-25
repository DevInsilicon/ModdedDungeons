package dev.insilicon.moddedDungeons.Interface.Admin;

import dev.insilicon.moddedDungeons.ModdedDungeons;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class DebugLogCMD implements CommandExecutor {
    public static final int MAX_LOG_SIZE = 100;
    public static final LinkedList<String> debugLog = new LinkedList<>();

    public static void log(String message) {
        synchronized (debugLog) {
            debugLog.addFirst("[" + System.currentTimeMillis() + "] " + message);
            if (debugLog.size() > MAX_LOG_SIZE) debugLog.removeLast();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("moddeddungeons.debuglog")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        sender.sendMessage("§e--- ModdedDungeons Debug Log ---");
        synchronized (debugLog) {
            if (debugLog.isEmpty()) {
                sender.sendMessage("§7No recent log entries.");
            } else {
                for (String entry : debugLog) {
                    sender.sendMessage("§7" + entry);
                }
            }
        }
        return true;
    }
}
