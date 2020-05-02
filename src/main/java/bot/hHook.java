package bot;


import bot.modules.InfoBuilder;
import bot.modules.TagChecker;
import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class hHook implements Runnable {
	private static final Logger logger = LogManager.getLogger(hHook.class);

	@Override
	public void run() {
		try {
			runHook();
		} catch (Exception e) {
			logger.error("Exception in h-hook thrown:\n" + Arrays.toString(e.getStackTrace()));
		}
	}

	public void runHook() {
		logger.info("Now running hook");
		int latest = SoupPitcher.getLatestNumber();
		logger.info("Current number: " + getLastNumber());
		logger.info("Latest number: " + latest);
		if (getLastNumber() == latest || getLastNumber() == -1) {
			logger.info("Hook finished.");
			return;
		}

		JDA jda = hBotMain.getMyBot();
		ArrayList<TextChannel> hooks = getHookChannels(jda);
		ArrayList<String> links = new ArrayList<>();

		for (int i = getLastNumber() + 1; i <= latest; i++) {
			links.add("https://nhentai.net/g/" + i);
		}

		ArrayList<String> filteredLinks = new ArrayList<>();
		for (String cur : links) {
			try {
				Thread.sleep(200);
				SoupPitcher curPitcher = new SoupPitcher(cur);
				if (TagChecker.wholesomeCheck(curPitcher.getTags()) && curPitcher.getLanguage().equals("English") && curPitcher.getTags().size() >= 3) {
					filteredLinks.add(cur);
					logger.info("Link found: " + cur);
				}
			} catch (Exception e) {
				logger.info("Exception occurred in hook!");
				e.printStackTrace();
			}
		}

		logger.info("Building and sending info embeds...");
		Message curMsg;
		for (String cur : filteredLinks) {
			try {
				MessageEmbed hookEmbed = InfoBuilder.getInfoEmbed(cur);
				for (TextChannel curChannel : hooks) {
					curMsg = curChannel.sendMessage(hookEmbed).complete();
					curMsg.addReaction("U+2B06").queue();
					curMsg.addReaction("U+2B07").queue();
				}
			} catch (IOException e) {
				logger.info("Error");
			}
		}
		setLastNumber(latest);
		logger.info("Hook finished.");
	}

	private static ArrayList<TextChannel> getHookChannels(JDA jda) {
		ArrayList<TextChannel> hooks = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(new File("./hook.json"));
			JSONObject hookmachine = new JSONObject(new JSONTokener(is));
			JSONArray hookparser = hookmachine.getJSONArray("channels");
			for (int i = 0; i < hookparser.length(); i++) {
				JSONObject cur = hookparser.getJSONObject(i);
				String guildId = cur.getString("guildId");
				String channelId = cur.getString("channelId");

				hooks.add(Objects.requireNonNull(jda.getGuildById(guildId), "Guild ID must not be null!").getTextChannelById(channelId));
			}
		} catch (IOException e) {
			logger.info("Hook channels not found???");
		}
		return hooks;
	}

	public static void addHookChannel(String guildId, String channelId) {
		try {
			InputStream is = new FileInputStream(new File("./hook.json"));
			JSONObject hookfile = new JSONObject(new JSONTokener(is));
			JSONArray hooklist = hookfile.getJSONArray("channels");
			JSONObject newhook = new JSONObject();
			newhook.put("guildId", guildId);
			newhook.put("channelId", channelId);
			hooklist.put(newhook);
			FileWriter file = new FileWriter("./hook.json");
			file.write(hookfile.toString(4));
			file.close();
		} catch (IOException e) {
			logger.info("Error occurred when registering new hook.");
			e.printStackTrace();
		}
	}

	public static void removeHookChannel(String guildId, String channelId) {
		try {
			InputStream is = new FileInputStream(new File("./hook.json"));
			JSONObject hookfile = new JSONObject(new JSONTokener(is));
			JSONArray hooklist = hookfile.getJSONArray("channels");

			for (int i = hooklist.length() - 1; i >= 0; i--) {
				JSONObject cur = hooklist.getJSONObject(i);
				if (guildId.equals(cur.get("guildId")) && channelId.equals(cur.get("channelId"))) {
					hooklist.remove(i);
				}
			}

			FileWriter file = new FileWriter("./hook.json");
			file.write(hookfile.toString(4));
			file.close();
		} catch (IOException e) {
			logger.info("Error occurred when registering new hook.");
			e.printStackTrace();
		}
	}

	private static void setLastNumber(int latest) {
		try {
			InputStream is = new FileInputStream(new File("./hook.json"));
			JSONObject setter = new JSONObject(new JSONTokener(is));
			setter.put("lastNumber", latest);
			FileWriter file = new FileWriter("./hook.json");
			file.write(setter.toString(4));
			file.close();
		} catch (IOException e) {
			logger.info("Last number set unsuccessfully.");
		}
	}

	private static int getLastNumber() {
		try {
			InputStream is = new FileInputStream(new File("./hook.json"));
			JSONObject getter = new JSONObject(new JSONTokener(is));
			return getter.getInt("lastNumber");
		} catch (IOException e) {
			logger.info("Last number was not gotten.");
		}
		return -1;
	}
}
