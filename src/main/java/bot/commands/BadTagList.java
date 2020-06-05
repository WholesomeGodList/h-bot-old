package bot.commands;

import bot.modules.TagList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utils.ArrayDisplay.logDisplay;

public class BadTagList {
	public static MessageEmbed getBadTagEmbed() {
		ArrayList<String> badTags = TagList.getTagList();
		Collections.sort(badTags);
		EmbedBuilder badTagEmbed = new EmbedBuilder();
		badTagEmbed.setAuthor("Bad Tags", null, "https://i.redd.it/fkg9yip5yyl21.png");
		List<String> firstHalf = badTags.subList(0, badTags.size() / 3 + 1);
		List<String> secondHalf = badTags.subList(badTags.size() / 3 + 1, 2 * badTags.size() / 3 + 1);
		List<String> thirdHalfLol = badTags.subList(2 * badTags.size() / 3 + 1, badTags.size());
		badTagEmbed.addField("The current non-wholesome tags:", logDisplay(firstHalf), true);
		badTagEmbed.addField(" ", logDisplay(secondHalf), true);
		badTagEmbed.addField(" ", logDisplay(thirdHalfLol), true);

		badTagEmbed.addField("--------", "", false);
		ArrayList<String> nonWholesomeTags = TagList.nonWholesomeTags();
		nonWholesomeTags.add("incest");
		Collections.sort(nonWholesomeTags);
		badTagEmbed.addField("The current warning tags:", logDisplay(nonWholesomeTags.subList(0, nonWholesomeTags.size() / 3 + 1)), true);
		badTagEmbed.addField(" ", logDisplay(nonWholesomeTags.subList(nonWholesomeTags.size() / 3 + 1, 2 * nonWholesomeTags.size() / 3 + 1)), true);
		badTagEmbed.addField(" ", logDisplay(nonWholesomeTags.subList(2 * nonWholesomeTags.size() / 3 + 1, nonWholesomeTags.size())), true);

		badTagEmbed.setFooter("Built by Stinggyray#1000", "https://images.emojiterra.com/twitter/v12/512px/1f914.png");
		badTagEmbed.addField("Illegal tags:", "shotacon\n lolicon", false);

		badTagEmbed.addField("You can help!", "If you have any tags that you want to add to the warning tags list, please tell me! I'll add them if I think they're necessary.", false);

		return badTagEmbed.build();
	}
}
