package ua.demirug.vkauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Authorization;

public class AuthCommand extends Command {

    public AuthCommand() {
        super("auth", null, new String[] {"authorization"});
    }

    @Override
    public void execute(CommandSender cs, String[] strings) {
       cs.sendMessage("§6Authorization §7> §fВерсия плагина §c" + Authorization.getInstance().getDescription().getVersion() + " §7| §fАвтор §cvk.com/demirug");
    }

}
