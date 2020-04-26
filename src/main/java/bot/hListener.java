package bot;

import bot.commands.*;
import bot.ehentai.EHSearch;
import bot.modules.BookTracker;
import bot.modules.BotAlert;
import bot.modules.InfoBuilder;
import bot.nhentai.SoupPitcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static bot.modules.BotAlert.createAlertEmbed;

public class hListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(hListener.class);
    private static ArrayList<BookTracker> openBooks;
    private static int currentSearches;
    private static int currentEHSearches;

    public hListener() {
        currentSearches = 0;
        openBooks = new ArrayList<>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //Don't respond to bots.
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        //If it isn't a command, return.
        if(content.isEmpty() || !content.substring(0, 1).equals(BotConfig.PREFIX)){
            return;
        }

        content = content.substring(1).toLowerCase();
        ArrayList<String> args = new ArrayList<>(Arrays.asList(content.split(" ")));

        //If the first argument isn't a command, return.
        if(!IsCommand.isCommand(args.get(0))){
            return;
        }

        //If the channel isn't NSFW... inform the user, then return.
        if(!event.getTextChannel().isNSFW()){
            channel.sendMessage(BotAlert.createAlertEmbed("Bot Alert", "This channel is not NSFW", "This bot can only be used in NSFW channels!")).queue();
            return;
        }

        logger.info("Command received: " + content);

        switch(args.get(0)){
            case "help" -> {
                channel.sendTyping().complete();
                channel.sendMessage(HelpMessage.getHelpEmbed()).queue();
            }
            case "supportedsites", "sites" -> {
                channel.sendTyping().complete();
                channel.sendMessage(SupportedSites.getSitesEmbed()).queue();
            }
            case "tags" -> {
                channel.sendTyping().complete();
                TagMessage.sendTags(channel, args);
            }
            case "badtags", "warningtags" -> {
                channel.sendTyping().complete();
                channel.sendMessage(createAlertEmbed("H-Bot", "Sent you a DM containing the bad tags!")).complete();

                User eventCreator = event.getAuthor();
                PrivateChannel messagePath = eventCreator.openPrivateChannel().complete();

                messagePath.sendMessage(BadTagList.getBadTagEmbed()).queue();
                messagePath.close().queue();
            }

            case "getpage" -> {
                channel.sendTyping().complete();
                GetPage.sendPageLink(channel, args);
            }
            case "random" -> {
                channel.sendTyping().complete();
                Randomizer.sendRandom(channel, args, event.getAuthor());
            }
            case "info" -> {
                channel.sendTyping().complete();
                DoujinInfo.sendInfo(channel, args, event.getAuthor());
            }

            case "deepsearch", "search" -> {
                if (currentSearches >= 3) {
                    channel.sendMessage("There are already 3 searches happening. Please wait before sending more.").queue();
                }
                currentSearches++;

                Search searchingThread = new Search(args, channel, event);
                searchingThread.start();
            }
            case "searcheh", "deepsearcheh" -> {
                if (currentEHSearches >= 2) {
                    channel.sendMessage("There are already 2 searches happening. Please wait before sending more.").queue();
                }
                currentEHSearches++;

                EHSearch ehSearchingThread = new EHSearch(args, channel, event);
                ehSearchingThread.start();
            }

            case "read" -> {
                channel.sendTyping().queue();
                Read.read(channel, args, event.getAuthor());
            }
            case "addhook" -> {
                if(event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    channel.sendMessage("New doujins from the telecom will now be relayed here").queue();
                    hHook.addHookChannel(event.getGuild().getId(), event.getChannel().getId());
                }
                else {
                    channel.sendMessage(BotAlert.createAlertEmbed("Bot Alert", "You do not have the permission Manage Server!")).queue();
                }
            }
            case "removehook" -> {
                if(event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    channel.sendMessage("New doujins from the telecom will no longer be relayed here").queue();
                    hHook.removeHookChannel(event.getGuild().getId(), event.getChannel().getId());
                }
                else {
                    channel.sendMessage(BotAlert.createAlertEmbed("Bot Alert", "You do not have the permission Manage Server!")).queue();
                }
            }
            default -> logger.info("Command not recognized.");
        }
    }

    //------------------------------------------
    //Book shenanigans start below

    public static void register(BookTracker book){
        openBooks.add(book);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        BookTracker curBook = null;
        for(BookTracker cur : openBooks){
            if(cur.getMessageId().equals(event.getMessageId())){
                curBook = cur;
                break;
            }
        }
        if(curBook == null){
            return;
        }
        if(event.getUser().isBot()){
            return;
        }
        MessageChannel channel = event.getChannel();
        MessageReaction rxn = event.getReaction();

        String messageId = curBook.getMessageId();
        String authorId = curBook.getAuthorId();
        String readLink = curBook.getReadLink();
        int currentPg = curBook.getCurrentPg();

        logger.info("Reaction received: " + event.getReaction().getReactionEmote().getName());

        fixReactions(event, curBook);

        switch(curBook.getMode()) {
            case READ -> {
                if (event.getReaction().getReactionEmote().getName().equals("❌") && messageId.equals(event.getMessageId()) && authorId.equals(event.getMember().getId())) {
                    logger.info("X detected, closing book");
                    channel.deleteMessageById(messageId).queue();
                    openBooks.remove(curBook);
                }
                if (event.getReaction().getReactionEmote().getName().equals("⬅") && messageId.equals(event.getMessageId()) && authorId.equals(event.getMember().getId())) {
                    try {
                        curBook.setCurrentPg(currentPg - 1);
                        currentPg = curBook.getCurrentPg();
                        ReactionPaginationAction test = rxn.retrieveUsers();
                        for (User current : test) {
                            if (!current.isBot()) {
                                rxn.removeReaction(current).queue();
                            }
                        }
                        EmbedBuilder book = new EmbedBuilder();

                        SoupPitcher pageFetcher = new SoupPitcher(readLink);
                        book.setTitle("Page " + currentPg);
                        book.setImage(pageFetcher.getPageLink(currentPg));

                        channel.editMessageById(messageId, book.build()).queue();
                        return;
                    } catch (HttpStatusException e) {
                        curBook.setCurrentPg(currentPg + 1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if (event.getReaction().getReactionEmote().getName().equals("➡") && messageId.equals(event.getMessageId()) && authorId.equals(event.getMember().getId())) {
                    try {
                        logger.info("Trying to advance pages");
                        curBook.setCurrentPg(currentPg + 1);
                        currentPg = curBook.getCurrentPg();
                        ReactionPaginationAction test = rxn.retrieveUsers();
                        for (User current : test) {
                            if (!current.isBot()) {
                                rxn.removeReaction(current).queue();
                            }
                        }
                        EmbedBuilder book = new EmbedBuilder();

                        SoupPitcher pageFetcher = new SoupPitcher(readLink);
                        book.setTitle("Page " + currentPg);
                        book.setImage(pageFetcher.getPageLink(currentPg));

                        channel.editMessageById(messageId, book.build()).queue();
                    } catch (HttpStatusException e) {
                        curBook.setCurrentPg(currentPg - 1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            case CONFIRM -> {
                if (event.getReaction().getReactionEmote().getName().equals("❌") && messageId.equals(event.getMessageId()) && authorId.equals(event.getMember().getId())) {
                    logger.info("X detected, closing dialogue");
                    channel.deleteMessageById(messageId).queue();
                    openBooks.remove(curBook);
                } else if (authorId.equals(event.getMember().getId()) && event.getReaction().getReactionEmote().getName().equals("✅")) {
                    try {
                        openBooks.remove(curBook);
                        channel.deleteMessageById(messageId).queue();
                        channel.sendTyping().complete();
                        channel.sendMessage(InfoBuilder.getInfoEmbed(readLink)).queue();
                    } catch (HttpStatusException e){
                        logger.info("Error in confirmation dialogue info embed building: status code " + e.getStatusCode());
                    } catch (IOException e){
                        logger.info("Something wacky happened:");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void fixReactions (MessageReactionAddEvent event, BookTracker curBook){
        MessageReaction rxn = event.getReaction();
        if(curBook.getAuthorId() != null && !curBook.getAuthorId().equals(Objects.requireNonNull(event.getMember()).getId())){
            ReactionPaginationAction test = rxn.retrieveUsers();
            for(User current : test){
                if(!current.isBot()){
                    rxn.removeReaction(current).queue();
                }
            }
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event){
        for(BookTracker cur : openBooks) {
            if(event.getMessageId().equals(cur.getMessageId())) {
                logger.info("Deletion detected, closing book");
                openBooks.remove(cur);
            }
        }
    }

    public static void decrementSearches(){currentSearches--;}
    public static void decrementEHSearches(){currentEHSearches--;}
}
