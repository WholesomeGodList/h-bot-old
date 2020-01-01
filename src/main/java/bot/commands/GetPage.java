package bot.commands;

import bot.modules.BotAlert;
import bot.modules.SoupPitcher;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jsoup.HttpStatusException;

import java.util.ArrayList;

import static bot.modules.TagChecker.tagCheck;
import static bot.modules.Validator.validate;
import static utils.ArrayDisplay.display;
import static utils.UtilMethods.isInteger;

public class GetPage {
    public static void sendPageLink(MessageChannel channel, ArrayList<String> args) {
        if (isInteger(args.get(1)) && args.get(1).length() <= 6) {
            args.set(1, "https://nhentai.net/g/" + args.get(1) + "/");
        }
        if (args.size() <= 2){
            channel.sendMessage("Please provide a page number to get!").queue();
            return;
        }
        if (!validate(channel, args)){
            return;
        }
        try {
            ArrayList<String> checkedTags = tagCheck(SoupPitcher.getTags(args.get(1)));
            if (!checkedTags.isEmpty() && (checkedTags.get(0).equals("lolicon") || checkedTags.get(0).equals("shotacon"))) {
                channel.sendMessage(BotAlert.createAlertEmbed("Bot Alert", "This doujin violates the Discord ToS", "This doujin contained the following illegal tags:\n" + display(checkedTags))).queue();
                return;
            }
            channel.sendMessage(SoupPitcher.getPageLink(args.get(1), args.get(2))).queue();
        } catch (
                HttpStatusException e) {
            channel.sendMessage("Can't find page: returned error code " + e.getStatusCode()).queue();
        }
    }
}
