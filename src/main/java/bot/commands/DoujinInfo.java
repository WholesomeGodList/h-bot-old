package bot.commands;

import bot.hListener;
import bot.modules.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jsoup.HttpStatusException;

import java.util.ArrayList;

import static bot.modules.Validator.validate;
import static utils.ArrayDisplay.display;
import static utils.UtilMethods.isInteger;

public class DoujinInfo {
    public static void sendInfo(MessageChannel channel, ArrayList<String> args, User author) {
        if (args.size() != 1 && isInteger(args.get(1)) && args.get(1).length() <= 6) {
            args.set(1, "https://nhentai.net/g/" + args.get(1) + "/");
        }
        if(!validate(channel, args)){
            return;
        }
        try {
            ArrayList<String> checkedTags = TagChecker.tagCheckWithWarnings(SoupPitcher.getTags(args.get(1)));
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
        }
    }
}
