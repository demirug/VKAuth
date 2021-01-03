package ua.demirug.vkauth.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import ua.demirug.vkauth.Account;
import ua.demirug.vkauth.AccountManager;
import ua.demirug.vkauth.Authorization;

public class CodeCommand extends Command {
    
    public CodeCommand() {
        super("code");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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
        if (args.length == 0) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.CODE-INFO"));
            return;
        }
        if (!user.isOnCode()) {
            player.sendMessage(Authorization.getInstance().getSTR("MESSAGES.NO-CODE"));
            return;
        }
        user.trycode(args[0].replace("[", "").replace("]", ""));
    }
}

