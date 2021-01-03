package ua.demirug.vkauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.AccountManager;
import ua.demirug.vkauth.Authorization;

public class ChangePasswordCommand extends Command {
    
    public ChangePasswordCommand() {
        super("changepass", null, new String[]{"changepassword"});
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§6Authorization §7> §fТолько для игроков");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)sender;
        Account user = AccountManager.getAccount(player.getName());
        if(user.getRealName() == null) player.sendMessage("NULL");
        else player.sendMessage(user.getRealName());
        if (user == null) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.NoRegistered"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.ChangePasswordInfo"));
            return;
        }
        if (!user.getPassword().equals(Authorization.getInstance().getHashManager().toHash(args[0].replace("[", "").replace("]", "")))) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.WrongOldPassword"));
            return;
        }
        if (args[1].replace("[", "").replace("]", "").length() < 4) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordMinLenght"));
            return;
        }
        if (args[1].replace("[", "").replace("]", "").length() > 16) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordMaxLenght"));
            return;
        }
        if (Authorization.getInstance().checkString("abcdefghijklmnopqrstuvwxyz_0123456789", args[1].replace("[", "").replace("]", ""))) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordIncorrectSymbols"));
            return;
        }
        user.setPassword(args[1].replace("[", "").replace("]", ""));
        player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.PasswordChanged"));
    }
}

