package com.paulfrmbrn.ssmdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SsmDemoApplicationTests {

	private final Logger logger = LoggerFactory.getLogger(SsmDemoApplicationTests.class);

	@Autowired
	protected OrderRepository repository;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	public static PostgreSQLContainer postgreSQLContainer;
	static {
		postgreSQLContainer = new PostgreSQLContainer("postgres:12.1");
		postgreSQLContainer.start();
	}

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testCreate() throws Exception {

		// given
		var commentData = new CommentData("the order now is created");

		// when
		var result = mockMvc.perform(MockMvcRequestBuilders.post("/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentData))
		).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// then
		logger.info("result={}", result);
		var returned = objectMapper.readValue(result, Order.class);
		var stored = repository.findById(returned.getId()).get();
		var expected = new Order(returned.getId(), State.CREATED.name(), commentData.getComment());
		assertEquals(expected, returned);
		assertEquals(expected, stored);

	}

	@Test
	void testConfirm() throws Exception {

		// given
		var order = repository.save(new Order(null, State.CREATED.name(), "none"));
		var commentData = new CommentData("the order now is confirmed");

		// when
		var result = mockMvc.perform(MockMvcRequestBuilders.post("/orders/{id}/confirm", order.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentData))
		).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// then
		logger.info("result={}", result);
		var returned = objectMapper.readValue(result, Order.class);
		var stored = repository.findById(returned.getId()).get();
		var expected = new Order(order.getId(), State.CONFIRMED.name(), commentData.getComment());
		assertEquals(expected, returned);
		assertEquals(expected, stored);

	}

	@Test
	void testConfirmFailOnOrderDoesNotExist() throws Exception {

		var commentData = new CommentData("the order now is confirmed");

		var result = mockMvc.perform(MockMvcRequestBuilders.post("/orders/{id}/confirm", -1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentData))
		).andExpect(status().is5xxServerError()).andReturn().getResponse().getContentAsString();

		logger.info("result={}", result);
		assertEquals("Order does not exist", result);

	}

	@Test
	void testConfirmFailOnOrderIsInWrongState() throws Exception {

		var order = repository.save(new Order(null, State.CONFIRMED.name(), "none"));
		var commentData = new CommentData("the order now is confirmed");

		var result = mockMvc.perform(MockMvcRequestBuilders.post("/orders/{id}/confirm", order.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentData))
		).andExpect(status().is5xxServerError()).andReturn().getResponse().getContentAsString();

		logger.info("result={}", result);
		assertEquals("Order is in wrong state: expected=CREATED, got=CONFIRMED", result);

	}

	@Test
	void testFulfill() throws Exception {

		// given
		var order = repository.save(new Order(null, State.CONFIRMED.name(), "none"));
		var commentData = new CommentData("the order now is confirmed");

		// when
		var result = mockMvc.perform(MockMvcRequestBuilders.post("/orders/{id}/fulfill", order.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentData))
		).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// then
		logger.info("result={}", result);
		var returned = objectMapper.readValue(result, Order.class);
		var stored = repository.findById(returned.getId()).get();
		var expected = new Order(order.getId(), State.FULFILLED.name(), commentData.getComment());
		assertEquals(expected, returned);
		assertEquals(expected, stored);


	}

}
