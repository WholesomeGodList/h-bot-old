package bot.commands;

import bot.ehentai.EHFetcher;
import bot.hListener;
import bot.modules.BookTracker;
import bot.modules.BotAlert;
import bot.modules.InfoBuilder;
import bot.modules.TagChecker;
import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.util.ArrayList;

import static bot.modules.Validator.validate;
import static utils.ArrayDisplay.display;
import static utils.UtilMethods.isInteger;

public class DoujinInfo {
    private static final Logger logger = LogManager.getLogger(DoujinInfo.class);
    public static void sendInfo(MessageChannel channel, ArrayList<String> args, User author) {
        if (args.size() != 1 && isInteger(args.get(1)) && args.get(1).length() <= 6) {
            args.set(1, "https://nhentai.net/g/" + args.get(1) + "/");
        }
        if(!validate(channel, args)){
            return;
        }
        try {
            ArrayList<String> checkedTags;

            if(args.get(1).replaceAll("-", "x").contains("exhentai")){
                EHFetcher tagGetter = new EHFetcher(args.get(1));
                checkedTags = TagChecker.tagCheckWithWarnings(tagGetter.getTags());
            }
            else {
                SoupPitcher tagGetter = new SoupPitcher(args.get(1));
                checkedTags = TagChecker.tagCheckWithWarnings(tagGetter.getTags());
            }
            if (checkedTags.isEmpty()) {
                channel.sendTyping().complete();
                channel.sendMessage(InfoBuilder.getInfoEmbed(args.get(1))).queue();
            } else if (checkedTags.get(0).equals("lolicon") || checkedTags.get(0).equals("shotacon")) {
                channel.sendMessage(BotAlert.createAlertEmbed("Bot Alert", "This doujin violates the Discord ToS", "This doujin contained the following illegal tags:\n" + display(checkedTags))).queue();
            } else {
                EmbedBuilder eb2 = new EmbedBuilder();
                eb2.setAuthor("Bot Alert", null, "https://i.redd.it/fkg9yip5yyl21.png");
                eb2.setTitle("This doujin has potentially dangerous tags");
                eb2.setDescription("This doujin contained the following tags:\n" + display(checkedTags));
                eb2.addField("Continue?", "To continue, press the checkmark.", false);
                String msgId = channel.sendMessage(eb2.build()).complete().getId();
                channel.addReactionById(msgId, "U+2705").queue();
                channel.addReactionById(msgId, "U+274C").queue();
                BookTracker currentBookTracker = new BookTracker(author.getId(), msgId, 1, args.get(1), BookTracker.BookMode.CONFIRM);
                hListener.register(currentBookTracker);
            }
        } catch (HttpStatusException e) {
            channel.sendMessage("Can't find page: returned error code " + e.getStatusCode()).queue();
        } catch (IOException e){
            channel.sendMessage("An error occurred. Please try again, or ping my owner if this persists.").queue();
            e.printStackTrace();
        }
    }
}
