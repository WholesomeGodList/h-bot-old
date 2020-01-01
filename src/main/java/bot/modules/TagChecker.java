package bot.modules;

import java.util.ArrayList;

public class TagChecker {
    public static ArrayList<String> tagCheck(ArrayList<String> tags){
        ArrayList<String> badTags = TagList.getTagList();
        ArrayList<String> reallyBadTags = new ArrayList<>();

        return tagCompare(tags, badTags, reallyBadTags);
    }

    public static ArrayList<String> tagCheckWithWarnings(ArrayList<String> tags){
        ArrayList<String> badTags = TagList.getTagList();
        badTags.addAll(TagList.nonWholesomeTags());
        badTags.addAll(TagList.slightlyQuestionableTags());
        ArrayList<String> reallyBadTags = new ArrayList<String>();

        return tagCompare(tags, badTags, reallyBadTags);
    }

    private static ArrayList<String> tagCompare(ArrayList<String> tags, ArrayList<String> badTags, ArrayList<String> reallyBadTags) {
        reallyBadTags.add("lolicon");
        reallyBadTags.add("shotacon");

        ArrayList<String> discoveredBadTags = new ArrayList<String>();
        ArrayList<String> discoveredReallyBadTags = new ArrayList<String>();
        for(String cur : tags){
            for(String cur2 : badTags){
                if(cur.equals(cur2)){
                    discoveredBadTags.add(cur);
                }
            }
            for(String cur3 : reallyBadTags){
                if(cur.equals(cur3)){
                    discoveredReallyBadTags.add(cur);
                }
            }
        }
        if(!discoveredReallyBadTags.isEmpty()){
            return discoveredReallyBadTags;
        }
        return discoveredBadTags;
    }

    public static boolean wholesomeCheck(ArrayList<String> tags){
        ArrayList<String> nonWholesomeTags = TagList.nonWholesomeTags();
        return wholesomeCompare(tags, nonWholesomeTags);
    }

    public static boolean wholesomeCheck(ArrayList<String> tags, String query){
        ArrayList<String> nonWholesomeTags = TagList.nonWholesomeTagsWithoutQuery(query);
        return wholesomeCompare(tags, nonWholesomeTags);
    }

    private static boolean wholesomeCompare(ArrayList<String> tags, ArrayList<String> nonWholesomeTags) {
        if(tagCheck(tags).isEmpty()){
            for(String cur : tags) {
                for (String cur2 : nonWholesomeTags) {
                    if (cur.equals(cur2)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
