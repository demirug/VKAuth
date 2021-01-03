package ua.demirug.vkauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.AccountManager;
import ua.demirug.vkauth.Authorization;

public class LogOutCommand extends Command {
    
    public LogOutCommand() {
        super("logout");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("§6Authorization §7> §fТолько для игроков");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)sender;
        Account user = AccountManager.getAccount(player.getName());
        if (user == null) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.NoRegistered"));
            return;
        }
        user.setLogOut(true);
        player.disconnect(Authorization.getInstance().getSTR("MESSAGES.LogOut"));
    }
}

