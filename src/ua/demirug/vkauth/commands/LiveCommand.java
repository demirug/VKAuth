package ua.demirug.vkauth.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Authorization;

public class LiveCommand extends Command {

    public LiveCommand() {
        super("live");
    }

    @Override
    public void execute(CommandSender cs, String[] strings) {
        if(cs != BungeeCord.getInstance().getConsole() && !Authorization.getInstance().getConfig().getStringList("ADMINS").contains(cs.getName())) return;
        if(Authorization.getInstance().isVKEnable()) {
            Authorization.getInstance().getGroup().longPoll().off();
            Authorization.getInstance().initGroup();
            cs.sendMessage("§6Authorization §7> §fВК переподключен");
        } else cs.sendMessage("§6Authorization §7> §fВК не подключен");
        
    }
    
}
