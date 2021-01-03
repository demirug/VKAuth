package ua.demirug.vkauth.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Authorization;

public class ReloadCFGCommand extends Command {

    public ReloadCFGCommand() {
        super("reloadconfig", null, new String[] {"reloadcfg"});
    }

    @Override
    public void execute(CommandSender cs, String[] strings) {
        if(cs != BungeeCord.getInstance().getConsole() && !Authorization.getInstance().getConfig().getStringList("ADMINS").contains(cs.getName())) return;
      Authorization.getInstance().reloadConfig();
      cs.sendMessage("§6Authorization §7> §aКонфигурация была перезагружена");
    }

}
