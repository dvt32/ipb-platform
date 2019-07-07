package com.ipb.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CityTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testAddObject() {
		this.addCategories();
		JSONObject object = this.getBody("CITY 1","Some CITY text",
				55.362, 77.32, "CITY", this.getImageList(1L),
				this.getCategoryList());

		this.createObject(object);
		ObjectResponseDTO[] objectList = getAll();

		assertEquals(1, objectList.length);
		verifyObject(object, objectList[0]);
	}

	@Test
	public void testGetObject()  {
		this.addCategories();
		JSONObject object = this.getBody("CITY 1","Some CITY text",
				55.362, 77.32, "CITY", this.getImageList(1L),
				this.getCategoryList());

		verifyObject(object, this.getObjectById(this.createObject(object)));
	}

	@Test
	public void testUpdateObject()  {
		this.addCategories();
		JSONObject object = this.getBody("CITY 1","Some CITY text",
				55.362, 77.32, "CITY", this.getImageList(1L),
				this.getCategoryList());

		long id = this.createObject(object);

		JSONObject object2 = this.getBody("CITY 2","Some CITY text",
				55.362, 77.32, "CITY", this.getImageList(1L),
				this.getCategoryList());
		Long id2 = this.createObject(object2);

		JSONObject updateObject = this.getBody("CITY 2 UPDATE","Some CITY text UPDATE",
				55.3622, 77.322, "CITY", this.getImageList(1L),
				this.getCategoryList());

		ObjectResponseDTO updatedObject = this.updateObject(id2, updateObject);
		verifyObject(updateObject, updatedObject);
	}

	@Test
	public void testDeleteObject()  {
		JSONObject object = this.getBody("CITY 1","Some CITY text",
				55.362, 77.32, "CITY",
				this.getImageList(1L), this.getCategoryList());

		long id = this.createObject(object);
		assertEquals(true, this.deleteObjectById(id));
	}

	@Before
	public void beforeAllTest()  {
		this.deleteAll();
	}

	private Long createObject(JSONObject body) {
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

	private ObjectResponseDTO getObjectById(Long id)  {
		ResultActions getObjectResultAction = null;
		String contentAsString = null;

		try {
			getObjectResultAction = mockMvc.perform(
					get("/cities/id/" + id+ "/").accept("application/json;charset=UTF-8")
			).andExpect(status().isOk());
			MvcResult result = getObjectResultAction.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, ObjectResponseDTO.class);
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
					delete("/cities/delete/" + id).accept("application/json;charset=UTF-8")
			).andExpect(status().isOk());
			MvcResult result = getObjectResultAction.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return Boolean.parseBoolean(contentAsString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private ObjectResponseDTO updateObject(Long id, JSONObject body)  {
		ResultActions andExpect = null;
		String contentAsString = null;

		try {
			andExpect = mockMvc.perform(
					put("/cities/update/" + id).accept("application/json;charset=UTF-8")
							.contentType("application/json").content(body.toString())
			).andExpect(status().isOk());
			MvcResult result = andExpect.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, ObjectResponseDTO.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private ObjectResponseDTO[] getAll() {
		ResultActions andExpect = null;
		String contentAsString = null;
		try {
			andExpect = mockMvc.perform(
					get("/cities/")
							.accept("application/json;charset=UTF-8")
			).andExpect(status().isOk());
			MvcResult result = andExpect.andReturn();
			contentAsString = result.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, ObjectResponseDTO[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void deleteAll()  {
		ObjectResponseDTO[] allObjects = getAll();
		Arrays.stream(allObjects).forEach(obj -> {
			try {
				this.deleteObjectById(obj.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	private JSONObject getBody(String name, String description, Double latitude, Double longitude, String type,
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

	private void verifyObject(JSONObject expected, ObjectResponseDTO actual) {
		try {
			assertEquals(expected.getString("name"), actual.getName());
			assertEquals(expected.getString("description"), actual.getDescription());
			assertEquals(expected.getDouble("latitude"), actual.getLatitude(), 0.001);
			assertEquals(expected.getDouble("longitude"), actual.getLongitude(), 0.001);
			assertEquals(expected.getString("type"), actual.getType());

			if (expected.getJSONArray("images") != null && actual.getImages() != null) {
				assertEquals(expected.getJSONArray("images").length(), actual.getImages().size());
			}
			if (expected.getJSONArray("categories") != null && actual.getCategories() != null) {
				assertEquals(expected.getJSONArray("categories").length(), actual.getCategories().size());
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
