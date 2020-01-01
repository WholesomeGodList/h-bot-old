package bot;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BotConfig {
    //This just loads the config of the bot.
    static final String PREFIX = readBotPrefix("./config.json");
    static final String BOT_TOKEN = readBotToken("./config.json");

    private static String readBotToken(String fileName) {
        try {
            InputStream is = new FileInputStream(new File(fileName));
            JSONObject bruh = new JSONObject(new JSONTokener(is));
            return bruh.getString("token");
        } catch(IOException e){
            System.out.println("Config not found.");
        }
        return null;
    }

    private static String readBotPrefix(String fileName) {
        try {
            InputStream is = new FileInputStream(new File(fileName));
            JSONObject bruh = new JSONObject(new JSONTokener(is));
            return bruh.getString("prefix");
        } catch(IOException e){
            System.out.println("Config not found.");
        }
        return null;
    }
}
