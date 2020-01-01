package bot.modules;

import java.util.ArrayList;

public class TagList {
    public static ArrayList<String> getTagList() {
        ArrayList<String> badTags = new ArrayList<String>();
        badTags.add("netorare");
        badTags.add("netori");
        badTags.add("scat");
        badTags.add("bestiality");
        badTags.add("gigantic");
        badTags.add("drugs");
        badTags.add("blackmail");
        badTags.add("horse");
        badTags.add("fisting");
        badTags.add("vore");
        badTags.add("guro");
        badTags.add("nose hook");
        badTags.add("urination");
        badTags.add("blood");
        badTags.add("cheating");
        badTags.add("dog");
        badTags.add("pig");
        badTags.add("corruption");
        badTags.add("mind control");
        badTags.add("vomit");
        badTags.add("bbm");
        badTags.add("cannibalism");
        badTags.add("tentacles");
        badTags.add("rape");
        badTags.add("snuff");
        badTags.add("moral degeneration");
        badTags.add("mind break");
        badTags.add("humiliation");
        badTags.add("chikan");
        badTags.add("ryona");
        badTags.add("piss drinking");
        badTags.add("prostitution");
        badTags.add("cum bath");
        badTags.add("infantilism");
        badTags.add("unbirth");
        badTags.add("abortion");
        badTags.add("eye penetration");
        badTags.add("urethra insertion");
        badTags.add("chloroform");
        return badTags;
    }

    public static ArrayList<String> nonWholesomeTags() {
        ArrayList<String> nonWholesomeTags = new ArrayList<>();
        nonWholesomeTags.add("amputee");
        nonWholesomeTags.add("futanari");
        nonWholesomeTags.add("gender bender");
        nonWholesomeTags.add("daughter");
        nonWholesomeTags.add("human on furry");
        nonWholesomeTags.add("group");
        nonWholesomeTags.add("lactation");
        nonWholesomeTags.add("femdom");
        nonWholesomeTags.add("ffm threesome");
        nonWholesomeTags.add("double penetration");
        nonWholesomeTags.add("gag");
        nonWholesomeTags.add("harem");
        nonWholesomeTags.add("collar");
        nonWholesomeTags.add("strap-on");
        nonWholesomeTags.add("inflation");
        nonWholesomeTags.add("mmf threesome");
        nonWholesomeTags.add("enema");
        nonWholesomeTags.add("bukkake");
        nonWholesomeTags.add("bbw");
        nonWholesomeTags.add("dick growth");
        nonWholesomeTags.add("big areolae");
        nonWholesomeTags.add("huge breasts");
        nonWholesomeTags.add("slave");
        nonWholesomeTags.add("gaping");
        nonWholesomeTags.add("torture");
        nonWholesomeTags.add("shemale");
        nonWholesomeTags.add("pegging");
        nonWholesomeTags.add("triple penetration");
        nonWholesomeTags.add("birth");
        nonWholesomeTags.add("prolapse");
        nonWholesomeTags.add("human pet");
        nonWholesomeTags.add("foot licking");
        nonWholesomeTags.add("milking");
        nonWholesomeTags.add("bondage");
        nonWholesomeTags.add("multiple penises");
        nonWholesomeTags.add("asphyxiation");
        nonWholesomeTags.add("stuck in wall");
        nonWholesomeTags.add("brain fuck");
        nonWholesomeTags.add("human cattle");
        nonWholesomeTags.add("clit growth");
        nonWholesomeTags.add("ttf threesome");
        nonWholesomeTags.add("phimosis");
        nonWholesomeTags.add("parasite");
        nonWholesomeTags.add("public use");
        nonWholesomeTags.add("glory hole");
        return nonWholesomeTags;
    }

    public static ArrayList<String> nonWholesomeTagsWithoutQuery(String query){
        ArrayList<String> nonWT = nonWholesomeTags();
        nonWT.remove(query);
        return nonWT;
    }

    public static ArrayList<String> slightlyQuestionableTags(){
        ArrayList<String> bruh = new ArrayList<>();
        bruh.add("incest");
        return bruh;
    }
}
