package bot.nhentai;

import bot.modules.TagChecker;
import org.apache.commons.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.NotFoundException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static utils.UtilMethods.isInteger;

/**
 * Utility class that does all the webscraping of nhentai.
 * All of the methods in here require that the URL be valid! Please do validation before calling these methods.
 */
public class SoupPitcher {
    private static final Logger logger = LogManager.getLogger(SoupPitcher.class);

    public static void main(String[] args) {

    }

    private Document doc;
    private String url;

    public SoupPitcher(String url) throws IOException {
        this.url = url;
        doc = Jsoup.connect(url).get();
    }

    public static int getLatestNumber() {
        try {
            SoupPitcher numFetch = new SoupPitcher("https://nhentai.net");
            ArrayList<String> tagBucket = numFetch.extractLinks();

            for (String cur : tagBucket) {
                if (cur.contains("/g/")) {
                    return Integer.parseInt(cur.substring(cur.indexOf("/g/") + 3, cur.length() - 1).replaceAll("-", " "));
                }
            }
        } catch (HttpStatusException e){
            logger.error("Error happened when getting latest number! This should NOT happen");
            logger.error("HTTP status code: " + e.getStatusCode());
        } catch (IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Latest number not found.");
    }

    public int getPages() {
        Elements divs = doc.select("div").select("#info").select("div");
        ArrayList<String> divBucket = new ArrayList<>();
        for (Element div : divs) {
            divBucket.add(div.text());
        }
        Pattern pattern = Pattern.compile("(\\d+) pages");
        for (String cur : divBucket) {
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()) {
                return parseInt(matcher.group(1));
            }
        }
        throw new NotFoundException("Page count not found.");
    }

    public ArrayList<String> getTags() {
        ArrayList<String> tagBucket = extractLinks();
        ArrayList<String> filteredTagBucket = new ArrayList<>();

        Pattern pattern = Pattern.compile("/tag/(.*)/?");
        for (String cur : tagBucket) {
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()) {
                filteredTagBucket.add(matcher.group(1).replaceAll("-", " "));
            }
        }

        //remove duplicates
        return filteredTagBucket.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> getParodies() {
        ArrayList<String> tagBucket = extractLinks();
        ArrayList<String> filteredTagBucket = new ArrayList<>();

        Pattern pattern = Pattern.compile("/parody/(.*)/?");
        for (String cur : tagBucket) {
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()) {
                filteredTagBucket.add(matcher.group(1).replaceAll("-", " "));
            }
        }

        return filteredTagBucket;
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> tagBucket = extractLinks();
        ArrayList<String> filteredTagBucket = new ArrayList<>();

        Pattern pattern = Pattern.compile("/group/(.*)/?");

        for (String cur : tagBucket) {
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()) {
                filteredTagBucket.add(matcher.group(1).replaceAll("-", " "));
            }
        }
        return filteredTagBucket;
    }

    public ArrayList<String> getArtists(){
        ArrayList<String> tagBucket = extractLinks();
        ArrayList<String> filteredTagBucket = new ArrayList<>();
        Pattern pattern = Pattern.compile("/artist/(.*)/?");

        for (String cur : tagBucket) {
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()) {
                filteredTagBucket.add(matcher.group(1).replaceAll("-", " "));
            }
        }
        return filteredTagBucket;
    }

    public ArrayList<String> getChars(){
        if(getParodies().isEmpty()) {return new ArrayList<>();}
        ArrayList<String> tagBucket = extractLinks();
        ArrayList<String> filteredTagBucket = new ArrayList<String>();

        Pattern pattern = Pattern.compile("/characters/(.*)/?");

        for (String cur : tagBucket) {
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()) {
                filteredTagBucket.add(matcher.group(1).replaceAll("-", " "));
            }
        }
        return filteredTagBucket;
    }

    public String getTitle() {
        Elements links = doc.select("h1");
        return links.first().text();
    }

    public int getFaves() {
        Elements links = doc.select("span");
        for(Element link : links){
            if(link.className().equals("nobold") && isInteger(link.text().substring(1, link.text().length() - 1))){
                return parseInt(link.text().substring(1, link.text().length() - 1));
            }
        }

        throw new NotFoundException("Favorites not found.");
    }

    public String getLanguage() {
        ArrayList<String> tagBucket = extractLinks();
        for(String cur : tagBucket){
            if(cur.contains("language") && !cur.contains("translated")){
                return WordUtils.capitalize(cur.substring(cur.indexOf("language") + 9, cur.length() - 1));
            }
        }

        throw new NotFoundException("Language not found.");
    }

    public String getPageLink(int pageNum) throws HttpStatusException {
        try {
            //make sure the URL is formed correctly (linked to the correct page)
            String newUrl = url;

            if (!newUrl.endsWith("/")) {
                newUrl += "/";
            }
            newUrl += pageNum;

            Document doc = Jsoup.connect(newUrl).get();
            Elements images = doc.select("img");
            ArrayList<String> imageBucket = new ArrayList<String>();
            for (Element cur : images) {
                imageBucket.add(cur.attr("abs:src"));
            }

            for (String cur : imageBucket) {
                if (cur.contains("galleries")) {
                    return cur;
                }
            }
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("No link found.");
    }

    public ArrayList<String> extractLinks() {
        Elements links = doc.select("a[href]");
        ArrayList<String> tagBucket = new ArrayList<>();
        for(Element link : links) {
            tagBucket.add(link.attr("abs:href"));
        }
        return tagBucket;
    }

    public static ArrayList<String> getTopSearchResults(String query, int pages, boolean nonrestrict) {
        String urlQuery = "https://nhentai.net/search/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&sort=popular&page=";
        ArrayList<String> results = new ArrayList<>();
        String newUrlQuery;
        for (int currentPage = 1; currentPage <= pages; currentPage++) {
            try {
                newUrlQuery = urlQuery + currentPage;
                logger.info("Current page: " + currentPage);
                logger.info("Query: " + newUrlQuery);

                SoupPitcher searcher = new SoupPitcher(newUrlQuery);
                ArrayList<String> tagBucket = searcher.extractLinks();

                ArrayList<String> pageResultsBucket = new ArrayList<>();

                for (String cur : tagBucket) {
                    if (cur.contains("/g/")) {
                        pageResultsBucket.add("https://nhentai.net" + cur.substring(cur.indexOf("/g/"), cur.length() - 1));
                    }
                }

                for (String cur : pageResultsBucket) {
                    logger.info("Checking " + cur);
                    SoupPitcher curChecker = new SoupPitcher(cur);
                    if(!nonrestrict) {
                        if (curChecker.getLanguage().equals("English") && TagChecker.wholesomeCheck(curChecker.getTags(), query)) {
                            results.add(cur);
                            logger.info("Wholesome!");
                        }
                    }
                    else {
                        if (curChecker.getLanguage().equals("English") && TagChecker.tagCheck(curChecker.getTags()).isEmpty()) {
                            results.add(cur);
                            logger.info("Wholesome!");
                        }
                    }
                }

                if(pages >= 10){
                    Thread.sleep(50);
                }
                if(pages >= 100){
                    Thread.sleep(100);
                }
            } catch (HttpStatusException e) {
                logger.info("Error: HTTP status " + e.getStatusCode());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}