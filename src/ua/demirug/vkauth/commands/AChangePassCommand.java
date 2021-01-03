package ua.demirug.vkauth.commands;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.Authorization;

public class AChangePassCommand extends Command {

    public AChangePassCommand() {
        super("achangepassword", null, new String[] {"achangepass", "adminchangepassword" ,"adminchangepass"});
    }

    @Override
    public void execute(CommandSender cs, String[] args) {
        if(cs != BungeeCord.getInstance().getConsole() && !Authorization.getInstance().getConfig().getStringList("ADMINS").contains(cs.getName())) return;
        if(args.length < 2) {
            cs.sendMessage("§6Authorization §7> §f/achangepass [Ник] [Новый пароль]");
            return;
        }
        
        if (args[1].replace("[", "").replace("]", "").length() < 4) {
            cs.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordMinLenght"));
            return;
        }
        if (args[1].replace("[", "").replace("]", "").length() > 16) {
            cs.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordMaxLenght"));
            return;
        }
        if (Authorization.getInstance().checkString("abcdefghijklmnopqrstuvwxyz_0123456789", args[1].replace("[", "").replace("]", ""))) {
            cs.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordIncorrectSymbols"));
            return;
        }
        
        Account user = Authorization.getInstance().getAccountBD().getAccount(args[0]);
        if(user == null) {
            cs.sendMessage("§6Authorization §7> §fИгрок §c" + args[0] + " §fненайден");
            return;
        }
        
        user.setPassword(args[1]);
        Authorization.getInstance().getAccountBD().updateAccount(user);
        cs.sendMessage("§6Authorization §7> §fПароль пользователя §c" + args[0] + " §fизменен");
        
    }

}
