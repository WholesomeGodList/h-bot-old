package bot.ehentai;

import bot.modules.TagChecker;
import org.apache.commons.text.WordUtils;
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
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import utils.NotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EHFetcher {
	public static void main(String[] args) throws IOException {
		new EHFetcher("https://e-hentai.org/g/1028286/37231df5ee/");
	}

	private static final Logger logger = LogManager.getLogger(EHFetcher.class);

	public enum Category {
		DOUJINSHI {
			public String toString() {
				return "Doujinshi";
			}
		},
		MANGA {
			public String toString() {
				return "Manga";
			}
		},
		ARTIST_CG {
			public String toString() {
				return "Artist CG";
			}
		},
		GAME_CG {
			public String toString() {
				return "Game CG";
			}
		},
		WESTERN {
			public String toString() {
				return "Western";
			}
		},
		IMAGE_SET {
			public String toString() {
				return "Image Set";
			}
		},
		NON_H {
			public String toString() {
				return "Non-H";
			}
		},
		COSPLAY {
			public String toString() {
				return "Cosplay";
			}
		},
		ASIAN_PORN {
			public String toString() {
				return "Asian Porn";
			}
		},
		MISC {
			public String toString() {
				return "Misc";
			}
		},
		PRIVATE {
			public String toString() {
				return "Private";
			}
		}
	}

	private int galleryId;
	private String galleryToken;
	private final JSONObject galleryMeta;
	private final ArrayList<String> tags;

	public EHFetcher(String url) throws IOException {
		if (!url.endsWith("/")) {
			url += "/";
		}

		Pattern galleryPattern = Pattern.compile("https?://e[x\\-]hentai\\.org/g/(\\d+)/([\\da-f]{10})/");
		Pattern pagePattern = Pattern.compile("https?://e[x\\-]hentai\\.org/s/([\\da-f]{10})/(\\d+)-(\\d+)/");

		Matcher galleryMatcher = galleryPattern.matcher(url);
		Matcher pageMatcher = pagePattern.matcher(url);

		if (galleryMatcher.find()) {
			galleryId = Integer.parseInt(galleryMatcher.group(1));
			galleryToken = galleryMatcher.group(2);
		} else if (pageMatcher.find()) {
			String pageId = pageMatcher.group(1);
			galleryId = Integer.parseInt(pageMatcher.group(2));
			int pageNum = Integer.parseInt(pageMatcher.group(3));

			JSONObject payload = new JSONObject();
			payload.put("method", "gtoken");

			JSONArray pageContainer = new JSONArray();
			JSONArray page = new JSONArray();

			page.put(galleryId);
			page.put(pageId);
			page.put(pageNum);

			pageContainer.put(page);

			payload.put("pagelist", pageContainer);

			galleryToken = ehAPIRequest(payload).getJSONArray("tokenlist").getJSONObject(0).getString("token");
		} else {
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

		galleryMeta = ehAPIRequest(payload).getJSONArray("gmetadata").getJSONObject(0);

		tags = new ArrayList<>();
		for (Object cur : galleryMeta.getJSONArray("tags")) {
			tags.add(cur.toString());
		}
	}

	public EHFetcher(JSONObject galleryMeta) {
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

			// Unescape any wacky HTML character sequences (like &#039;)
			BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
			JSONObject jsonResponse = new JSONObject(new JSONTokener(Parser.unescapeEntities(buf.readLine(), false)));
			buf.close();

			EntityUtils.consume(entity);

			return jsonResponse;
		} finally {
			connect.close();
		}
	}

	public Instant getTimePosted() {
		long time = Long.parseLong(galleryMeta.getString("posted"));
		return Instant.ofEpochSecond(time);
	}

	public Category getCategory() {
		String categoryName = galleryMeta.getString("category");
		for (Category cur : Category.values()) {
			if (cur.toString().equals(categoryName)) {
				return cur;
			}
		}
		throw new NotFoundException("Category not found.");
	}

	public int getGalleryId() {
		return galleryId;
	}

	public int getPages() {
		return Integer.parseInt(galleryMeta.getString("filecount").trim());
	}

	public String getGalleryToken() {
		return galleryToken;
	}

	public String getTitle() {
		Pattern titleExtractor = Pattern.compile("^(?:\\s*(?:=.*?=|<.*?>|\\[.*?]|\\(.*?\\)|\\{.*?})\\s*)*(?:[^\\[|\\](){}<>]*\\s*\\|\\s*)?([^\\[|\\](){}<>]*?)(?:\\s*(?:=.*?=|<.*?>|\\[.*?]|\\(.*?\\)|\\{.*?})\\s*)*$");
		Matcher matcher = titleExtractor.matcher(galleryMeta.getString("title"));
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return galleryMeta.getString("title").trim();
	}

	public String getTitleJapanese() {
		Pattern titleExtractor = Pattern.compile("^(?:\\s*(?:=.*?=|<.*?>|\\[.*?]|\\(.*?\\)|\\{.*?})\\s*)*(?:[^\\[|\\](){}<>]*\\s*\\|\\s*)?([^\\[|\\](){}<>]*?)(?:\\s*(?:=.*?=|<.*?>|\\[.*?]|\\(.*?\\)|\\{.*?})\\s*)*$");
		Matcher matcher = titleExtractor.matcher(galleryMeta.getString("title_jpn"));
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return galleryMeta.getString("title_jpn").trim();
	}

	public String getUploader() {
		return galleryMeta.getString("uploader");
	}

	public String getThumbnailUrl() {
		return galleryMeta.getString("thumb");
	}

	public double getRating() {
		return Double.parseDouble(galleryMeta.getString("rating"));
	}

	public ArrayList<String> getArtists() {
		return tagSearch(Pattern.compile("artist:(.*)$"));
	}

	public ArrayList<String> getMaleTags() {
		return tagSearch(Pattern.compile("^male:(.*)$"));
	}

	public ArrayList<String> getFemaleTags() {
		return tagSearch(Pattern.compile("female:(.*)$"));
	}

	public ArrayList<String> getMiscTags() {
		return tagSearch(Pattern.compile("^([^:]*)$"));
	}

	public ArrayList<String> getTags() {
		ArrayList<String> tags = new ArrayList<>();
		tags.addAll(getMaleTags());
		tags.addAll(getFemaleTags());
		tags.addAll(getMiscTags());

		return tags;
	}

	public ArrayList<String> getGroups() {
		return tagSearch(Pattern.compile("group:(.*)$"));
	}

	public ArrayList<String> getParodies() {
		return tagSearch(Pattern.compile("parody:(.*)$"));
	}

	public ArrayList<String> getCharacters() {
		return tagSearch(Pattern.compile("character:(.*)$"));
	}

	public String getLanguage() {
		ArrayList<String> languageTags = tagSearch(Pattern.compile("language:(.*)$"));
		for (String cur : languageTags) {
			if (!cur.equals("translated")) {
				return WordUtils.capitalize(cur);
			}
		}
		return null;
	}

	public ArrayList<String> tagSearch(Pattern pattern) {
		ArrayList<String> results = new ArrayList<>();

		for (String cur : tags) {
			Matcher matcher = pattern.matcher(cur);
			if (matcher.find()) {
				results.add(matcher.group(1));
			}
		}
		return results;
	}

	public static ArrayList<String> getTopSearchResults(String query, int pages, boolean nonrestrict) {
		query += " language:english";
		String urlQuery = "https://e-hentai.org/?f_cats=1017&f_search=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=";
		ArrayList<String> results = new ArrayList<>();
		String newUrlQuery;

		CloseableHttpClient connect = HttpClients.createDefault();

		int errorCount = 0;

		for (int currentPage = 0; currentPage < pages; currentPage++) {
			try {
				newUrlQuery = urlQuery + currentPage;
				logger.info("Current page: " + (currentPage + 1));
				logger.info("Query: " + newUrlQuery);

				Document doc = Jsoup.connect(newUrlQuery).get();
				Elements links = doc.select("a[href]");
				ArrayList<String> linkBucket = new ArrayList<>();

				for (Element link : links) {
					if (link.attr("abs:href").contains("e-hentai.org/g/")) {
						linkBucket.add(link.attr("abs:href"));
					}
				}

				if (linkBucket.isEmpty()) {
					break;
				}

				Pattern galleryPattern = Pattern.compile("https?://e[x\\-]hentai\\.org/g/(\\d+)/([\\da-f]+)/");

				JSONObject payload = new JSONObject();
				payload.put("method", "gdata");

				JSONArray gidContainer = new JSONArray();

				System.out.println(payload.toString(4));

				for (String cur : linkBucket) {
					JSONArray gid = new JSONArray();
					if (!cur.endsWith("/")) {
						cur += "/";
					}
					Matcher galleryMatcher = galleryPattern.matcher(cur);

					if (galleryMatcher.find()) {
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
				if (jsonResponse.has("error")) {
					logger.info("Some error happened: " + jsonResponse.getString("error"));

					if (errorCount > 3) {
						logger.info("Too many errors in a row. Aborting...");
						break;
					}
					logger.info("Waiting 5 seconds, then retrying current page...");
					Thread.sleep(5000);

					errorCount++;

					currentPage--;
					continue;
				}

				JSONArray metadatas = jsonResponse.getJSONArray("gmetadata");

				EntityUtils.consume(entity);
				apiResponse.close();

				ArrayList<EHFetcher> bigOlListOfFetchers = new ArrayList<>();

				for (int i = 0; i < metadatas.length(); i++) {
					bigOlListOfFetchers.add(new EHFetcher(metadatas.getJSONObject(i)));
				}

				for (int i = 0; i < bigOlListOfFetchers.size(); i++) {
					logger.info("Checking " + linkBucket.get(i));
					EHFetcher cur = bigOlListOfFetchers.get(i);
					if (!nonrestrict) {
						if (TagChecker.wholesomeCheck(cur.getTags(), query)) {
							results.add(linkBucket.get(i));
							logger.info("Wholesome!");
						}
					} else {
						if (TagChecker.tagCheck(cur.getTags()).isEmpty()) {
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
