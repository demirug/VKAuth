package ua.demirug.vkauth.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;

public class UnBlockCommand extends Command {

    public UnBlockCommand() {
        super("unblock");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(cs != BungeeCord.getInstance().getConsole() && !!Authorization.getInstance().getConfig().getStringList("ADMINS").contains(cs.getName())) return;
        if(args.length < 1) {
            cs.sendMessage("§6Authorization §7> §f/unblock [Ник]");
            return;
        }
        
        Account user = Authorization.getInstance().getAccountBD().getAccount(args[0]);
        if(user == null) {
            cs.sendMessage("§6Authorization §7> §fПользователь §c" + args[0] + " §fненайден");
            return;
        }
        
        if(!user.isLocked()) {
            cs.sendMessage("§6Authorization §7> §fПользователь §c" + args[0] + " §fне заблокирован");
            return;
        }
        
        user.setLocked(false);
        Authorization.getInstance().getAccountBD().updateAccount(user);
        cs.sendMessage("§6Authorization §7> §fПользователь §c" + args[0] + " §fразблокирован");
        
    }

}
