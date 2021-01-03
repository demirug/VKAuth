package ua.demirug.vkauth.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;

public class BlockCommand extends Command {

    public BlockCommand() {
        super("block");
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(cs != BungeeCord.getInstance().getConsole() && !Authorization.getInstance().getConfig().getStringList("ADMINS").contains(cs.getName())) return;
        if(args.length < 1) {
            cs.sendMessage("§6Authorization §7> §f/block [Ник]");
            return;
        }
        
        Account user = Authorization.getInstance().getAccountBD().getAccount(args[0]);
        if(user == null) {
            cs.sendMessage("§6Authorization §7> §fПользователь §c" + args[0] + " §fненайден");
            return;
        }
        
        if(user.isLocked()) {
            cs.sendMessage("§6Authorization §7> §fПользователь §c" + args[0] + " §fуже заблокирован");
            return;
        }
        
        user.setLocked(true);
        if(user.isOnline()) user.getPlayer().disconnect(Authorization.getInstance().getSTR("MESSAGES.BLOCKED"));
        else Authorization.getInstance().getAccountBD().updateAccount(user);
        cs.sendMessage("§6Authorization §7> §fПользователь §c" + args[0] + " §fзаблокирован");
        
    }

}
