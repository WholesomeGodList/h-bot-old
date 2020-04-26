package bot.commands;

import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.util.ArrayList;

import static bot.modules.Validator.validate;
import static utils.UtilMethods.isInteger;

public class TagMessage {
    public static void sendTags(MessageChannel channel, ArrayList<String> args){
        if (args.size() != 1 && isInteger(args.get(1)) && args.get(1).length() <= 6) {
            args.set(1, "https://nhentai.net/g/" + args.get(1) + "/");
        }
        if (!validate(channel, args)){
            return;
        }
        StringBuilder msg = new StringBuilder();

        channel.sendMessage("Tags for " + args.get(1) + ":").queue();
        try {
            SoupPitcher taginator = new SoupPitcher(args.get(1));
            ArrayList<String> tagPitcher = taginator.getTags();
            for (String cur : tagPitcher) {
                msg.append("`");
                msg.append(cur);
                msg.append("` ");
            }
            channel.sendMessage(msg.toString()).queue();
        } catch (HttpStatusException e) {
            channel.sendMessage("Can't find linked page: returned error code " + e.getStatusCode()).queue();
        } catch (IOException e){
            channel.sendMessage("An error occurred. Please try again, or ping my owner if this persists.").queue();
            e.printStackTrace();
        }
    }
}
