package bot.commands;

import bot.hListener;
import bot.modules.BookTracker;
import bot.modules.BotAlert;
import bot.modules.TagChecker;
import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.util.ArrayList;

import static bot.modules.Validator.validate;
import static utils.ArrayDisplay.display;
import static utils.UtilMethods.isInteger;

public class Read {
    public static void read(MessageChannel channel, ArrayList<String> args, User author){
        if (args.size() != 1 && isInteger(args.get(1)) && args.get(1).length() <= 6) {
            args.set(1, "https://nhentai.net/g/" + args.get(1) + "/");
        }
        if(!validate(channel, args)) {
            return;
        }

        try {
            SoupPitcher readinator = new SoupPitcher(args.get(1));
            ArrayList<String> checkedTags = TagChecker.tagCheck(readinator.getTags());
            if (!checkedTags.isEmpty() && (checkedTags.get(0).equals("lolicon") || checkedTags.get(0).equals("shotacon"))) {
                channel.sendMessage(BotAlert.createAlertEmbed("Bot Alert", "This doujin violates the Discord ToS", "This doujin contained the following illegal tags:\n" + display(checkedTags))).queue();
                return;
            }
            channel.sendTyping().complete();
            EmbedBuilder reader = new EmbedBuilder();
            reader.setTitle("Page 1");
            reader.setImage(readinator.getPageLink(1));
            Message book = channel.sendMessage(reader.build()).complete();
            book.addReaction("U+2B05").complete();
            book.addReaction("U+27A1").complete();
            book.addReaction("U+274C").complete();
            BookTracker currentBookTracker = new BookTracker(author.getId(), book.getId(), 1, args.get(1), BookTracker.BookMode.READ);
            hListener.register(currentBookTracker);
        } catch (HttpStatusException e) {
            channel.sendMessage("Can't find page: returned error code " + e.getStatusCode()).queue();
        } catch (IOException e){
            channel.sendMessage("An error occurred. Please try again, or ping my owner if this persists.").queue();
            e.printStackTrace();
        }
    }
}
