package com.ipb.platform.webparser;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.services.impl.CityService;
import com.ipb.platform.worker.WebParserWorker;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityWebparser {

    private static HashMap<String, List<Double>> citiesCoordinates = new HashMap<String, List<Double>>() {{
        put("Ахелой", new ArrayList<Double>(2) {{
            add(42.648889);
            add(27.648056);
        }});
        put("Баня", new ArrayList<Double>(2) {{
            add(42.55);
            add(24.833333);
        }});
        put("Бяла", new ArrayList<Double>(2) {{
            add(43.46);
            add(25.736111);
        }});
        put("Ветрен", new ArrayList<Double>(2) {{
            add(42.280833);
            add(24.046369);
        }});
        put("Гоце Делчев", new ArrayList<Double>(2) {{
            add(41.572778);
            add(23.729444);
        }});
        put("Елин Пелин", new ArrayList<Double>(2) {{
            add(42.668889);
            add(23.6025);
        }});
        put("Завет", new ArrayList<Double>(2) {{
            add(43.766667);
            add(26.666667);
        }});
        put("Искър", new ArrayList<Double>(2) {{
            add(43.45);
            add(24.266667);
        }});
        put("Кубрат", new ArrayList<Double>(2) {{
            add(43.796667);
            add(26.501111);
        }});
        put("Кула", new ArrayList<Double>(2) {{
            add(43.883333);
            add(22.516667);
        }});
        put("Левски", new ArrayList<Double>(2) {{
            add(43.356944);
            add(25.143056);
        }});
        put("Лозница", new ArrayList<Double>(2) {{
            add(43.366667);
            add(26.6);
        }});
        put("Мизия", new ArrayList<Double>(2) {{
            add(43.683333);
            add(23.85);
        }});
        put("Несебър", new ArrayList<Double>(2) {{
            add(42.660833);
            add(27.713889);
        }});
        put("Омуртаг", new ArrayList<Double>(2) {{
            add(43.107222);
            add(26.419167);
        }});
        put("Пещера", new ArrayList<Double>(2) {{
            add(42.032222);
            add(24.302222);
        }});
        put("Раковски", new ArrayList<Double>(2) {{
            add(42.2875);
            add(24.967778);
        }});
        put("Роман", new ArrayList<Double>(2) {{
            add(43.15);
            add(23.916667);
        }});
        put("Русе", new ArrayList<Double>(2) {{
            add(43.850275);
            add(25.954658);
        }});
        put("Септември", new ArrayList<Double>(2) {{
            add(42.218345);
            add(24.123668);
        }});
        put("Средец", new ArrayList<Double>(2) {{
            add(42.346111);
            add(27.180833);
        }});
        put("Стамболийски", new ArrayList<Double>(2) {{
            add(42.134444);
            add(24.535278);
        }});
        put("Съединение", new ArrayList<Double>(2) {{
            add(42.266667);
            add(24.55);
        }});
        put("Тервел", new ArrayList<Double>(2) {{
            add(43.75);
            add(27.4);
        }});
        put("Цар Калоян", new ArrayList<Double>(2) {{
            add(43.616667);
            add(26.25);
        }});

        put("Шипка", new ArrayList<Double>(2) {{
            add(42.710278);
            add(25.335556);
        }});
    }};

    @Autowired
    private WebParserWorker worker;

    @Autowired
    private CityService service;

    public void run() {
        Document doc = worker.connectURL("https://opoznai.bg/view/orlovo-oko-iagodina");
        Elements results = worker.getElementsBySelector(
                doc,
                "#all_cities_dropdown" +
                        " > div.hdr_dropdown_column" +
                        " > .hdr_dropdown_column_wrap" +
                        " > div.hdr_dropdown_links" +
                        " > a.location_link"
        );
        List<CityRequestDTO> citiesDTO = getCityPages(results);
        System.out.println(citiesDTO);
        citiesDTO.forEach(cityRequestDTO -> service.save(cityRequestDTO));
    }

    private List<CityRequestDTO> getCityPages(Elements results) {
        return results.stream()
                .map(result -> this.createCity(result.attributes().get("href")))
                .filter(result -> result != null)
                .collect(Collectors.toList());
    }

    private CityRequestDTO createCity(String url) {
        try {
            Document doc = worker.connectURL(url);
            CityRequestDTO city = new CityRequestDTO();

            String name = worker.getElementsBySelector(
                    doc, ".images_pile_overlay > .images_pile_overlay_wrap > h1").text();
            city.setName(name);

            setDescription(name, 5, city);

            setCoordinates(name, city);

            setImages(doc, city);

            setCategories(doc, ".catmenu_subcats > .selected > .catmenu_btn_txt");

            city.setType("CITY");

            city.setApproved(true);

            return city;
        } catch (Exception ex) {
            System.out.println();
        }

        return null;
    }

    private void setDescription(String name, int numberParagraphs, CityRequestDTO city) {
        try {
            Document doc = worker.connectURL("https://bg.wikipedia.org/wiki/" + name.replaceAll(" ", "_"));
            Elements results = worker.getElementsBySelector(doc, ".mw-parser-output > p");
            city.setDescription(getDescription(results, numberParagraphs));
        } catch (Exception ex) {
            System.out.println("[[ CITY DESCRIPTION ]]:" + ex.getMessage());
        }
    }

    private String getDescription(Elements results, int numberParagraphs) {
        return results.stream().limit(numberParagraphs).reduce(null, (subtotal, element) -> {
            if (subtotal == null) {
                subtotal = element.clone();
            } else {
                String newText = subtotal.html() + element.html();
                subtotal.html(newText);
            }
            return subtotal;
        }).html();
    }

    private void setCoordinates(String name, CityRequestDTO city) {
        Document doc = worker.connectURL("https://bg.wikipedia.org/wiki/" + name.replaceAll(" ", "_"));

        String latitude = worker.getElementsBySelector(doc, "span.geo > span.latitude").text();

        String longitude = worker.getElementsBySelector(doc, "span.geo > span.longitude").text();
        try {
            city.setLatitude(Double.parseDouble(latitude));
            city.setLongitude(Double.parseDouble(longitude));
        } catch (Exception ex) {

            System.out.println("[[ NAME ]]: " + name);
            System.out.println(ex.getMessage());

            if (citiesCoordinates.containsKey(name)) {
                List<Double> cityCoordinates = citiesCoordinates.get(name);
                System.out.println("[[ GET citiesCoordinates ]]: " + name + " : " + cityCoordinates);
                city.setLatitude(cityCoordinates.get(0));
                city.setLongitude(cityCoordinates.get(1));
            }
            System.out.println();
        }
    }

    private void setImages(Document doc, CityRequestDTO city) {
        List<ImageRequestDTO> images = doc.select("#main_layout_content > section#images_pile > a")
                .stream()
                .map(element -> {
                    String attribute = element.attr("style");
                    return new ImageRequestDTO(
                            null,
                            null,
                            attribute.substring(attribute.indexOf("https://"), attribute.indexOf("')"))
                    );
                })
                .collect(Collectors.toList());

        city.setImages(images);
    }

    private void setCategories(Document doc, String s) {
        String category = doc.select(s).text();
        //landmark.setCategories();
    }
}
