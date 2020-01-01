package bot.commands;

import java.util.ArrayList;

public class IsCommand {
    public static boolean isCommand(String query){
        ArrayList<String> allCommands = new ArrayList<>();
        allCommands.add("help");
        allCommands.add("tags");
        allCommands.add("getpage");
        allCommands.add("info");
        allCommands.add("read");
        allCommands.add("badtags");
        allCommands.add("warningtags");
        allCommands.add("supportedsites");
        allCommands.add("sites");
        allCommands.add("random");
        allCommands.add("search");
        allCommands.add("deepsearch");
        allCommands.add("addhook");
        allCommands.add("removehook");

        return allCommands.contains(query);
    }
}
