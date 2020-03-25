package bot.imgur;

import bot.BotConfig;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import utils.NotFoundException;

public class ImgurFetcher {
    public static int getPages(String hashCode) {
        try {
            CloseableHttpClient bruh = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://api.imgur.com/3/album/" + hashCode);
            httpGet.setHeader("Authorization", "Client-ID " + BotConfig.CLIENT_ID);

            CloseableHttpResponse response = bruh.execute(httpGet);
            HttpEntity entity = response.getEntity();
            JSONObject jsonResponse = new JSONObject(new JSONTokener(entity.getContent()));

            jsonResponse = jsonResponse.getJSONObject("data");

            EntityUtils.consume(entity);

            return Integer.parseInt(jsonResponse.get("images_count").toString().trim());
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new NotFoundException("Page count not found.");
    }

    public static JSONObject imgurAPIRequest(String uri){
        try {
            CloseableHttpClient bruh = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(uri.trim());
            httpGet.setHeader("Authorization", "Client-ID " + BotConfig.CLIENT_ID);

            CloseableHttpResponse response = bruh.execute(httpGet);
            HttpEntity entity = response.getEntity();
            JSONObject jsonResponse = new JSONObject(new JSONTokener(entity.getContent()));
            EntityUtils.consume(entity);

            return jsonResponse;
        } catch (Exception e){
            e.printStackTrace();
        }
        throw new NotFoundException("Page count not found.");
    }
}