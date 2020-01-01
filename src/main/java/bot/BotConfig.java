package bot;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

public class BotConfig {
    //This just loads the config of the bot.
    static final String PREFIX = readBotPrefix();
    static final String BOT_TOKEN = readBotToken();

    private static String readBotToken() {
        try {
            InputStream is = new FileInputStream(new File("./config.json"));
            JSONObject bruh = new JSONObject(new JSONTokener(is));
            return bruh.getString("token");
        } catch(IOException e){
            System.err.println("ERROR: Config file not found. Stopping bot...");
            System.exit(1);
            return "How did you even get here?";
        }
    }

    private static String readBotPrefix() {
        try {
            InputStream is = new FileInputStream(new File("./config.json"));
            JSONObject bruh = new JSONObject(new JSONTokener(is));
            return bruh.getString("prefix");
        } catch(IOException e){
            System.err.println("Prefix not found.");
            System.exit(1);
            return "If this somehow enters the code I'm suing Java";
        }
    }
}
