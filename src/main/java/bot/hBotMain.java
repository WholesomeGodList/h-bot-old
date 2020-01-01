package bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class hBotMain {
    private static final Logger logger = LogManager.getLogger(hBotMain.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static JDA myBot;
    public static void main(String[] args) {
        try {
            logger.info("Log file found!");
            myBot = new JDABuilder(BotConfig.BOT_TOKEN).addEventListeners(new hListener()).build();
            myBot.awaitReady();
            myBot.setAutoReconnect(true);
            logger.info("Bot has started!");
            myBot.getPresence().setActivity(Activity.watching("hentai | " + BotConfig.PREFIX + "help"));

            scheduler.scheduleAtFixedRate(new hHook(), 0, 15, TimeUnit.MINUTES);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static JDA getMyBot(){
        return myBot;
    }
}