package bot.commands;

import bot.modules.BotAlert;
import bot.modules.TagChecker;
import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;
import utils.UtilMethods;

import java.io.IOException;
import java.util.ArrayList;

public class Randomizer {
	private static final Logger logger = LogManager.getLogger(Randomizer.class);

	public static ImmutablePair<String, Integer> getRandomUrl(ArrayList<String> args) {
		String random = "";

		int checksFailed = 0;
		boolean englishOnly = true;
		boolean badTagFilter = false;
		boolean wholesomeOnly = false;
		boolean incestIllegal = false;
		boolean yaoiIllegal = false;
		boolean yuriIllegal = false;

		if (args.contains("-nbt")) {
			badTagFilter = true;
		}
		if (args.contains("-e")) {
			englishOnly = false;
		}
		if (args.contains("-w")) {
			wholesomeOnly = true;
		}
		if (args.contains("-i")) {
			incestIllegal = true;
		}
		if (args.contains("-ya")) {
			yaoiIllegal = true;
		}
		if (args.contains("-yu")) {
			yuriIllegal = true;
		}


		ArrayList<String> checkedTags, tags;
		int latestNum = SoupPitcher.getLatestNumber();
		boolean tagCheckFailed = true;
		while (tagCheckFailed) {
			try {
				random = "https://nhentai.net/g/" + ((int) (Math.random() * latestNum) + 1);
				logger.info("Trying " + random);

				SoupPitcher randominator3000 = new SoupPitcher(random);

				if (englishOnly && !randominator3000.getLanguage().equals("English")) {
					logger.info("Not English.");
					checksFailed++;
					continue;
				}

				tags = randominator3000.getTags();

				if (yuriIllegal) {
					if (tags.contains("yuri")) {
						logger.info("Contains yuri.");
						checksFailed++;
						continue;
					}
				}
				if (incestIllegal) {
					if (tags.contains("incest") || tags.contains("inseki")) {
						logger.info("Contains incest/inseki.");
						checksFailed++;
						continue;
					}
				}
				if (yaoiIllegal) {
					if (tags.contains("yaoi")) {
						logger.info("Contains yaoi.");
						checksFailed++;
						continue;
					}
				}
				checkedTags = TagChecker.tagCheck(tags);
				if (wholesomeOnly) {
					if (TagChecker.wholesomeCheck(tags) && tags.size() > 3) {
						break;
					}
					logger.info("Unwholesome.");
					checksFailed++;
				} else {
					if (checkedTags.isEmpty()) {
						break;
					}
					if (badTagFilter) {
						logger.info("Check failed.");
						checksFailed++;
					} else {
						tagCheckFailed = checkedTags.get(0).equals("lolicon") || checkedTags.get(0).equals("shotacon");
						if (tagCheckFailed) {
							checksFailed++;
							logger.info("Check failed.");
						}
					}
				}
			} catch (HttpStatusException e) {
				logger.info(e.getStatusCode());
				checksFailed++;
			} catch (IOException e) {
				e.printStackTrace();
				checksFailed++;
			}
		}
		logger.info("Check successful!");
		return new ImmutablePair<>(random, checksFailed);
	}

	public static void sendRandom(MessageChannel channel, ArrayList<String> args, User author) {
		boolean wholesomeOnly = args.contains("-w");

		if (wholesomeOnly) {
			UtilMethods.waitForDelete(channel.sendMessage(BotAlert.createAlertEmbed("Fetching random wholesome doujin...", "This will take some time")).complete());
		} else {
			UtilMethods.waitForDelete(channel.sendMessage(BotAlert.createAlertEmbed("Fetching random doujin...", "This may take some time")).complete());
		}

		ImmutablePair<String, Integer> results = getRandomUrl(args);

		args.add(1, results.left);

		int checksFailed = results.right;
		channel.sendMessage(BotAlert.createAlertEmbed(wholesomeOnly ? "(Probably) Wholesome doujin found!" : "Doujin found!", "Doujins checked: " + (checksFailed + 1))).queue();

		DoujinInfo.sendInfo(channel, args, author);
	}
}
