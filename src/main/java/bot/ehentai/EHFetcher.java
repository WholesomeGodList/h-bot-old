package bot.ehentai;

import bot.modules.TagChecker;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.NotFoundException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EHFetcher {
    private static final Logger logger = LogManager.getLogger(EHFetcher.class);
    public enum Category {
        DOUJINSHI { public String toString() { return "Doujinshi";}},
        MANGA { public String toString() { return "Manga";}},
        ARTIST_CG { public String toString() { return "Artist CG";}},
        GAME_CG { public String toString() { return "Game CG";}},
        WESTERN { public String toString() { return "Western";}},
        IMAGE_SET { public String toString() { return "Image Set";}},
        NON_H { public String toString() { return "Non-H";}},
        COSPLAY { public String toString() { return "Cosplay";}},
        ASIAN_PORN { public String toString() { return "Asian Porn";}},
        MISC { public String toString() { return "Misc";}},
        PRIVATE { public String toString() { return "Private";}}
    }

    private int galleryId;
    private String galleryToken;
    private JSONObject galleryMeta;
    private ArrayList<String> tags;

    public EHFetcher(String url) throws IOException {
        if(!url.endsWith("/")){
            url += "/";
        }
        Pattern galleryPattern = Pattern.compile("https?://e[x\\-]hentai\\.org/g/(\\d+)/([\\da-f]+)/");
        Pattern pagePattern = Pattern.compile("https?://e[x\\-]hentai\\.org/s/([\\da-f]+)/(\\d+)-(\\d+)/");

        Matcher galleryMatcher = galleryPattern.matcher(url);
        Matcher pageMatcher = pagePattern.matcher(url);

        if(galleryMatcher.find()){
            galleryId = Integer.parseInt(galleryMatcher.group(1));
            galleryToken = galleryMatcher.group(2);
        }
        else if(pageMatcher.find()){
            String pageId = pageMatcher.group(1);
            galleryId = Integer.parseInt(pageMatcher.group(2));
            int pageNum = Integer.parseInt(pageMatcher.group(3));

            JSONObject payload = new JSONObject();
            payload.put("method", "gtoken");

            JSONArray pageContainer = new JSONArray();
            JSONArray page = new JSONArray();

            page.put(galleryToken);
            page.put(pageId);
            page.put(pageNum);

            pageContainer.put(page);

            payload.put("pagelist", pageContainer);

            JSONObject galleryInfoFinder = ehAPIRequest(payload);

            JSONObject galleryInfo = galleryInfoFinder.getJSONArray("tokenlist").getJSONObject(0);
            galleryToken = galleryInfo.getString("token");
        }
        else {
            logger.info("Improper link. Neither regex matched.");
            throw new HttpStatusException("Not proper link", 404, url);
        }
        JSONObject payload = new JSONObject();
        payload.put("method", "gdata");

        JSONArray gidContainer = new JSONArray();
        JSONArray gid = new JSONArray();
        gid.put(galleryId);
        gid.put(galleryToken);

        gidContainer.put(gid);

        payload.put("gidlist", gidContainer);
        payload.put("namespace", 1);

        System.out.println(payload.toString(4));

        galleryMeta = ehAPIRequest(payload);

        tags = new ArrayList<>();
        for (Object cur : galleryMeta.getJSONArray("tags")) {
            tags.add(cur.toString());
        }
    }

    public EHFetcher(JSONObject galleryMeta){
        this.galleryMeta = galleryMeta;

        tags = new ArrayList<>();
        for (Object cur : galleryMeta.getJSONArray("tags")) {
            tags.add(cur.toString());
        }
    }

    public static JSONObject ehAPIRequest(JSONObject payload) throws IOException {
        StringEntity payloadEntity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
        CloseableHttpClient connect = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://api.e-hentai.org/api.php");
        post.setEntity(payloadEntity);

        try {
            CloseableHttpResponse apiResponse = connect.execute(post);

            HttpEntity entity = apiResponse.getEntity();
            JSONObject jsonResponse = new JSONObject(new JSONTokener(entity.getContent()));
            jsonResponse = jsonResponse.getJSONArray("gmetadata").getJSONObject(0);

            EntityUtils.consume(entity);

            return jsonResponse;
        } finally {
            connect.close();
        }
    }

    public Instant getTimePosted(){
        long time = Long.parseLong(galleryMeta.getString("posted"));
        return Instant.ofEpochSecond(time);
    }

    public Category getCategory(){
        String categoryName = galleryMeta.getString("category");
        for(Category cur : Category.values()){
            if(cur.toString().equals(categoryName)){
                return cur;
            }
        }
        throw new NotFoundException("Category not found.");
    }

    public String getTitle(){
        return galleryMeta.getString("title");
    }

    public String getTitleJapanese(){
        return galleryMeta.getString("title_jpn");
    }

    public String getUploader(){
        return galleryMeta.getString("uploader");
    }

    public String getThumbnailUrl(){
        return galleryMeta.getString("thumb");
    }

    public double getRating(){
        return Double.parseDouble(galleryMeta.getString("rating"));
    }

    public ArrayList<String> getArtists(){
        return tagSearch(Pattern.compile("artist:(.*)$"));
    }

    public ArrayList<String> getMaleTags(){
        return tagSearch(Pattern.compile("male:(.*)$"));
    }

    public ArrayList<String> getFemaleTags(){
        return tagSearch(Pattern.compile("female:(.*)$"));
    }

    public ArrayList<String> getMiscTags(){
        return tagSearch(Pattern.compile("^([^:]*)$"));
    }

    public ArrayList<String> getTags(){
        ArrayList<String> tags = new ArrayList<String>();
        tags.addAll(getMaleTags());
        tags.addAll(getFemaleTags());
        tags.addAll(getMiscTags());

        return tags;
    }

    public ArrayList<String> getGroups(){
        return tagSearch(Pattern.compile("group:(.*)$"));
    }

    public ArrayList<String> getParodies(){
        return tagSearch(Pattern.compile("parody:(.*)$"));
    }

    public ArrayList<String> getCharacters(){
        return tagSearch(Pattern.compile("character:(.*)$"));
    }

    public String getLanguage(){
        ArrayList<String> languageTags = tagSearch(Pattern.compile("language:(.*)$"));
        for(String cur : languageTags){
            if (!cur.equals("translated")){
                return cur;
            }
        }
        return null;
    }

    public ArrayList<String> tagSearch(Pattern pattern){
        ArrayList<String> results = new ArrayList<>();

        for(String cur : tags){
            Matcher matcher = pattern.matcher(cur);
            if(matcher.find()){
                results.add(matcher.group(1));
            }
        }
        return results;
    }

    public static ArrayList<String> getTopSearchResults(String query, int pages, boolean nonrestrict){
        query += " language:english";
        String urlQuery = "https://e-hentai.org/?f_cats=1017&f_search=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=";
        ArrayList<String> results = new ArrayList<>();
        String newUrlQuery;

        CloseableHttpClient connect = HttpClients.createDefault();
        for (int currentPage = 0; currentPage < pages; currentPage++) {
            try {
                newUrlQuery = urlQuery + currentPage;
                logger.info("Current page: " + (currentPage + 1));
                logger.info("Query: " + newUrlQuery);

                Document doc = Jsoup.connect(newUrlQuery).get();
                Elements links = doc.select("a[href]");
                ArrayList<String> linkBucket = new ArrayList<>();

                for(Element link : links) {
                    if(link.attr("abs:href").contains("e-hentai.org/g/")) {
                        linkBucket.add(link.attr("abs:href"));
                    }
                }

                if(linkBucket.isEmpty()){
                    break;
                }

                Pattern galleryPattern = Pattern.compile("https?://e[x\\-]hentai\\.org/g/(\\d+)/([\\da-f]+)/");

                JSONObject payload = new JSONObject();
                payload.put("method", "gdata");

                JSONArray gidContainer = new JSONArray();

                System.out.println(payload.toString(4));

                for(String cur : linkBucket){
                    JSONArray gid = new JSONArray();
                    if(!cur.endsWith("/")){
                        cur += "/";
                    }
                    Matcher galleryMatcher = galleryPattern.matcher(cur);

                    if(galleryMatcher.find()){
                        gid.put(Integer.parseInt(galleryMatcher.group(1)));
                        gid.put(galleryMatcher.group(2));

                        gidContainer.put(gid);
                    }
                }

                payload.put("gidlist", gidContainer);
                payload.put("namespace", 1);

                StringEntity payloadEntity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);

                HttpPost post = new HttpPost("https://api.e-hentai.org/api.php");
                post.setEntity(payloadEntity);

                logger.info("Executing post request...");
                CloseableHttpResponse apiResponse = connect.execute(post);

                HttpEntity entity = apiResponse.getEntity();
                JSONObject jsonResponse = new JSONObject(new JSONTokener(entity.getContent()));
                if(jsonResponse.has("error")){
                    logger.info("Some error happened: " + jsonResponse.getString("error"));
                    logger.info("Waiting 5 seconds, then retrying current page...");
                    Thread.sleep(5000);

                    currentPage--;
                    continue;
                }

                JSONArray metadatas = jsonResponse.getJSONArray("gmetadata");

                EntityUtils.consume(entity);
                apiResponse.close();

                ArrayList<EHFetcher> bigOlListOfFetchers = new ArrayList<>();

                for(int i = 0; i < metadatas.length(); i++){
                    bigOlListOfFetchers.add(new EHFetcher(metadatas.getJSONObject(i)));
                }

                for(int i = 0; i < bigOlListOfFetchers.size(); i++){
                    logger.info("Checking " + linkBucket.get(i));
                    EHFetcher cur = bigOlListOfFetchers.get(i);
                    if(!nonrestrict){
                        if(TagChecker.wholesomeCheck(cur.getTags(), query)){
                            results.add(linkBucket.get(i));
                            logger.info("Wholesome!");
                        }
                    }
                    else {
                        if(TagChecker.tagCheck(cur.getTags()).isEmpty()){
                            results.add(linkBucket.get(i));
                            logger.info("Wholesome!");
                        }
                    }
                }

                Thread.sleep(1000);
            } catch (HttpStatusException e) {
                logger.info("Error: HTTP status " + e.getStatusCode());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
