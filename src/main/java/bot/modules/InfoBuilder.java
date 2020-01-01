package bot.modules;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;

import java.awt.*;
import java.time.Instant;

import static utils.ArrayDisplay.display;

public class InfoBuilder {
    private static final Logger logger = LogManager.getLogger(InfoBuilder.class);
    public static MessageEmbed getInfoEmbed (String url) throws HttpStatusException {
        try {
            EmbedBuilder info = new EmbedBuilder();
            info.setColor(Color.BLACK);
            info.setDescription("by " + WordUtils.capitalize(display(SoupPitcher.getAuthors(url))));
            info.setTimestamp(Instant.now());
            info.setTitle(SoupPitcher.getTitle(url), url);
            info.addField("Language", SoupPitcher.getLanguage(url), true);
            info.addField("Favorites", "" + SoupPitcher.getFaves(url), true);
            info.addField("Parody", display(SoupPitcher.getParodies(url)), true);
            info.addField("Characters", display(SoupPitcher.getChars(url)), true);
            info.addField("Tags", display(SoupPitcher.getTags(url)), false);
            info.setAuthor("Doujin Info", null, "https://i.redd.it/fkg9yip5yyl21.png");
            info.setFooter("Built by Stinggyray#1000", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
            info.setImage(SoupPitcher.getPageLink(url, "1"));

            return info.build();
        } catch(HttpStatusException e){
            logger.info("Building the info embed threw a connection exception");
            logger.info("HTTP status code: " + e.getStatusCode());
            throw e;
        }
    }
}