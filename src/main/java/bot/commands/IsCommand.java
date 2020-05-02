package bot.commands;

import java.util.ArrayList;
import java.util.Arrays;

public class IsCommand {
	private static final ArrayList<String> allCommands = new ArrayList<>(
			Arrays.asList(
					"help", "tags", "getpage", "info", "read", "badtags", "warningtags",
					"supportedsites", "sites", "random", "search", "deepsearch", "addhook", "removehook",
					"searcheh", "deepsearcheh", "searchhelp"
			)
	);

	public static boolean isCommand(String query) {
		return allCommands.contains(query);
	}
}
