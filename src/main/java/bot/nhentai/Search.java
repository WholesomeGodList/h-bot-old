package bot.nhentai;

import bot.hListener;
import bot.modules.BotAlert;
import bot.modules.InfoBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;

import java.util.ArrayList;

import static utils.UtilMethods.waitForDelete;

public class Search extends Thread {
	private static final Logger logger = LogManager.getLogger(Search.class);
	private final ArrayList<String> args;
	private final MessageChannel channel;
	private final MessageReceivedEvent event;

	public Search(ArrayList<String> args, MessageChannel channel, MessageReceivedEvent event) {
		this.args = args;
		this.channel = channel;
		this.event = event;
	}

	public void run() {
		try {
			boolean deepSearch = false;
			if (args.get(0).equals("deepsearch")) {
				deepSearch = true;
			}
			if (args.size() < 2) {
				hListener.decrementSearches();
				waitForDelete(channel.sendMessage("Bruh you're supposed to search something").complete());
				return;
			}
			if (args.get(1).equals("lolicon") || args.get(1).equals("shotacon")) {
				hListener.decrementSearches();
				channel.sendMessage("***FBI OPEN UP***").queue();
				return;
			}
			boolean nonrestrict = false;
			if (args.get(1).equals("-n")) {
				nonrestrict = true;
			}
			PrivateChannel privatechannel = event.getAuthor().openPrivateChannel().complete();

			privatechannel.sendTyping().complete();

			waitForDelete(channel.sendMessage(BotAlert.createAlertEmbed("Searching...", "Please wait while the search is being done")).complete());

			StringBuilder queryBuilder = new StringBuilder();
			if (nonrestrict) {
				for (int i = 2; i < args.size(); i++) {
					queryBuilder.append(args.get(i));
					queryBuilder.append(" ");
				}
			} else {
				for (int i = 1; i < args.size(); i++) {
					queryBuilder.append(args.get(i));
					queryBuilder.append(" ");
				}
			}
			queryBuilder.append("english");
			String query = queryBuilder.toString();
			ArrayList<String> results;

			if (deepSearch) {
				results = SoupPitcher.getTopSearchResults(query, 10, nonrestrict);
			} else {
				results = SoupPitcher.getTopSearchResults(query, 4, nonrestrict);
			}

			MessageEmbed noResultsAlert = BotAlert.createAlertEmbed("Search Results", "No results found!");
			privatechannel.sendMessage(noResultsAlert).queue();

			channel.sendTyping().complete();
			channel.sendMessage(BotAlert.createAlertEmbed("Search", "Sending the results to your DMs!")).complete();
			try {
				MessageEmbed eb = InfoBuilder.getInfoEmbed(results.get((int) (Math.random() * results.size())));
				privatechannel.sendMessage(eb).queue();
			} catch (HttpStatusException e) {
				logger.info("Exception happened while building random info embed! HTTP status code: " + e.getStatusCode());
				privatechannel.sendMessage("An error occurred in the connection. Status code: " + e.getStatusCode()).queue();
			}

			StringBuilder current = new StringBuilder();
			current.append("Full results:");
			while (!results.isEmpty()) {
				current.append("\n<").append(results.get(0)).append(">");
				results.remove(0);
				if (current.length() > 1500) {
					privatechannel.sendMessage(current.toString()).complete();
					current = new StringBuilder();
				}
			}
			privatechannel.sendMessage(current.toString()).queue();

			hListener.decrementSearches();
			privatechannel.close().queue();
		} catch (Exception e) {
			logger.info("Something went wrong while searching. Printing stack trace...");
			e.printStackTrace();
			hListener.decrementSearches();
		}
	}
}
