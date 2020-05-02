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
		JSONObject jsonResponse = imgurAPIRequest("https://api.imgur.com/3/album/" + hashCode);
		jsonResponse = jsonResponse.getJSONObject("data");

		return Integer.parseInt(jsonResponse.get("images_count").toString().trim());
	}

	public static JSONObject imgurAPIRequest(String uri) {
		try {
			CloseableHttpClient bruh = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(uri.trim());
			httpGet.setHeader("Authorization", "Client-ID " + BotConfig.CLIENT_ID);

			CloseableHttpResponse response = bruh.execute(httpGet);
			HttpEntity entity = response.getEntity();
			JSONObject jsonResponse = new JSONObject(new JSONTokener(entity.getContent()));
			EntityUtils.consume(entity);

			return jsonResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new NotFoundException("Page count not found.");
	}
}