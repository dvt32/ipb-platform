package com.ipb.platform;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ipb.platform.dto.responses.CategoryResponseDTO;
import org.json.JSONException;
import org.json.JSONObject;
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CategoryTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testAddCategory() throws Exception {
		JSONObject body = this.getBody(null, "first");
		this.createCategory(body);
		CategoryResponseDTO[] categoryList = getAll();

		assertEquals(1, categoryList.length);
		verifyParentCategory(body, categoryList[0]);
	}

	@Test
	public void testGetCategory() throws Exception {
		JSONObject body = this.getBody(null, "first");
		verifyParentCategory(body, getCategoryById(createCategory(body)));
	}

	@Test
	public void testUpdateCategory() throws Exception {
		JSONObject category1 = this.getBody(null, "first");
		long id = this.createCategory(category1);

		JSONObject category2 = this.getBody(null, "second");
		Long id2 = this.createCategory(category2);

		JSONObject category3 = this.getBody(null, "second");

		CategoryResponseDTO updatedCategory = this.updateCategory(id2, category3);
		verifyParentCategory(category2, updatedCategory);
	}

	@Test
	public void testDeleteCategory() throws Exception {
		JSONObject body = this.getBody(null, "first");
		long id = this.createCategory(body);
		assertEquals(true, this.deleteCategoryById(id));
	}

	@Test
	public void testGetChildrenCategory() throws Exception {
		JSONObject parent = this.getBody(null, "first");
		long id = this.createCategory(parent);

		JSONObject childern1 = this.getBody(id, "childern 1");
		this.createCategory(childern1);

		JSONObject childern2 = this.getBody(id, "childern 2");
		this.createCategory(childern2);

		CategoryResponseDTO[] childrenList = this.getByParentId(id);
		assertEquals(2, childrenList.length);
		this.verifyCategory(childern1, childrenList[0]);
		this.verifyCategory(childern2, childrenList[1]);
	}

	private Long createCategory(JSONObject body) throws Exception {
		ResultActions createCategoryResultAction = mockMvc
				.perform(post("/categories/create").contentType("application/json").content(body.toString()))
				.andExpect(status().isOk());


		MvcResult result = createCategoryResultAction.andReturn();
		String newCategoryId = result.getResponse()
				.getContentAsString();

		return Long.parseLong(newCategoryId);
	}

	private CategoryResponseDTO getCategoryById(Long id) throws Exception {
		ResultActions getCategoryResultAction = mockMvc.perform(
				get("/categories/id/" + id).accept("application/json;charset=UTF-8")
		).andExpect(status().isOk());

		MvcResult result = getCategoryResultAction.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		return objectMapper.readValue(contentAsString, CategoryResponseDTO.class);
	}

	private boolean deleteCategoryById(Long id) throws Exception {
		ResultActions getCategoryResultAction = mockMvc.perform(
				delete("/categories/id/" + id).accept("application/json;charset=UTF-8")
		).andExpect(status().isOk());

		MvcResult result = getCategoryResultAction.andReturn();
		String contentAsString = result.getResponse().getContentAsString();

		return Boolean.parseBoolean(contentAsString);
	}

	private CategoryResponseDTO updateCategory(Long id, JSONObject body) throws Exception {
		ResultActions andExpect = mockMvc.perform(
				put("/categories/update/" + id).accept("application/json;charset=UTF-8")
						.contentType("application/json").content(body.toString())
		).andExpect(status().isOk());

		MvcResult result = andExpect.andReturn();
		String contentAsString = result.getResponse()
				.getContentAsString();

		return objectMapper.readValue(contentAsString, CategoryResponseDTO.class);
	}

	private CategoryResponseDTO[] getAll() throws Exception{
		ResultActions andExpect = mockMvc.perform(
				get("/categories/")
						.accept("application/json;charset=UTF-8")
		).andExpect(status().isOk());

		MvcResult result = andExpect.andReturn();
		String contentAsString = result.getResponse()
				.getContentAsString();

		return objectMapper.readValue(contentAsString, CategoryResponseDTO[].class);
	}

	private CategoryResponseDTO[] getByParentId(Long parentId) throws Exception{
		ResultActions andExpect = mockMvc.perform(
				get("/categories/parentId/" + parentId)
						.accept("application/json;charset=UTF-8")
		).andExpect(status().isOk());

		MvcResult result = andExpect.andReturn();
		String contentAsString = result.getResponse()
				.getContentAsString();

		return objectMapper.readValue(contentAsString, CategoryResponseDTO[].class);
	}

	private JSONObject getBody(Long parentId, String name) throws Exception {
		return new JSONObject()
				.put("parentId", parentId)
				.put("name", name);
	}

	private void verifyParentCategory(JSONObject expected, CategoryResponseDTO actual) throws JSONException {
		assertEquals(null, actual.getParentId());
		assertEquals(expected.getString("name"), actual.getName());
		assertNotNull(actual.getId());
	}

	private void verifyCategory(JSONObject expected, CategoryResponseDTO actual) throws JSONException {
		Long parentId = expected.getLong("parentId");
		assertEquals(parentId, actual.getParentId());
		assertEquals(expected.getString("name"), actual.getName());
		assertNotNull(actual.getId());
	}

}
