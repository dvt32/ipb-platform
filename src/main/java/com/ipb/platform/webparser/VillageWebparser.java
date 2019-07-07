package com.ipb.platform.webparser;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.services.IPBObjectService;
import com.ipb.platform.services.impl.CityService;
import com.ipb.platform.services.impl.IPBObjectServiceImpl;
import com.ipb.platform.worker.WebParserWorker;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VillageWebparser {

    private static HashMap<String, List<Double>> villagesCoordinates = new HashMap<String, List<Double>>() {{
        put("Антон", new ArrayList<Double>(2) {{
            add(42.75);
            add(24.283333);
        }});
        put("Арда", new ArrayList<Double>(2) {{
            add(41.462778);
            add(24.6325);
        }});
        put("Богородица", new ArrayList<Double>(2) {{
            add(41.510833);
            add(23.19);
        }});
        put("Богослов", new ArrayList<Double>(2) {{
            add(42.255);
            add(22.675278);
        }});
        put("Бръшлян", new ArrayList<Double>(2) {{
            add(42.05);
            add(27.433333);
        }});
        put("Бяла", new ArrayList<Double>(2) {{
            add(42.733333);
            add(26.183333);
        }});
        put("Ваклиново", new ArrayList<Double>(2) {{
            add(41.615139);
            add(24.054861);
        }});
        put("Велика", new ArrayList<Double>(2) {{
            add(42.183333);
            add(27.783333);
        }});
        put("Венелин", new ArrayList<Double>(2) {{
            add(43.049722);
            add(27.666667);
        }});
        put("Войвода", new ArrayList<Double>(2) {{
            add(43.45);
            add(27.133333);
        }});
        put("Врата", new ArrayList<Double>(2) {{
            add(41.866667);
            add(24.966667);
        }});
        put("Гранит", new ArrayList<Double>(2) {{
            add(42.25);
            add(25.15);
        }});
        put("Граница", new ArrayList<Double>(2) {{
            add(42.253056);
            add(22.741667);
        }});
        put("Гугутка", new ArrayList<Double>(2) {{
            add(41.416667);
            add(25.916667);
        }});
        put("Гърло", new ArrayList<Double>(2) {{
            add(42.766667);
            add(22.85);
        }});
        put("Гърмен", new ArrayList<Double>(2) {{
            add(0.0);
            add(0.0);
        }});
        put("Жеравино", new ArrayList<Double>(2) {{
            add(42.317778);
            add(22.373333);
        }});
        put("Кестен", new ArrayList<Double>(2) {{
            add(41.55);
            add(24.433333);
        }});
        put("Константин", new ArrayList<Double>(2) {{
            add(42.95);
            add(26.066667);
        }});
        put("Кошница", new ArrayList<Double>(2) {{
            add(41.516667);
            add(24.683333);
        }});
        put("Крепост", new ArrayList<Double>(2) {{
            add(42.016667);
            add(25.6);
        }});
        put("Кукувица", new ArrayList<Double>(2) {{
            add(41.7);
            add(24.616667);
        }});
        put("Марица", new ArrayList<Double>(2) {{
            add(42.3);
            add(23.7);
        }});
        put("Медовина", new ArrayList<Double>(2) {{
            add(43.316667);
            add(26.166667);
        }});
        put("Неофит Рилски", new ArrayList<Double>(2) {{
            add(43.283333);
            add(27.516667);
        }});

        put("Огняново (Област Благоевград)", new ArrayList<Double>(2) {{
            add(0.0);
            add(0.0);
        }});

        put("Опълченец", new ArrayList<Double>(2) {{
            add(42.2);
            add(25.116667);
        }});

        put("Палат", new ArrayList<Double>(2) {{
            add(41.591667);
            add(23.188611);
        }});

        put("Партизани", new ArrayList<Double>(2) {{
            add(43.016667);
            add(27.25);
        }});

        put("Перуника", new ArrayList<Double>(2) {{
            add(41.45);
            add(25.75);
        }});

        put("Петко Славейков", new ArrayList<Double>(2) {{
            add(43.05);
            add(24.966667);
        }});

        put("Рибница", new ArrayList<Double>(2) {{
            add(41.466667);
            add(24.866667);
        }});

        put("Рожен", new ArrayList<Double>(2) {{
            add(41.533333);
            add(23.433333);
        }});

        put("Самодива", new ArrayList<Double>(2) {{
            add(41.431667);
            add(25.33);
        }});

        put("Света Петка", new ArrayList<Double>(2) {{
            add(42.033333);
            add(23.866667);
        }});

        put("Светля", new ArrayList<Double>(2) {{
            add(42.596389);
            add(22.797222);
        }});

        put("Сенник", new ArrayList<Double>(2) {{
            add(42.983333);
            add(25.05);
        }});

        put("Срем", new ArrayList<Double>(2) {{
            add(42.05);
            add(26.483333);
        }});

        put("Стряма", new ArrayList<Double>(2) {{
            add(42.25);
            add(24.883333);
        }});

        put("Тича", new ArrayList<Double>(2) {{
            add(42.966667);
            add(26.433333);
        }});

        put("Томпсън", new ArrayList<Double>(2) {{
            add(42.933333);
            add(23.383333);
        }});

        put("Тракия", new ArrayList<Double>(2) {{
            add(42.2);
            add(25.65);
        }});

        put("Труд", new ArrayList<Double>(2) {{
            add(42.233333);
            add(24.733333);
        }});

        put("Фабрика", new ArrayList<Double>(2) {{
            add(41.433333);
            add(24.983333);
        }});

        put("Хан Крум", new ArrayList<Double>(2) {{
            add(43.203333);
            add(26.892778);
        }});

        put("Хвойна", new ArrayList<Double>(2) {{
            add(41.866667);
            add(24.683333);
        }});

        put("Чавка", new ArrayList<Double>(2) {{
            add(41.416667);
            add(25.45);
        }});

        put("Чепино", new ArrayList<Double>(2) {{
            add(42.616667);
            add(22.783333);
        }});
        put("Черна вода", new ArrayList<Double>(2) {{
            add(42.983333);
            add(26.166667);
        }});

        put("Черни Вит", new ArrayList<Double>(2) {{
            add(42.883333);
            add(24.2);
        }});
        put("Шуменци", new ArrayList<Double>(2) {{
            add(43.966667);
            add(26.6);
        }});
        put("Ягода", new ArrayList<Double>(2) {{
            add(42.55);
            add(25.6);
        }});
        put("Прибой", new ArrayList<Double>(2) {{
            add(42.496667);
            add(22.916667);
        }});
    }};

    @Autowired
    private WebParserWorker worker;

    @Autowired
    private CityService service;

    public void run() {
        Document docTest = worker.connectURL("https://opoznai.bg/browse/village:7/");
        Document doc = worker.connectURL("https://opoznai.bg/view/orlovo-oko-iagodina");
        Elements results = worker.getElementsBySelector(
                doc,
                "#all_villages_dropdown" +
                        " > div.hdr_dropdown_column" +
                        " > .hdr_dropdown_column_wrap" +
                        " > div.hdr_dropdown_links" +
                        " > a.location_link"
        );
        List<CityRequestDTO> villagesDTO = getVillagePages(results);
        System.out.println(villagesDTO);
        villagesDTO.forEach(villageRequestDTO -> service.save(villageRequestDTO));
    }

    private List<CityRequestDTO> getVillagePages(Elements results) {
        return results.stream()
                .map(result -> this.createObject(result.attributes().get("href")))
                .filter(result -> result != null)
                .collect(Collectors.toList());
    }

    private CityRequestDTO createObject(String url) {
        try {
            Document doc = worker.connectURL(url + "/");
            CityRequestDTO village = new CityRequestDTO();

            String name = worker.getElementsBySelector(
                    doc, ".images_pile_overlay > .images_pile_overlay_wrap > h1").text();
            village.setName(name);

            setDescription(name, 5, village);

            setCoordinates(name, village);

            setImages(doc, village);

            setCategories(doc, ".catmenu_subcats > .selected > .catmenu_btn_txt");

            village.setType("VILLAGE");

            return village;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private void setDescription(String name, int numberParagraphs, CityRequestDTO village) {
        try {
            Document doc = worker.connectURL("https://bg.wikipedia.org/wiki/" +
                    name.replaceAll(" ", "_"));
            Elements results = worker.getElementsBySelector(doc, ".mw-parser-output > p");
            village.setDescription(getDescription(results, numberParagraphs));
        } catch (Exception ex) {
            System.out.println("[[ DESCRIPTION NAME ]]: " + name);
            System.out.println("[[ numberParagraphs ]]" + numberParagraphs);
            System.out.println(ex.getMessage());
            System.out.println();
        }
    }

    private String getDescription(Elements results, int numberParagraphs) {
        return results == null ? null : results.stream().limit(numberParagraphs).reduce(null, (subtotal, element) -> {
            if (subtotal == null) {
                subtotal = element.clone();
            } else {
                String newText = subtotal.html() + element.html();
                subtotal.html(newText);
            }
            return subtotal;
        }).html();
    }

    private void setCoordinates(String name, CityRequestDTO village) {
        String url = "https://bg.wikipedia.org/wiki/" + name.replaceAll(" ", "_");
        try {
            Document doc = worker.connectURL(url);
            String latitude = worker.getElementsBySelector(doc, "span.geo > span.latitude").text();
            String longitude = worker.getElementsBySelector(doc, "span.geo > span.longitude").text();
            double[] coordinates = coordinatesParserToDouble(name, latitude, longitude);
            village.setLatitude(coordinates[0]);
            village.setLongitude(coordinates[1]);
        } catch (Exception ex) {
            System.out.println("[[ URL ]]: " + url);
            System.out.println("Document doc = worker.connectURL(url); --> doc = null");
            System.out.println(ex.getMessage());
            System.out.println();
        }
    }

    private double[] coordinatesParserToDouble(String name, String latitude, String longitude) {
        double[] coordinates = {0.0, 0.0};
        try {
            coordinates[0] = Double.parseDouble(latitude);
            coordinates[1] = Double.parseDouble(longitude);
        } catch (Exception ex) {
            System.out.println("[[ NAME ]]: " + name);
            System.out.println(ex.getMessage());
            System.out.println();

            if (villagesCoordinates.containsKey(name)) {
                List<Double> villageCoordinates = villagesCoordinates.get(name);
                coordinates[0] = villageCoordinates.get(0);
                coordinates[1] = villageCoordinates.get(1);
            }
        }
        return coordinates;
    }

    private void setImages(Document doc, CityRequestDTO village) {
        List<ImageRequestDTO> images = doc.select("#main_layout_content > section#images_pile > a")
                .stream()
                .filter(element -> {
                    String attribute = element.attr("style");
                    String url = attribute.substring(attribute.indexOf("https://"), attribute.indexOf("')"));
                    return url.endsWith(".gif") ? false : true;
                })
                .map(element -> {
                    String attribute = element.attr("style");
                    return new ImageRequestDTO(
                            null,
                            null,
                            attribute.substring(attribute.indexOf("https://"), attribute.indexOf("')"))
                    );
                })
                .collect(Collectors.toList());

        village.setImages(images);
    }

    private void setCategories(Document doc, String s) {
        String category = doc.select(s).text();
        //landmark.setCategories();
    }
}
