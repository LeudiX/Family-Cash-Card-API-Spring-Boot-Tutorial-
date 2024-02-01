package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/*start our Spring Boot application and make it available for our test to perform requests to it*/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate; /*
											 * Injecting a test helper that’ll allow us to make HTTP requests to the
											 * locally running application
											 */

	@Test
	void shouldReturnACashCardWhenDataIsSaved() {
		/*
		 * Using restTemplate to make an HTTP GET request to our application endpoint
		 * /cashcards/99
		 */
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/3", String.class);

		/* Expecting the HTTP reponse status code to be 200 OK */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		/*
		 * Converts the response String into a JSON-aware object with lots of helper
		 * methods
		 */
		DocumentContext dContext = JsonPath.parse(response.getBody());

		Number id = dContext.read("$.id");
		Double amount = dContext.read("$.amount");
		assertThat(id).isEqualTo(3);
		assertThat(amount).isEqualTo(123.45);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	void shouldCreateANewCashCard() {

		/*
		 * database will create and manage all unique CashCard.id values for us. We
		 * shouldn't provide one
		 */
		CashCard cashCard = new CashCard(null, 100.0);

		ResponseEntity<Void> response = restTemplate.postForEntity("/cashcards", cashCard, Void.class);

		/*
		 * Expecting the HTTP response status code to be 201 CREATED, which is
		 * semantically
		 * correct if our API creates a new CashCard from our request.
		 */
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		/* Obtaninig the location of the recently created new CashCard resource through the Response Header */
		URI locationOfNewCashCard = response.getHeaders().getLocation();
		/*Performing a GET to access the newly created CashCard resource */
		ResponseEntity<String> responseToNewCashCard = restTemplate.getForEntity(locationOfNewCashCard, String.class);

		assertThat(responseToNewCashCard.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext docContxt = JsonPath.parse(responseToNewCashCard.getBody());

		Number id = docContxt.read("$.id");
		Double amount = docContxt.read("$.amount");

		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(100.0);
	}
}
