package bot.ehentai;

import bot.hListener;
import bot.modules.BotAlert;
import bot.modules.InfoBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static utils.UtilMethods.waitForDelete;

public class EHSearch extends Thread {
    private static final Logger logger = LogManager.getLogger(EHSearch.class);
    private ArrayList<String> args;
    private MessageChannel channel;
    private MessageReceivedEvent event;
    private boolean searchAll;

    public EHSearch( ArrayList<String> args, MessageChannel channel, MessageReceivedEvent event) {
        this.args = args;
        this.channel = channel;
        this.event = event;
    }

    public void run() {
        try {
            boolean deepSearch = false;
            if (args.get(0).equals("deepsearcheh")) {
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

            if (nonrestrict) {
                privatechannel.sendTyping().complete();
            } else {
                channel.sendTyping().complete();
            }

            waitForDelete(channel.sendMessage(BotAlert.createAlertEmbed("Searching e-hentai...", "Please wait while the search is being done")).complete());

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
            String query = queryBuilder.toString().substring(0, queryBuilder.toString().length() - 1);
            ArrayList<String> results;

            if (deepSearch) {
                results = EHFetcher.getTopSearchResults(query, 10, nonrestrict);
            } else {
                results = EHFetcher.getTopSearchResults(query, 4, nonrestrict);
            }

            if (results.isEmpty()) {
                MessageEmbed noResultsAlert = BotAlert.createAlertEmbed("Search Results", "No results found!");
                if (nonrestrict)
                    privatechannel.sendMessage(noResultsAlert).queue();
                else
                    channel.sendMessage(noResultsAlert).queue();
            } else {
                channel.sendTyping().complete();
                if (nonrestrict) {
                    channel.sendMessage(BotAlert.createAlertEmbed("Non-restrictive mode detected", "Sending the results to your DMs!")).complete();
                } else {
                    EmbedBuilder bruh = new EmbedBuilder();
                    bruh.setAuthor("Random Search Result", "https://e-hentai.org/?f_cats=1017&f_search=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=", "https://i.redd.it/fkg9yip5yyl21.png");
                    bruh.setDescription("Wholesome results all found! Building info embed for random one...");
                    channel.sendMessage(bruh.build()).complete();
                }
                try {
                    MessageEmbed eb = InfoBuilder.getInfoEmbed(results.get((int) (Math.random() * results.size())));
                    if (nonrestrict) {
                        privatechannel.sendMessage(eb).queue();
                    } else {
                        channel.sendMessage(eb).queue();
                    }
                } catch (HttpStatusException e) {
                    logger.info("Exception happened while building random info embed! HTTP status code: " + e.getStatusCode());
                }

                StringBuilder current = new StringBuilder();
                current.append("Full results:");
                while (!results.isEmpty()) {
                    current.append("\n<").append(results.get(0)).append(">");
                    results.remove(0);
                    if (current.length() > 1500) {
                        if (!nonrestrict) {
                            channel.sendMessage(current.toString()).queue();
                        } else {
                            privatechannel.sendMessage(current.toString()).queue();
                        }
                        current = new StringBuilder();
                    }
                }
                if (nonrestrict) {
                    privatechannel.sendMessage(current.toString()).queue();
                } else {
                    channel.sendMessage(current.toString()).queue();
                }
            }
            hListener.decrementEHSearches();
            privatechannel.close().queue();
        }
        catch (Exception e){
            logger.info("Something went wrong while searching. Printing stack trace...");
            e.printStackTrace();
            hListener.decrementEHSearches();
        }
    }
}
