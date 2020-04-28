package bot.modules;

import bot.ehentai.EHFetcher;
import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;

import java.awt.*;
import java.io.IOException;

import static utils.ArrayDisplay.display;

public class InfoBuilder {
    private static final Logger logger = LogManager.getLogger(InfoBuilder.class);
    public static MessageEmbed getInfoEmbed (String url) throws IOException {
        if(url.contains("nhentai")) {
            try {
                SoupPitcher infoFetcher = new SoupPitcher(url);
                EmbedBuilder info = new EmbedBuilder();
                info.setColor(Color.BLACK);
                info.setDescription("by " + WordUtils.capitalize(display(infoFetcher.getArtists())));
                info.setTimestamp(infoFetcher.getTimePosted());
                info.setTitle(infoFetcher.getTitle(), url);
                info.addField("Language", infoFetcher.getLanguage(), true);
                info.addField("Japanese Title", infoFetcher.getTitleJapanese(), true);
                if(!infoFetcher.getParodies().isEmpty()){
                    info.addField("Parody", display(infoFetcher.getParodies()), true);
                    if(!infoFetcher.getChars().isEmpty()){
                        info.addField("Characters", display(infoFetcher.getChars()), true);
                    }
                }
                info.addField("Tags", display(infoFetcher.getTags()), false);
                info.setAuthor("Doujin Info", null, "https://i.redd.it/fkg9yip5yyl21.png");
                info.setFooter(infoFetcher.getPages() + " pages | Favorites: " + infoFetcher.getFaves() + " | Uploaded:", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
                info.setImage(infoFetcher.getPageLink(1));

                return info.build();
            } catch (HttpStatusException e) {
                logger.info("Building the info embed threw a connection exception");
                logger.info("HTTP status code: " + e.getStatusCode());
                throw e;
            } catch (IOException e) {
                logger.info("An unexpected IOException occurred while building the info embed.");
                e.printStackTrace();
                throw e;
            }
        }
        else {
            try {
                EHFetcher infoFetcher = new EHFetcher(url);
                EmbedBuilder info = new EmbedBuilder();
                info.setColor(Color.BLACK);
                info.setDescription("by " + WordUtils.capitalize(display(infoFetcher.getArtists())));
                info.setTimestamp(infoFetcher.getTimePosted());
                info.setTitle(infoFetcher.getTitle(), url);
                info.addField("Language", WordUtils.capitalize(infoFetcher.getLanguage()), true);
                info.addField("Japanese Title", infoFetcher.getTitleJapanese(), true);
                if(!infoFetcher.getParodies().isEmpty()) {
                    info.addField("Parody", display(infoFetcher.getParodies()), true);
                    if(!infoFetcher.getCharacters().isEmpty()) {
                        info.addField("Characters", display(infoFetcher.getCharacters()), true);
                    }
                }
                info.addField("--------", "", false);
                info.addField("Male Tags", display(infoFetcher.getMaleTags()), true);
                info.addField("Female Tags", display(infoFetcher.getFemaleTags()), true);
                info.addField("Misc Tags", display(infoFetcher.getMiscTags()), true);
                info.setAuthor("Doujin Info", null, "https://i.redd.it/fkg9yip5yyl21.png");
                info.setFooter(infoFetcher.getPages() + " pages | Rating: " + infoFetcher.getRating() + " | Uploaded:", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
                info.setImage(infoFetcher.getThumbnailUrl());

                return info.build();
            } catch (HttpStatusException e) {
                logger.info("Building the info embed threw a connection exception");
                logger.info("HTTP status code: " + e.getStatusCode());
                throw e;
            } catch (IOException e) {
                logger.info("An unexpected IOException occurred while building the info embed.");
                e.printStackTrace();
                throw e;
            }
        }
    }
}