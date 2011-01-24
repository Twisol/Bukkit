package org.bukkit.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PluginCommand extends Command {
    private final Plugin owningPlugin;

    public PluginCommand(String name, Plugin owner) {
        super(name);
        this.owningPlugin = owner;
        this.usageMessage = "";
    }

    public boolean execute(Player player, String commandLabel, String[] args) {
        boolean cmdSuccess = owningPlugin.onCommand(player, this, commandLabel, args);
        if (!cmdSuccess)
            displayUsage(player, commandLabel);
        return cmdSuccess;
    }
    
    public void displayUsage(Player player, String label) {
        if (!usageMessage.isEmpty()) {
            String tmpMsg = usageMessage.replace("<command>", label);
            String[] usageLines = tmpMsg.split("\\n");
            for (String line: usageLines) {
                while (line.length() > 0) {
                    int stripChars = (line.length() > 53 ? 53:line.length());
                    player.sendMessage(ChatColor.RED + line.substring(0, stripChars));
                    line = line.substring(stripChars);
                }
            }
        }
    }
}