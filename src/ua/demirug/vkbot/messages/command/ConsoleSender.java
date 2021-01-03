package ua.demirug.vkbot.messages.command;

import java.util.ArrayList;
import java.util.Collection;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public class ConsoleSender implements CommandSender {
    
    private StringBuffer buffer = new StringBuffer();

    @Override
    public String getName() {
        return "VKUser";
    }

    @Override
    public void sendMessage(String string) {
        this.buffer.append(string).append("\n");
    }

    @Override
    public void sendMessages(String ... strings) {
        for (String str : strings) {
            this.buffer.append(str).append("\n");
        }
    }

    @Override
    public void sendMessage(BaseComponent ... bcs) {
        for (BaseComponent str : bcs) {
            this.buffer.append(str.toLegacyText()).append("\n");
        }
    }

    public void sendMessage(BaseComponent bc) {
        this.buffer.append(bc.toLegacyText()).append("\n");
    }

    public Collection<String> getGroups() {
        return new ArrayList<String>();
    }

    public /* varargs */ void addGroups(String ... strings) {
    }

    public /* varargs */ void removeGroups(String ... strings) {
    }

    public boolean hasPermission(String string) {
        return true;
    }

    public void setPermission(String string, boolean bln) {
    }

    public Collection<String> getPermissions() {
        return new ArrayList<String>();
    }

    public StringBuffer getBuffer() {
        return this.buffer;
    }
}

