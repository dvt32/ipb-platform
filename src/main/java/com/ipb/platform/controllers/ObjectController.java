package com.ipb.platform.controllers;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.CategoryRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.services.CategoryService;
import com.ipb.platform.services.impl.CityService;
import com.ipb.platform.services.impl.EventService;
import com.ipb.platform.services.impl.LandmarkService;
import lombok.AllArgsConstructor;
import org.aspectj.util.LangUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ipb.platform.persistence.entities.ObjectType.*;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/objects/")
public class ObjectController {
    private static Map<ObjectType, Double> OBJECTS_RATIO = new HashMap<ObjectType, Double>() {
        {
            put(CITY, 0.2);
            put(EVENT, 0.3);
            put(LANDMARK, 0.5);
        }
    };

    private CityService cityService;

    private EventService eventService;

    private LandmarkService landmarkService;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public List<ObjectResponseDTO> getAllNameContainsOrDescriptionContainsPage (
            @RequestParam(name = "search") String searchValue,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "numberOfObjects", defaultValue = "10") int numberOfObjects
    ) {
        return this.cityService.getAllNameContainsOrDescriptionContains(searchValue, page, numberOfObjects);
    }

    @RequestMapping(value = "around-coordinates", method = RequestMethod.POST)
    public List<ObjectResponseDTO> getAllAroundCoordinates(
            @RequestParam(name = "numberOfObjects", defaultValue = "10") int numberOfObjects,
            @RequestBody List<CoordinatesDTO> coordinates) {

        List<ObjectResponseDTO> objects = this.getObjectAroundByCoordinatesAndType(coordinates, numberOfObjects, CITY);
        objects.addAll(this.getObjectAroundByCoordinatesAndType(coordinates, numberOfObjects, EVENT));
        objects.addAll(this.getObjectAroundByCoordinatesAndType(coordinates, numberOfObjects, LANDMARK));
        return objects;
    }

    private List<ObjectResponseDTO> getObjectAroundByCoordinatesAndType(
            List<CoordinatesDTO> coordinates,
            int numberOfObjects,
            ObjectType type) {

        switch (type) {
            case EVENT:
                return this.eventService.findAllAroundCoordinates(
                        coordinates,
                        (int) (numberOfObjects * OBJECTS_RATIO.get(EVENT))
                );
            case LANDMARK:
                return this.landmarkService.findAllAroundCoordinates(
                        coordinates,
                        (int) (numberOfObjects * OBJECTS_RATIO.get(LANDMARK))
                );
            default:
                return this.cityService.findAllAroundCoordinates(
                        coordinates,
                        (int) (numberOfObjects * OBJECTS_RATIO.get(CITY))
                );
        }
    }

}
