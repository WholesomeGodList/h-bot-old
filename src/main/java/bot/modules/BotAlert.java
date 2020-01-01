package bot.modules;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BotAlert {
    public static MessageEmbed createAlertEmbed(String header, String content){
        EmbedBuilder alert = new EmbedBuilder();
        alert.setAuthor(header, null, "https://i.redd.it/fkg9yip5yyl21.png");
        alert.setDescription(content);

        return alert.build();
    }

    public static MessageEmbed createAlertEmbed(String header, String title, String content){
        EmbedBuilder alert = new EmbedBuilder();
        alert.setAuthor(header, null, "https://i.redd.it/fkg9yip5yyl21.png");
        alert.setTitle(title);
        alert.setDescription(content);

        return alert.build();
    }
}
