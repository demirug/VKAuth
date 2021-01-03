package ua.demirug.vkbot.messages.performance;

import com.petersamokhin.bots.sdk.callbacks.callbackapi.ExecuteCallback;
import com.petersamokhin.bots.sdk.clients.Client;
import com.petersamokhin.bots.sdk.objects.Message;
import java.util.HashMap;
import java.util.List;
import ua.demirug.vkauth.Authorization;

public class MessageManager {
    private static HashMap<String, MessageHandler> messages = new HashMap();
    private static HashMap<String, String> submessages = new HashMap();
    private static HashMap<String, String> keys = new HashMap();

    public static void registerKeys(List<String> keyz, String message) {
        for (String key : keyz) {
            keys.put(key.toLowerCase(), message);
        }
    }

    public static void registerMessage(MessageHandler handler, String ... commands) {
        for (String cmd : commands) {
            messages.put(cmd.toLowerCase(), handler);
        }
    }
    
    public static void clear() {
        submessages.clear();
        keys.clear();
    }

    public static void registerSubMessage(String question, String answer) {
        submessages.put(question.toLowerCase(), answer);
    }

    public static void handle(Message message) {
        String[] args = message.getText().toLowerCase().split(" ");
        if (messages.containsKey(args[0])) {
            messages.get(args[0]).execute(message.authorId(), MessageManager.build(message.getText().replace("[", "").replace("]", "").split(" ")));
            return;
        }
        if (submessages.containsKey(args[0])) {
            MessageManager.sendMessage((int)message.authorId(), submessages.get(args[0]));
            return;
        }
        for (String str : args) {
            
            for(String key : keys.keySet()) {
                if(str.contains(key)) {
                    MessageManager.sendMessage((int)message.authorId(), keys.get(key));
                    return;
                }
            }
        }
        MessageManager.sendMessage((int)message.authorId(), Authorization.getInstance().getSTR("VK-MODULE.UNKNOW"));
    }

    private static String[] build(String[] args) {
        String[] elements = new String[args.length - 1];
        for (int i = 1; i < args.length; ++i) {
            elements[i - 1] = args[i];
        }
        return elements;
    }

    public static void sendMessage(int author_id, String text) {
        if(text.isEmpty()) return;
        new Message().from((Client)Authorization.getInstance().getGroup()).to(author_id).text((Object)text).send(new ExecuteCallback[0]);
    }

    public static void sendMessage(int author_id, String[] text) {
        StringBuilder builder = new StringBuilder();
        for (String st : text) {
            if(st.isEmpty()) continue;
            builder.append(st).append("\n");
        }
        new Message().from((Client)Authorization.getInstance().getGroup()).to(author_id).text((Object)builder.toString()).send(new ExecuteCallback[0]);
    }
}

