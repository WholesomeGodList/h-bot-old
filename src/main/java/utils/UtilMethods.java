package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class UtilMethods {
    public static boolean isInteger(String s){
        if(s.isEmpty()) {return false;}
        for(int i = 0; i < s.length(); i++){
            if(Character.digit(s.charAt(i), 10) < 0) {return false;}
        }
        return true;
    }

    public static MessageEmbed cursedEmbed(){
        EmbedBuilder badNumbers = new EmbedBuilder();
        ArrayList<String> memeURLs = new ArrayList<>();
        memeURLs.add("https://cdn.discordapp.com/attachments/549278996915814423/609487232692387851/lobster_claw_handjob.jpg");
        memeURLs.add("https://i.imgur.com/W2DCqPt.jpg");
        memeURLs.add("https://i.imgur.com/9PmmmpU.png");
        int randomURLNum = (int)(memeURLs.size() * Math.random());
        String randomURL = memeURLs.get(randomURLNum);
        badNumbers.setImage(randomURL);

        return badNumbers.build();
    }

    public static Message waitForDelete(Message message){
        return waitForDelete(message, 5);
    }

    public static Message waitForDelete(Message message, int secondsDelay){
        message.delete().queueAfter(secondsDelay, TimeUnit.SECONDS);
        return message;
    }
}
