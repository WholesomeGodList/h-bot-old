package bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class HelpMessage {
    //Just the help message.
    public static MessageEmbed getHelpEmbed() {
        EmbedBuilder helpMsg = new EmbedBuilder();
        helpMsg.setColor(Color.BLACK);
        helpMsg.setAuthor("H-Bot", null, "https://i.redd.it/fkg9yip5yyl21.png");
        helpMsg.setTitle("For easily accessing info about doujins");
        helpMsg.setFooter("Built by Stinggyray#1000", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
        helpMsg.addField("Core commands",
                """
                        All nhentai commands work with numbers!
                        >help: Displays the help message
                        >tags [link]: Returns tags for an nhentai link
                        >getpage [link] [page]: Gets a page of an nhentai doujin
                        >info [link]: Returns info about a doujin
                        >random [flags]: Returns info about a random doujin (flags must have a dash in front of them!)
                        ```
                        -e: include non-english results
                        -nbt: no bad tags allowed in results
                        -w: no non-wholesome tags allowed in results (more restrictive than -nbt)
                        -i: no incest/inseki
                        -ya / yu: no yaoi / yuri
                        ```
                        """, false);
        helpMsg.addField("Specialized commands",
                """
                        >read [link]: Opens the reader for a doujin
                        >badtags/warningtags: Lists the tags you'll be warned about
                        >supportedsites/sites: Lists the sites this bot supports
                        >search [-n] [query]: Queries for up to 100 doujins, and returns the ones it finds without any non-wholesome and warning tags (>badtags)
                        >deepsearch [-n] [query]: Queries for up to 250 doujins instead of 100. Use this only if you want to wait a long time.
                        ```
                        -n: non-restrictive (works for both search and deepsearch) (no longer blocks warning tags, just non-wholesome tags)
                        ```
                        """
                , false);
        helpMsg.addField("Send any bugs / suggestions to Stinggyray#1000", "This bot is pretty much a finished product, but bug reports are always welcome!", false);
        helpMsg.setTimestamp(Instant.now());

        return helpMsg.build();
    }
}
