package com.ipb.platform.webparser;

import com.ipb.platform.dto.requests.DatesNotWorkingRequestDTO;
import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.persistence.CategoryRepository;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.services.impl.LandmarkService;
import com.ipb.platform.worker.WebParserWorker;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LandmarkWebparser {

    private static final Pattern COORDINATES = Pattern.compile("google.maps.LatLng\\(([0-9.]+),([0-9.]+)");

    @Autowired
    private WebParserWorker worker;

    @Autowired
    private LandmarkService landmarkService;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private CityRepository cityRepo;

    public void run() {
        HashMap<String, Integer> categoryTypes = new HashMap<String, Integer>() {{
//            put("https://opoznai.bg/kultura-i-izkustvo/sort:popular_ever/page:", 20);// pages 121
            put("https://opoznai.bg/prirodni-zabelejitelnosti/sort:popular_ever/page:", 15); //pages 40
            put("https://opoznai.bg/istoricheski-zabelejitelnosti/sort:popular_ever/page", 15); // pages 15
            put("https://opoznai.bg/sport/sort:popular_ever/page", 7); // pages 7
        }};
        for (Map.Entry<String, Integer> entry : categoryTypes.entrySet()) {
            for (int i = 1; i <= entry.getValue(); i++) {
                Document doc = worker.connectURL(entry.getKey() +Integer.toString(i));
                Elements results = worker.getElementsBySelector(doc, "#guides_container > article > div > div"
                        + ".article_padding"
                        + " > div"
                        + ".article_content > h3 > a");
                Elements cityAroundResults = worker.getElementsBySelector(doc,
                        "#guides_container > article > div > div"
                                + ".article_padding"
                                + " > span.article_tlocation_wrp"
                                + " > span.article_tlocation"
                                + " > a.article_tlocation_main > b");
                List<LandmarkRequestDTO> landmarksDTO = getLandmarkPages(results, cityAroundResults);
                landmarksDTO
                        .stream()
                        .filter(landmarkDTO -> landmarkDTO.getCityId() != 0)
                        .forEach(landmarkDTO -> landmarkService.save(landmarkDTO));
            }
        }
    }

    private List<LandmarkRequestDTO> getLandmarkPages(Elements results, Elements cityAroundResults) {
        List<String> cityPrefix = new ArrayList<String>() {
            {
                add("до");
                add("село");
                add("гр.");
            }
        };

        return IntStream.range(0, results.size() - 1)
                .mapToObj(i -> {
                    AtomicReference<String> cityName = new AtomicReference<>(cityAroundResults.get(i).text().trim());
                    cityPrefix.stream().forEach(prefix -> {
                        if (cityName.get().startsWith(prefix)) {
                            cityName.set(cityName.get().substring(cityName.get().indexOf(" ")).trim());
                        }
                    });

                    return this.createLandmark(
                            results.get(i).attributes().get("href"),
                            cityName.get()
                    );
                })
                .collect(Collectors.toList());
    }

    private LandmarkRequestDTO createLandmark(String url, String cityAround) {
        Document doc = worker.connectURL("https://opoznai.bg/" + url);
        LandmarkRequestDTO landmark = new LandmarkRequestDTO();

        System.out.println("[[ createLandmark: URL ]]: " + "https://opoznai.bg/" + url);

        landmark.setName(doc.getElementsByClass("viewpage_guide_title").text());

        System.out.println("[[ CITY AROUND ANME: ]]" + cityAround);
        setCity(cityAround, landmark);

        String altitude = doc.getElementsByClass("attitude").text()
                .replaceAll("\\D+", "");

        if (!altitude.isEmpty()) {
            landmark.setAltitude(Double.valueOf(altitude));
        }

        landmark.setDatesNotWorking(new ArrayList<DatesNotWorkingRequestDTO>());

        landmark.setInTop100(!doc.getElementsByClass("bg100").isEmpty());

        setCategories(doc, ".catmenu_subcats > .selected > .catmenu_btn_txt", landmark);

        landmark.setDescription(doc.getElementsByClass("main_article_text").html());

        setImages(doc, landmark);

        setCoordinates(doc, landmark);

        landmark.setType("LANDMARK");

        landmark.setWorkTime("8h-17h");

        return landmark;
    }

    private void setCoordinates(Document doc, LandmarkRequestDTO landmark) {
        Elements scriptElements = doc.getElementsByTag("script");
        String allScripts = null;
        for (Element element : scriptElements) {
            for (DataNode node : element.dataNodes())
                allScripts += node.getWholeData();
        }

        Matcher matcher = COORDINATES.matcher(allScripts);
        while (matcher.find()) {
            landmark.setLatitude(Double.valueOf(matcher.group(1)));
            landmark.setLongitude(Double.valueOf(matcher.group(2)));
        }
    }

    private void setImages(Document doc, LandmarkRequestDTO landmark) {
        String attr = doc.getElementsByClass("imgallery_bigimg").attr("style");
        List<ImageRequestDTO> images = new ArrayList<>();
        images.add(new ImageRequestDTO(null, null,
                attr.substring(attr.indexOf("https://"), attr.indexOf("')"))));

        doc.select(".imgallery_thumbs > .imgallery_thumb")
                .forEach(element -> {
                    String attribute = element.attr("style");
                    String url = attribute.substring(attribute.indexOf("https://"), attribute.indexOf("')"));
                    if (!url.endsWith(".gif")) {
                        images.add(new ImageRequestDTO(null, null, url));
                    }
                });

        landmark.setImages(images);
    }

    private void setCategories(Document doc, String s, LandmarkRequestDTO landmark) {
        Elements category = doc.select(s);
        List<Long> categoriesIds = category
                .stream()
                .map(cat -> {
                    List<CategoryEntity> categoriesByName = categoryRepo.findAllByName(cat.text().trim());
                    if (categoriesByName.size() > 0) {
                        return categoriesByName.get(0).getId();
                    }
                    return null;
                })
                .filter(cat -> cat != null)
                .collect(Collectors.toList());
        landmark.setCategories(categoriesIds);
    }

    private void setCity(String cityName, LandmarkRequestDTO landmark) {
        List<CityEntity> cities = this.cityRepo.findAllByName(cityName);
        if (cities.size() > 0) {
            Long cityId = cities.get(0).getId();
            landmark.setCityId(cityId);
        } else {
            System.out.println("[[ Not Exist city with name: ]]" + cityName);
        }
    }

}
