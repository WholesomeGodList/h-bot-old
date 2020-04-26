package bot.modules;

import java.util.ArrayList;
import java.util.Arrays;

public class TagList {
    private static final ArrayList<String> badTags = new ArrayList<>(
            Arrays.asList (
                    "netorare", "netori", "scat", "bestiality", "gigantic", "drugs", "blackmail", "horse", "fisting",
                    "vore", "guro", "nose hook", "urination", "blood", "cheating", "dog", "pig", "corruption", "mind control",
                    "vomit", "bbm", "cannibalism", "tentacles", "rape", "snuff", "moral degeneration", "mind break", "humiliation",
                    "chikan", "ryona", "piss drinking", "prostitution", "cum bath", "infantilism", "unbirth", "abortion",
                    "eye penetration", "urethra insertion", "chloroform", "parasite", "public use", "petrification", "necrophilia",
                    "brain fuck", "daughter", "torture", "birth"
            )
    );

    private static final ArrayList<String> nonWholesomeTags = new ArrayList<>(
            Arrays.asList (
                    "amputee", "futanari", "gender bender", "human on furry", "group", "lactation", "femdom",
                    "ffm threesome", "double penetration", "gag", "harem", "collar", "strap-on", "inflation", "mmf threesome", "enema",
                    "bukkake", "bbw", "dick growth", "big areolae", "huge breasts", "slave", "gaping", "shemale", "pegging",
                    "triple penetration", "prolapse", "human pet", "foot licking", "milking", "bondage", "multiple penises",
                    "asphyxiation", "stuck in wall", "human cattle", "clit growth", "ttf threesome", "phimosis", "glory hole"
            )
    );
    public static ArrayList<String> getTagList() {
        return new ArrayList<>(badTags);
    }

    public static ArrayList<String> nonWholesomeTags() {
        return new ArrayList<>(nonWholesomeTags);
    }

    public static ArrayList<String> nonWholesomeTagsWithoutQuery(String query){
        ArrayList<String> nonWT = new ArrayList<>(nonWholesomeTags());
        for(int i = nonWT.size() - 1; i >= 0; i--) {
            if (query.contains(nonWT.get(i))){
                nonWT.remove(i);
            }
        }
        return nonWT;
    }

    public static ArrayList<String> slightlyQuestionableTags(){
        ArrayList<String> bruh = new ArrayList<>();
        bruh.add("incest");
        return bruh;
    }
}
