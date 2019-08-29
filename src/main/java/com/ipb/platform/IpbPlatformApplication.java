package com.ipb.platform;

import static com.ipb.platform.persistence.entities.ObjectType.LANDMARK;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ipb.platform.mappers.LandmarkMapper;
import com.ipb.platform.persistence.entities.LandmarkEntity;
import com.ipb.platform.worker.WebParserWorker;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class IpbPlatformApplication implements CommandLineRunner {

	private static final Pattern COORDINATES = Pattern.compile("google.maps.LatLng\\(([0-9.]+),([0-9.]+)");

	@Autowired
	private WebParserWorker worker;

	@Autowired
	private LandmarkMapper mapper;

	public static void main(String[] args) {
		SpringApplication.run(IpbPlatformApplication.class, args);
	}

	/**
	 * A password encoder bean for storing passwords in an encrypted BCrypt format.
	 * 
	 * @return an instance of the bean
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(String... args) throws Exception {
		for (int i = 1; i < 41; i++) {
			Document doc = worker.connectURL("https://opoznai.bg/prirodni-zabelejitelnosti/sort:popular_ever/page:" +
					Integer.toString(i));
			Elements results = worker.getElementsBySelector(doc, "#guides_container > article > div > div"
					+ ".article_padding"
					+ " > div"
					+ ".article_content > h3 > a");
			getLandmarkPages(results);
		}
	}

	private List<LandmarkEntity> getLandmarkPages(Elements results) {
		return results.stream()
				.map(result -> this.createLandmark(result.attributes().get("href")))
				.collect(Collectors.toList());
	}

	private LandmarkEntity createLandmark(String url) {
		Document doc = worker.connectURL("https://opoznai.bg/" + url);
		LandmarkEntity landmark = new LandmarkEntity();

		landmark.setName(doc.getElementsByClass("viewpage_guide_title").text());

		setCity(doc, ".heading_meta > .location");

		landmark.setAltitude(Double.valueOf(doc.getElementsByClass("attitude").text().replaceAll("\\D+","")));

		//landmark.setDatesNotWorking();

		landmark.setInTop100(!doc.getElementsByClass("bg100").isEmpty());

		setCategories(doc, ".catmenu_subcats > .selected > .catmenu_btn_txt");


		landmark.setDescription(doc.getElementsByClass("main_article_text").html());

		setImages(doc);

		setCoordinates(doc, landmark);


		landmark.setType(LANDMARK);

		return landmark;
	}

	private void setCoordinates(Document doc, LandmarkEntity landmark) {
		Elements scriptElements = doc.getElementsByTag("script");
		String allScripts = null;
		for (Element element :scriptElements ){
			for (DataNode node : element.dataNodes())
				allScripts += node.getWholeData();
		}

		Matcher matcher = COORDINATES.matcher(allScripts);
		while(matcher.find()) {
			landmark.setLatitude(Double.valueOf(matcher.group(1)));
			landmark.setLongitude(Double.valueOf(matcher.group(2)));
		}
	}

	private void setImages(Document doc) {
		String attr = doc.getElementsByClass("imgallery_bigimg").attr("style");
		List<String> images = new ArrayList<>();
		images.add(attr.substring(attr.indexOf("https://"), attr.indexOf("')")));

		doc.select(".imgallery_thumbs > .imgallery_thumb")
				.forEach(element -> {
					String attribute = element.attr("style");
					images.add(attribute.substring(attribute.indexOf("https://"), attribute.indexOf("')")));
				});

		//landmark.setImages();
	}

	private void setCategories(Document doc, String s) {
		String category = doc.select(s).text();
		//landmark.setCategories();
	}

	private void setCity(Document doc, String s) {
		String city = doc.select(s).text();
		//landmark.setCity();
	}

}
