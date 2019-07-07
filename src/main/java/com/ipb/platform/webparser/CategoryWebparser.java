package com.ipb.platform.webparser;

import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.CategoryMapping;
import com.ipb.platform.persistence.CategoryRepository;
import com.ipb.platform.persistence.entities.CategoryEntity;
import com.ipb.platform.services.CategoryService;
import com.ipb.platform.worker.WebParserWorker;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CategoryWebparser {

    @Autowired
    private WebParserWorker worker;

    @Autowired
    private CategoryService service;

    @Autowired
    CategoryRepository repo;


    public void run() {
        saveMainCategories();
        saveSubCategories();
    }


    private void saveMainCategories() {
        saveCategories(
                "https://opoznai.bg/view/orlovo-oko-iagodina",
                ".categories_menu > .catmenu_item > a.catmenu_btn > .catmenu_btn_txt"
        );
        saveCategories(
                "https://opoznai.bg/events",
                ".evnt_aside_categories > a.evnt_aside_category > span.event-cat-list"
        );
    }

    private void saveCategories(String url, String selector) {
        Document doc = worker.connectURL(url);

        List<String> categoriesNames =
                getCategoriesNames(doc, selector);
        categoriesNames.forEach(cat -> service.save(new CategoryRequestDTO(null, cat)));
    }

    private List<String> getCategoriesNames(Document doc, String s) {
        return doc
                .select(s)
                .stream()
                .map((domElement) -> domElement.text())
                .collect(Collectors.toList());
    }

    private void saveSubCategories() {
        saveLandmarkSubCategories();
        saveEventSubCategories();
    }

    private void saveLandmarkSubCategories() {
        Document doc = worker.connectURL("https://opoznai.bg/view/orlovo-oko-iagodina");

        List<CategoryRequestDTO> categories = getCategoriesWithParents(doc,
                ".categories_menu > .catmenu_item > .catmenu_subcats > a.catmenu_subbtn > .catmenu_btn_txt"
        );
        categories.forEach(catDTO -> service.save(catDTO));
    }

    private List<CategoryRequestDTO> getCategoriesWithParents(Document doc, String s) {
        return doc.select(s).stream()
                .map((domElement) ->
                        new CategoryRequestDTO(this.getParentId(domElement), domElement.text()))
                .collect(Collectors.toList());
    }

    private Long getParentId(Element children) {
        String parentName = getParentElement(
                children,
                "a.catmenu_btn > .catmenu_btn_txt"
        ).text();
        return getParentId(parentName);
    }

    private Long getParentId(String name) {
        CategoryEntity parentCategoryEntity = this.repo.findAllByName(name).get(0);
        return parentCategoryEntity.getId();
    }

    private Element getParentElement(Element children, String selector) {
        Elements parents = children.parents().select(selector);
        return parents.get(0);
    }

    private void saveEventSubCategories() {
        Document doc = worker.connectURL("https://opoznai.bg/events/category_id:238");

        List<List<String>> categoriesPages = getEventCategoriesPages(
                doc,
                ".evnt_aside_categories > a.evnt_aside_category"
        );

        List<CategoryRequestDTO> subCategories = new ArrayList<>();
        categoriesPages.forEach(elem -> {
            subCategories.addAll(getEventSubCategories(elem.get(0),elem.get(1)));
        });
        subCategories.forEach(catDTO -> service.save(catDTO));
    }

    private List<List<String>> getEventCategoriesPages(Document doc, String selector) {
        return doc
                .select(selector)
                .stream()
                .map((domElement) ->
                        new ArrayList<String>() {
                            {
                                add(domElement.select("span.event-cat-list").text());
                                add(domElement.attr("href"));
                            }
                        }
                )
                .filter(elem -> !elem.get(0).isEmpty())
                .collect(Collectors.toList());
    }

    private List<CategoryRequestDTO> getEventSubCategories(String parentCategoryName, String url) {
        Document doc = worker.connectURL(url);
        List<String> subCategories = getCategoriesNames(doc, ".evnt_aside_categories > a.evnt_aside_subcategory");
        return subCategories
                .stream()
                .map(cat -> new CategoryRequestDTO(
                        this.getParentId(parentCategoryName),
                        cat.replaceAll("\\d","").trim()
                ))
                .collect(Collectors.toList());
    }

}
