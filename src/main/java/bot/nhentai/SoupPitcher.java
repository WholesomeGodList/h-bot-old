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
import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class that does all the webscraping of nhentai.
 * All of the methods in here require that the URL be valid! Please do validation before calling these methods.
 */
public class SoupPitcher {
	private static final Logger logger = LogManager.getLogger(SoupPitcher.class);

	private final Document doc;
	private final String url;

	public SoupPitcher(String url) throws IOException {
		this.url = url;
		doc = Jsoup.connect(url).get();
	}

	public static int getLatestNumber() {
		try {
			Document doc = Jsoup.connect("https://nhentai.net/").get();
			Elements links = doc.select("div .index-container").last().select("a");

			Pattern pattern = Pattern.compile("/g/(\\d+?)/?$");
			for (Element curElement : links) {
				Matcher matcher = pattern.matcher(curElement.attr("href"));
				if (matcher.find()) {
					return Integer.parseInt(matcher.group(1));
				}
			}
		} catch (HttpStatusException e) {
			logger.error("Error happened when getting latest number! This should NOT happen");
			logger.error("HTTP status code: " + e.getStatusCode());
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new NotFoundException("Latest number not found.");
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
					try {
						logger.info("Checking " + cur);
						SoupPitcher curChecker = new SoupPitcher(cur);
						if (!nonrestrict) {
							if (curChecker.getLanguage().equals("English") && TagChecker.wholesomeCheck(curChecker.getTags(), query)) {
								results.add(cur);
								logger.info("Wholesome!");
							}
						} else {
							if (curChecker.getLanguage().equals("English") && TagChecker.tagCheck(curChecker.getTags()).isEmpty()) {
								results.add(cur);
								logger.info("Wholesome!");
							}
						}
					} catch (HttpStatusException e) {
						logger.info("Error on current check: status code " + e.getStatusCode());
					}
				}

				if (pages >= 10) {
					Thread.sleep(50);
				}
				if (pages >= 100) {
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

	public Instant getTimePosted() {
		Element timePosted = doc.select("time").first();
		return Instant.parse(timePosted.attr("datetime"));
	}

	public int getPages() {
        /*
        Elements divs = doc.select("span").select(".tags").select("a");

        for (Element div : divs) {
            if(div.attr("href").contains("pages")) {
                return Integer.parseInt(div.select("span").text());
            }
        }

        throw new NotFoundException("Page count not found.");
         */

		Elements divs = doc.select("div").select("#info").select("div");

		for (Element div : divs) {
			Pattern pattern = Pattern.compile("(\\d+)\\s* pages");
			Matcher matcher = pattern.matcher(div.text());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		}

		throw new NotFoundException("Page count not found.");
	}

	public ArrayList<String> getTags() {
		return tagSearch(Pattern.compile("/tag/(.*?)/?$"));
	}

	public ArrayList<String> getParodies() {
		return tagSearch(Pattern.compile("/parody/(.*?)/?$"));
	}

	public ArrayList<String> getGroups() {
		return tagSearch(Pattern.compile("/group/(.*?)/?$"));
	}

	public ArrayList<String> getArtists() {
		return tagSearch(Pattern.compile("/artist/(.*?)/?$"));
	}

	public ArrayList<String> getChars() {
		return tagSearch(Pattern.compile("/character/(.*?)/?$"));
	}

	public ArrayList<String> tagSearch(Pattern pattern) {
		ArrayList<String> tagBucket = extractLinks();
		ArrayList<String> filteredTagBucket = new ArrayList<>();

		for (String cur : tagBucket) {
			Matcher matcher = pattern.matcher(cur);
			if (matcher.find()) {
				filteredTagBucket.add(matcher.group(1).replaceAll("-", " "));
			}
		}
		// Remove duplicates
		return filteredTagBucket.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
	}

	public String getTitle() {
		//This would work if nhentai updated their bloody databases so that the actual title was within ".pretty"

		//Elements titles = doc.select("h1").select("span").select(".pretty");
		//return titles.first().text().trim();

		Elements titles = doc.select("h1");

		Pattern titleExtractor = Pattern.compile("^(?:\\s*[<\\[({].*?[])}>]\\s*)*(?:[^\\[|\\](){}<>]*\\s*\\|\\s*)?([^\\[|\\](){}<>]*)(?:\\s*[<\\[({]/?.*?[])}>]\\s*)*$");
		Matcher matcher = titleExtractor.matcher(titles.first().text());
		if (matcher.find()) {
			return matcher.group(1).trim();
		}

		return titles.first().text().trim();
	}

	public String getTitleJapanese() {
		//Same thing here

		//Elements titles = doc.select("h2").select("span").select(".pretty");
		//return titles.first().text().trim();

		Elements titles = doc.select("h2");

		Pattern titleExtractor = Pattern.compile("^(?:\\s*[<\\[({].*?[])}>]\\s*)*(?:[^\\[|\\](){}<>]*\\s*\\|\\s*)?([^\\[|\\](){}<>]*)(?:\\s*[<\\[({]/?.*?[])}>]\\s*)*$");
		Matcher matcher = titleExtractor.matcher(titles.first().text());
		if (matcher.find()) {
			return matcher.group(1).trim();
		}

		return titles.first().text().trim();
	}

	public int getFaves() {
		Elements links = doc.select("span").select(".nobold");

		Pattern pattern = Pattern.compile("\\((\\d+)\\)");
		Matcher matcher = pattern.matcher(links.last().text());

		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1));
		}

		throw new NotFoundException("Favorites not found.");
	}

	public String getLanguage() {
		ArrayList<String> tagBucket = extractLinks();
		for (String cur : tagBucket) {
			if (cur.contains("language") && !cur.contains("translated")) {
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
			ArrayList<String> imageBucket = new ArrayList<>();
			for (Element cur : images) {
				imageBucket.add(cur.attr("abs:src"));
			}

			for (String cur : imageBucket) {
				if (cur.contains("galleries")) {
					return cur;
				}
			}
		} catch (HttpStatusException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new NotFoundException("No link found.");
	}

	public ArrayList<String> extractLinks() {
		Elements links = doc.select("a[href]");
		ArrayList<String> tagBucket = new ArrayList<>();
		for (Element link : links) {
			tagBucket.add(link.attr("abs:href"));
		}
		return tagBucket;
	}
}