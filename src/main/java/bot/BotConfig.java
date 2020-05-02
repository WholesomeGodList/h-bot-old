package bot;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BotConfig {
	//This just loads the config of the bot.
	public static final String BOT_TOKEN = readBotToken();
	public static final String PREFIX = readBotPrefix();

	//Optional Imgur config (for the Imgur functionality)
	public static final String CLIENT_ID = readClientID();
	public static boolean IMGUR_DISABLED;

	private static String readClientID() {
		try {
			InputStream is = new FileInputStream(new File("./config.json"));
			JSONObject bruh = new JSONObject(new JSONTokener(is));

			if (bruh.has("imgur-clientid")) {
				IMGUR_DISABLED = false;
				return bruh.getString("token");
			} else {
				IMGUR_DISABLED = true;
				return "";
			}
		} catch (IOException e) {
			System.err.println("ERROR: Config file not found. Stopping bot...");
			System.exit(1);
			return "Did the code not freaking exit? I'm calling the CIA";
		}
	}

	private static String readBotToken() {
		try {
			InputStream is = new FileInputStream(new File("./config.json"));
			JSONObject bruh = new JSONObject(new JSONTokener(is));
			return bruh.getString("token");
		} catch (IOException e) {
			System.err.println("ERROR: Config file not found. Please make sure that config.json is in the same folder as hbot.jar.");
			System.err.println("Stopping bot...");
			System.exit(1);
			return "How did you even get here?";
		}
	}

	private static String readBotPrefix() {
		try {
			InputStream is = new FileInputStream(new File("./config.json"));
			JSONObject bruh = new JSONObject(new JSONTokener(is));
			return bruh.getString("prefix");
		} catch (IOException e) {
			System.err.println("ERROR: Prefix not found. Please make sure that config.json is in the same folder as hbot.jar.");
			System.err.println("Stopping bot...");
			System.exit(1);
			return "If this somehow enters the code I'm suing Java";
		}
	}
}
