package bot.modules;

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

    public static void main(String[] args) throws HttpStatusException{
        System.out.println(getPages("https://nhentai.net/g/298877/"));
    }

    public static int getLatestNumber() {
        try {
            ArrayList<String> tagBucket = extractLinks("https://nhentai.net");

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

    public static int getPages(String url) throws HttpStatusException {
        try {
            Document doc = Jsoup.connect(url).get();
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
        } catch (HttpStatusException e) {
            throw e;
        } catch (IOException e){
            e.printStackTrace();
        }
        throw new NotFoundException("Page count not found.");
    }

    public static ArrayList<String> getTags(String url)throws HttpStatusException{
        try {
            ArrayList<String> tagBucket = extractLinks(url);
            ArrayList<String> filteredTagBucket = new ArrayList<>();
            for(String cur : tagBucket){
                if(cur.contains("/tag/")){
                    filteredTagBucket.add(cur.substring(cur.indexOf("tag/") + 4, cur.length() - 1).replaceAll("-", " "));
                }
            }
            return filteredTagBucket.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Tags not found.");
    }

    public static ArrayList<String> getParodies(String url)throws HttpStatusException{
        try {
            ArrayList<String> tagBucket = extractLinks(url);
            ArrayList<String> filteredTagBucket = new ArrayList<String>();
            for(String cur : tagBucket){
                if(cur.contains("/parody/")){
                    filteredTagBucket.add(cur.substring(cur.indexOf("ody/") + 4, cur.length() - 1).replaceAll("-", " "));
                }
            }
            return filteredTagBucket;
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Parody not found.");
    }

    public static ArrayList<String> getGroups(String url)throws HttpStatusException{
        try {
            ArrayList<String> tagBucket = extractLinks(url);
            ArrayList<String> filteredTagBucket = new ArrayList<String>();
            for(String cur : tagBucket){
                if(cur.contains("/group/")){
                    filteredTagBucket.add(cur.substring(cur.indexOf("oup/") + 4, cur.length() - 1).replaceAll("-", " "));
                }
            }
            return filteredTagBucket;
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Groups not found.");
    }

    public static ArrayList<String> getAuthors(String url)throws HttpStatusException{
        try {
            ArrayList<String> tagBucket = extractLinks(url);
            ArrayList<String> filteredTagBucket = new ArrayList<String>();
            for(String cur : tagBucket){
                if(cur.contains("/artist/")){
                    filteredTagBucket.add(cur.substring(cur.indexOf("ist/") + 4, cur.length() - 1).replaceAll("-", " "));
                }
            }
            return filteredTagBucket;
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Authors not found.");
    }

    public static ArrayList<String> getChars(String url)throws HttpStatusException{
        try {
            if(getParodies(url).isEmpty()) {return new ArrayList<>();}

            ArrayList<String> tagBucket = extractLinks(url);
            ArrayList<String> filteredTagBucket = new ArrayList<String>();
            for(String cur : tagBucket){
                if(cur.contains("/character/")){
                    filteredTagBucket.add(cur.substring(cur.indexOf("ter/") + 4, cur.length() - 1).replaceAll("-", " "));
                }
            }
            return filteredTagBucket;
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Characters not found.");
    }

    public static String getTitle(String url)throws HttpStatusException{
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("h1");
            return links.first().text();
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Title not found.");
    }

    public static int getFaves(String url)throws HttpStatusException{
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("span");
            for(Element link : links){
                if(link.className().equals("nobold") && isInteger(link.text().substring(1, link.text().length() - 1))){
                    return parseInt(link.text().substring(1, link.text().length() - 1));
                }
            }
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Favorites not found.");
    }

    public static String getLanguage(String url) throws HttpStatusException{
        try {
            ArrayList<String> tagBucket = extractLinks(url);
            for(String cur : tagBucket){
                if(cur.contains("language") && !cur.contains("translated")){
                    return WordUtils.capitalize(cur.substring(cur.indexOf("language") + 9, cur.length() - 1));
                }
            }
        } catch(HttpStatusException e){
            throw e;
        } catch(IOException e){
            e.printStackTrace();
        }

        throw new NotFoundException("Language not found.");
    }

    public static String getPageLink(String url, String pageNum) throws HttpStatusException{
        try {
            //make sure the URL is formed correctly (linked to the correct page)
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += pageNum;

            Document doc = Jsoup.connect(url).get();
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

    public static ArrayList<String> extractLinks(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        ArrayList<String> tagBucket = new ArrayList<>();
        for(Element link : links) {
            tagBucket.add(link.attr("abs:href"));
        }
        return tagBucket;
    }

    public static ArrayList<String> getTopSearchResult(String query, int pages, boolean nonrestrict) {
        String urlQuery = "https://nhentai.net/search/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&sort=popular&page=";
        ArrayList<String> results = new ArrayList<>();
        String newUrlQuery;
        for (int currentPage = 1; currentPage <= pages; currentPage++) {
            try {
                newUrlQuery = urlQuery + currentPage;
                logger.info("Current page: " + currentPage);
                logger.info("Query: " + newUrlQuery);

                ArrayList<String> tagBucket = extractLinks(newUrlQuery);

                ArrayList<String> pageResultsBucket = new ArrayList<>();

                for (String cur : tagBucket) {
                    if (cur.contains("/g/")) {
                        pageResultsBucket.add("https://nhentai.net" + cur.substring(cur.indexOf("/g/"), cur.length() - 1));
                    }
                }

                for (String cur : pageResultsBucket) {
                    logger.info("Checking " + cur);
                    if(!nonrestrict) {
                        if (getLanguage(cur).equals("English") && TagChecker.wholesomeCheck(getTags(cur), query)) {
                            results.add(cur);
                            logger.info("Wholesome!");
                        }
                    }
                    else {
                        if (getLanguage(cur).equals("English") && TagChecker.tagCheck(getTags(cur)).isEmpty()) {
                            results.add(cur);
                            logger.info("Wholesome!");
                        }
                    }
                }

                if(pages >= 10){
                    Thread.sleep(50);
                }
                if(pages >= 100){
                    Thread.sleep(50);
                }
            } catch (HttpStatusException e) {
                logger.info("Error: HTTP status " + e.getStatusCode());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(results.isEmpty()){
            return new ArrayList<>();
        }
        return results;
    }
}
