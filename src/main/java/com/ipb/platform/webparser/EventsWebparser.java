package com.ipb.platform.webparser;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.persistence.CategoryRepository;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.EventRepository;
import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.EventEntity;
import com.ipb.platform.services.CategoryService;
import com.ipb.platform.services.impl.EventService;
import com.ipb.platform.worker.WebParserWorker;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class EventsWebparser {

    @Autowired
    private WebParserWorker worker;

    @Autowired
    private EventService service;

    @Autowired
    EventRepository repo;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CityRepository cityRepository;

    public void run() {
        saveEvents();
    }

    private void saveEvents() {
        // max page is 100
        for (int i = 1; i <= 20; i++) {
            Document doc = worker.connectURL("https://opoznai.bg/events/page:" + Integer.toString(i));

            List<String> categoriesPages = getEventCategoriesPages(
                    doc,
                    ".evnt_aside_categories > a.evnt_aside_category"
            );

            List<EventRequestDTO> events = new ArrayList<>();
            categoriesPages.forEach(page -> {
                events.addAll(getEventsFromPage(page));
            });

            System.out.println("[[ categoriesPages ]]: " + categoriesPages);
            events.forEach(event -> this.service.save(event));
        }
    }

    private List<String> getEventCategoriesPages(Document doc, String selector) {
        return doc
                .select(selector)
                .stream()
                .map((domElement) -> domElement.attr("href"))
                .collect(Collectors.toList());
    }

    private List<EventRequestDTO> getEventsFromPage(String pageUrl) {
        Document doc = worker.connectURL(pageUrl);
        Elements events = worker.getElementsBySelector(
                doc,
                ".events_2col_page" +
                        " > .events_2col_main" +
                        " > .events_calendar" +
                        " > .events_calendar_cont" +
                        " > .events_calendar_day" +
                        " > .events_calendar_day_list" +
                        " > .events_fitem" +
                        " > .ft_event" +
                        " > .ft_event_wrp" +
                        " > .ft_event_cont_wrp" +
                        " > .ft_event_cont" +
                        " > .ft_event_name" +
                        " > a");

        return events
                .stream()
                .map(event -> createEvent(event.attr("href")))
                .collect(Collectors.toList());
    }

    private EventRequestDTO createEvent(String pageUrl) {
        Document doc = worker.connectURL(pageUrl);

        EventRequestDTO event = new EventRequestDTO();
        String name = worker.getElementsBySelector(
                doc,
                ".viewpage_event_heading > .event_heading_caption > .event_heading_caption_cont > h1"
        ).text();
        name = name.replaceAll("\"", "").trim();
        event.setName(name);

        String cityAroundName = this.getCityName(doc);
        setCity(cityAroundName, event);
        setCoordinates(cityAroundName, event);

        event.setType("EVENT");

        event.setApproved(true);

        setDescription(doc, event);

        setDates(doc, event);

        setWorksTime(doc, event);

        setCategories(doc, event);

        setImages(doc, event);

        return event;
    }

    private String getCityName(Document doc) {
        return worker.getElementsBySelector(
                doc,
                "div.main_breadcrumbs > ul.breadcrumbs-list-wrap > li > a.breadcrumb-el"
        ).get(2).text();
    }

    private void setCity(String name, EventRequestDTO event) {
        Long cityId = this.cityRepository.findAllByName(name).get(0).getId();
        event.setCityId(cityId);
    }

    private void setCoordinates(String name, EventRequestDTO event) {
        CityEntity city = this.cityRepository.findAllByName(name).get(0);
        event.setLatitude(city.getLatitude());
        event.setLongitude(city.getLongitude());
    }

    private void setDescription(Document doc, EventRequestDTO event) {
        String description = worker.getElementsBySelector(
                doc,
                "#tabcont_info > .main_article_text").html();

        event.setDescription(description);
    }

    private void setDates(Document doc, EventRequestDTO event) {
        Elements dates = worker.getElementsBySelector(
                doc,
                ".event_heading_caption_double" +
                        " > .event_heading_caption" +
                        " > .event_heading_caption_cont" +
                        " > .event_heading_dates");

        int afterDays = this.getAfterDays(dates.get(0).select(".week_day").text());
        Date startDate = new Date(new Date().getTime() + afterDays * 24 * 60 * 60 * 10000);
        Date endDate = startDate;

        event.setStartDate(startDate);
        event.setEndDate(endDate);
    }

    private int getAfterDays(String afterDays) {
        if (afterDays.isEmpty()) {
            return 0;
        }

        afterDays = afterDays.replaceAll("\\D", "").trim();
        return Integer.parseInt(afterDays);
    }


    private void setWorksTime(Document doc, EventRequestDTO event) {
        Elements sections = worker.getElementsBySelector(
                doc,
                ".event_heading_caption_double" +
                        " > .event_heading_caption" +
                        " > .event_heading_caption_hdr");


        event.setWorkTime(this.getWorkTime(sections));
    }

    private String getWorkTime(Elements sections){
        List<Element> workTimeSection = sections
                .stream()
                .filter(el -> el.text().toLowerCase().equals("начален час"))
                .collect(Collectors.toList());
        String workTime = (workTimeSection.size() == 0) ? "" :
                workTimeSection.get(0).parent().select(".event_heading_caption_cont").html();

        return workTime;
    }
    private void setCategories(Document doc, EventRequestDTO event) {
        Elements categories = worker.getElementsBySelector(
                doc,
                ".evnt_aside_categories > .evnt_aside_category.selected > span.subcategory-text");
        List<Long> categoriesIds = categories
                .stream()
                .map(categoryElement -> this.getCategoryIdByName(categoryElement.text()))
                .collect(Collectors.toList());
        event.setCategories(categoriesIds);
    }

    private Long getCategoryIdByName(String name) {
        CategoryEntity categories = this.categoryRepository.findAllByName(name).get(0);
        return categories.getId();
    }

    private void setImages(Document doc, EventRequestDTO event) {
        Elements images = worker.getElementsBySelector(
                doc,
                "img.event-gallery-img");

        List<ImageRequestDTO> imagesDTO = images
                .stream()
                .map(imgElem -> new ImageRequestDTO(null, null, imgElem.attr("src")))
                .collect(Collectors.toList());

        event.setImages(imagesDTO);
    }
}
