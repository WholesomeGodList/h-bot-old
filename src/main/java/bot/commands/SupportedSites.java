package bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class SupportedSites {
	public static MessageEmbed getSitesEmbed() {
		EmbedBuilder sSites = new EmbedBuilder();
		sSites.setColor(Color.BLACK);
		sSites.setAuthor("H-Bot", null, "https://i.redd.it/fkg9yip5yyl21.png");
		sSites.setTitle("For easily accessing info about doujins");
		sSites.setFooter("Built by Stinggyray#1000", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
		sSites.addField("Currently supported sites",
				"""
						nhentai
						e(x)hentai (info and search)
						""", false);
		sSites.setTimestamp(Instant.now());

		return sSites.build();
	}

	public static boolean isSupported(String query) {
		ArrayList<String> supportedSites = new ArrayList<>();

		query = query.replaceAll("http://", "https://");

		supportedSites.add("https://nhentai.net");
		supportedSites.add("https://e-hentai.org");
		supportedSites.add("https://exhentai.org");
		for (String cur : supportedSites) {
			if (query.contains(cur)) {
				return true;
			}
		}
		return false;
	}
}
