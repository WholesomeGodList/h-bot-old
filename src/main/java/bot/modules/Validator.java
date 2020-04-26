package bot.modules;

import bot.commands.SupportedSites;
import net.dv8tion.jda.api.entities.MessageChannel;
import utils.UtilMethods;

import java.util.ArrayList;

public class Validator {
    public static boolean validate(MessageChannel channel, ArrayList<String> args){
        if (args.size() == 1) {
            channel.sendMessage("Please supply a link or numbers!").queue();
            return false;
        }
        if (args.get(1).contains("177013")) {
            channel.sendMessage(UtilMethods.cursedEmbed()).queue();
            return false;
        }
        if (!SupportedSites.isSupported(args.get(1))) {
            channel.sendMessage("Not a supported site / valid URL!").queue();
            return false;
        }
        if ("read".contains(args.get(0)) && args.get(1).contains("exhentai") || args.get(1).contains("e-hentai")){
            channel.sendMessage("`>read` is not supported for e-hentai currently").queue();
            return false;
        }

        return true;
    }
}
