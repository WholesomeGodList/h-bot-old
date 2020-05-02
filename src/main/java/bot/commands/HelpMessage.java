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
						>searcheh: >search but for e-hentai (significantly faster)
						>deepsearcheh: >deepsearch but for e-hentai (significantly faster)
						>searchhelp: Tips for searching with H-Bot
						```
						-n: non-restrictive (works for both search and deepsearch) (no longer blocks warning tags, just non-wholesome tags)
						```
						"""
				, false);
		helpMsg.addField("Send any bugs / suggestions to Stinggyray#1000", "This bot is pretty much a finished product, but bug reports are always welcome!", false);
		helpMsg.setTimestamp(Instant.now());

		return helpMsg.build();
	}

	public static MessageEmbed getSearchHelpEmbed() {
		EmbedBuilder searchHelpMsg = new EmbedBuilder();
		searchHelpMsg.setColor(Color.BLACK);
		searchHelpMsg.setAuthor("H-Bot", null, "https://i.redd.it/fkg9yip5yyl21.png");
		searchHelpMsg.setTitle("Searching Help");
		searchHelpMsg.setFooter("Built by Stinggyray#1000", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
		searchHelpMsg.addField("**Maximize results by being specific!**",
				"""
						The bot searches by checking a preset amount of doujins (to avoid overloading the nhentai website).
						The more specific your query is,the likelier it is that the bot will find a lot of results.
						For maximum search results in a short period of time, try making your query more specific.
						For example, adding `sole male` and `sole female` to your search will make it a lot more likely
						that the bot returns a lot of results (as those are usually vanilla / wholesome).
						""".replaceAll("\n", " ")
					+ "\n\n(Don't bother specifying `english` though - the bot does that for you already.)", false);
		searchHelpMsg.addField("**If you're searching something with warning tags, you might want to use -n**",
				"""
						If your query is likely to contain multiple warning tags (see the list by doing `>badtags`) (which are tags that some people
						may find objectionable), instead of doing `>search [query]`, consider doing `>search -n [query]`. This will
						turn on non-restrictive mode, meaning that warning tags are omitted from the tag checking process.
						""".replaceAll("\n", " ") + "\n\n" +
						"""
						However, if your query is only ONE warning tag, don't bother using -n, because if your query explicitly
						contains something that is a warning tag, the tag checker will omit that tag, and the search will
						still return results.
						""".replaceAll("\n", " "), false);
		searchHelpMsg.addField("**E-Hentai is much faster**",
				"""
						Because e-hentai has an actual API, any e-hentai searches can be executed an entire page at a time,
						meaning that your results will come a LOT faster. If you don't like waiting, and don't mind finding
						the nhentai equivalent of e-hentai doujins / you use e-hentai, consider using >searcheh instead.
						""".replaceAll("\n", " "), false);
		searchHelpMsg.addField("**Don't repeat queries...**",
				"""
						The results for a query will be the same every time it's run (except when new doujins are uploaded
						and enter the most popular). Don't bother rerunning any of them - you won't get new results.
						""".replaceAll("\n", " "), false);
		searchHelpMsg.addField("Send any bugs / suggestions to Stinggyray#1000", "This bot is pretty much a finished product, but bug reports are always welcome!", false);
		searchHelpMsg.setTimestamp(Instant.now());

		return searchHelpMsg.build();
	}
}
