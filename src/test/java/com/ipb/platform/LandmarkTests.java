package com.ipb.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LandmarkTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	public void testAddObject() {
		JSONObject object = this.getBody("Landmark 1","Some Landmark text",
				55.362, 77.32, "LANDMARK", this.getImageList(1L),
				this.getCategoryList(), 1L, this.getDatesNotWorking(), "8-12");

		this.createObject(object);
		LandmarkResponseDTO[] objectList = getAll();

		assertEquals(1, objectList.length);
		verifyObject(object, objectList[0]);
	}

	@Test
	public void testGetObject() {
		JSONObject object = this.getBody("Landmark 1","Some Landmark text",
				55.362, 77.32, "LANDMARK", this.getImageList(1L),
				this.getCategoryList(), 1L, this.getDatesNotWorking(), "8-12");

		verifyObject(object, this.getObjectById(this.createObject(object)));
	}

	@Test
	public void testUpdateObject()  {
		JSONObject object = this.getBody("Landmark 1","Some Landmark text",
				55.362, 77.32, "LANDMARK", this.getImageList(1L),
				this.getCategoryList(), 1L, this.getDatesNotWorking(), "8-12");

		long id = this.createObject(object);

		JSONObject object2 = this.getBody("Landmark 2","Some Landmark text",
				55.362, 77.32, "LANDMARK", this.getImageList(1L),
				this.getCategoryList(), 1L, this.getDatesNotWorking(), "8-12");
		Long id2 = this.createObject(object2);

		JSONObject updateObject = this.getBody("Landmark 2 UPDATE","Some Landmark text UPDATE",
				55.3622, 77.322, "LANDMARK", this.getImageList(1L),
				this.getCategoryList(), 1L, this.getDatesNotWorking(), "8-24");

		LandmarkResponseDTO updatedObject = this.updateObject(id2, updateObject);
		verifyObject(updateObject, updatedObject);
	}

	@Test
	public void testDeleteObject()  {
		JSONObject object = this.getBody("Landmark 1","Some Landmark text",
				55.362, 77.32, "LANDMARK",
				this.getImageList(1L), this.getCategoryList(),
				1L, this.getDatesNotWorking(), "8-12");

		long id = this.createObject(object);
		assertEquals(true, this.deleteObjectById(id));
	}

	@Before
	public void beforeAllTest()  {
		this.deleteAll();
		this.addCategories();
		this.addCity();
	}

	private Date getEndDate(Date startDate, int afterDays) {
		return new Date(startDate.getTime() + 1000 * 60 * 60 * 24 * afterDays);
	}

	private void addCity() {
		JSONObject object = this.getCityBody("CITY 1","Some CITY text",
				55.362, 77.32, "CITY",
				this.getImageList(1L), this.getCategoryList());

		long id = this.createCityObject(object);
	}

	private Long createCityObject(JSONObject body) {
		ResultActions createObjectResultAction = null;
		String newObjectId = null;
		try {
			createObjectResultAction = mockMvc
					.perform(post("/cities/create").contentType("application/json")
							.content(body.toString()))
					.andExpect(status().isOk());
			MvcResult result = createObjectResultAction.andReturn();
			newObjectId = result.getResponse().getContentAsString();
			return Long.parseLong(newObjectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Long createObject(JSONObject body) {
		ResultActions createObjectResultAction = null;
		String newObjectId = null;
		try {
			createObjectResultAction = mockMvc
					.perform(post("/landmarks/create").contentType("application/json")
							.content(body.toString()))
					.andExpect(status().isOk());
			MvcResult result = createObjectResultAction.andReturn();
			newObjectId = result.getResponse().getContentAsString();
			return Long.parseLong(newObjectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private LandmarkResponseDTO getObjectById(Long id)  {
		ResultActions getObjectResultAction = null;
		String contentAsString = null;

		try {
			getObjectResultAction = mockMvc.perform(
					get("/landmarks/id/" + id+ "/").accept("application/json;charset=UTF-8")
			).andExpect(status().isOk());
			MvcResult result = getObjectResultAction.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, LandmarkResponseDTO.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean deleteObjectById(Long id)  {
		ResultActions getObjectResultAction = null;
		String contentAsString = null;

		try {
			getObjectResultAction = mockMvc.perform(
					delete("/landmarks/delete/" + id).accept("application/json;charset=UTF-8")
			).andExpect(status().isOk());
			MvcResult result = getObjectResultAction.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return Boolean.parseBoolean(contentAsString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	private LandmarkResponseDTO updateObject(Long id, JSONObject body)  {
		ResultActions andExpect = null;
		String contentAsString = null;

		try {
			andExpect = mockMvc.perform(
					put("/landmarks/update/" + id).accept("application/json;charset=UTF-8")
							.contentType("application/json").content(body.toString())
			).andExpect(status().isOk());
			MvcResult result = andExpect.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, LandmarkResponseDTO.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private LandmarkResponseDTO[] getAll() {
		ResultActions andExpect = null;
		String contentAsString = null;
		try {
			andExpect = mockMvc.perform(
					get("/landmarks/")
							.accept("application/json;charset=UTF-8")
			).andExpect(status().isOk());
			MvcResult result = andExpect.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, LandmarkResponseDTO[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void deleteAll()  {
		LandmarkResponseDTO[] allObjects = getAll();
		Arrays.stream(allObjects).forEach(obj -> {
			try {
				this.deleteObjectById(obj.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	private JSONObject getCityBody(String name, String description, Double latitude, Double longitude, String type,
								   JSONArray images, JSONArray categories) {
		try {
			return new JSONObject()
					.put("name", name)
					.put("description", description)
					.put("latitude", latitude)
					.put("longitude", longitude)
					.put("type", type)
					.put("images", images)
					.put("categories",categories);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getBody(String name, String description, Double latitude, Double longitude, String type,
							   JSONArray images, JSONArray categories,
							   Long cityId, JSONArray datesNotWorking, String workTime) {
		try {
			return new JSONObject()
					.put("name", name)
					.put("description", description)
					.put("latitude", latitude)
					.put("longitude", longitude)
					.put("type", type)
					.put("images", images)
					.put("categories", categories)
					.put("cityId", cityId)
					.put("datesNotWorking", datesNotWorking)
					.put("workTime", workTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getImageBody(Long ipbObjectId, String base64code, String path) {
		try {
			return new JSONObject()
					.put("ipbObjectId", ipbObjectId)
					.put("base64code", base64code)
					.put("path", path);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private JSONArray getDatesNotWorking(){
		Date statDate = new Date();
		Date statDate2 = this.getEndDate(statDate, 20);
		Date statDate3 = this.getEndDate(statDate2, 40);
		Date statDate4 = this.getEndDate(statDate3, 60);

		return new JSONArray()
				.put(this.getDatesNotWorkingBody(1L, statDate, this.getEndDate(statDate, 3)))
				.put(this.getDatesNotWorkingBody(1L, statDate2, this.getEndDate(statDate2, 30)))
				.put(this.getDatesNotWorkingBody(1L, statDate3, this.getEndDate(statDate3, 1)))
				.put(this.getDatesNotWorkingBody(1L, statDate4, this.getEndDate(statDate4, 3)));
	}

	private JSONObject getDatesNotWorkingBody(Long landmarkId, Date startDate, Date endDate) {
		try {
			return new JSONObject()
					.put("landmarkId", landmarkId)
					.put("startDate", this.dateFormat.format(startDate))
					.put("endDate", this.dateFormat.format(endDate));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void verifyObject(JSONObject expected, LandmarkResponseDTO actual) {
		try {
			assertEquals(expected.getString("name"), actual.getName());
			assertEquals(expected.getString("description"), actual.getDescription());
			assertEquals(expected.getDouble("latitude"), actual.getLatitude(), 0.001);
			assertEquals(expected.getDouble("longitude"), actual.getLongitude(), 0.001);
			assertEquals(expected.getString("type"), actual.getType());
			assertEquals(expected.getLong("cityId"), actual.getCityId());
			assertEquals(expected.getString("workTime"), actual.getWorkTime());

			if (expected.getJSONArray("images") != null && actual.getImages() != null) {
				assertEquals(expected.getJSONArray("images").length(), actual.getImages().size());
			}

			if (expected.getJSONArray("categories") != null && actual.getCategories() != null) {
				assertEquals(expected.getJSONArray("categories").length(), actual.getCategories().size());
			}

			if (expected.getJSONArray("datesNotWorking") != null && actual.getDatesNotWorking() != null) {
				assertEquals(expected.getJSONArray("datesNotWorking").length(), actual.getDatesNotWorking().size());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		assertNotNull(actual.getId());
	}

	private JSONArray getImageList(Long objectId)  {
		JSONArray images = new JSONArray();
		images.put(this.getImageBody(objectId, null, "https://imgrabo.com/pics/guide/900x600/20160815095547_59057.jpg"));
		images.put(this.getImageBody(objectId, null, "http://static.bnr.bg/gallery/40/4086b6bb64ec9b41dd774d42839b8908.jpg"));
		images.put(this.getImageBody(objectId, null, "https://imgrabo.com/pics/guide/900x600/20160815095547_59057.jpghttps://i.ytimg.com/vi/Ds98521tjNc/maxresdefault.jpg"));
		images.put(this.getImageBody(objectId, null, "https://i.pik.bg/news/559/660_dcf1b116f2e4c057921651bc833417bc.jpg"));
		return images;
	}

	private JSONArray getCategoryList() {
		JSONArray categories = new JSONArray();
		categories.put(1L);
		categories.put(2L);
		categories.put(3L);
		return categories;
	}

	private void addCategories()  {
		Long id = this.createCategory(this.getBody(null, "first"));
		this.createCategory(this.getBody(null, "second"));
		Long id3 = this.createCategory(this.getBody(id, "children 1"));
		this.createCategory(this.getBody(id3,"children 2" ));
	}

	private Long createCategory(JSONObject body) {

		ResultActions createCategoryResultAction = null;
		String newCategoryId = null;

		try {
			createCategoryResultAction = mockMvc
					.perform(post("/categories/create").contentType("application/json").content(body.toString()))
					.andExpect(status().isOk());
			MvcResult result = createCategoryResultAction.andReturn();
			newCategoryId = result.getResponse().getContentAsString();
			return Long.parseLong(newCategoryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getBody(Long parentId, String name)  {
		try {
			return new JSONObject()
					.put("parentId", parentId)
					.put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
