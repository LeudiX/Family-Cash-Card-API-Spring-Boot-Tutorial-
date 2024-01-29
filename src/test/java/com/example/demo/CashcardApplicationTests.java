package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

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
	private TestRestTemplate restTemplate; /*Injecting a test helper that’ll allow us to make HTTP requests to the locally running application*/

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnACashCardWhenDataIsSaved(){
		/*Using restTemplate to make an HTTP GET request to our application endpoint /cashcards/99*/
		ResponseEntity<String> response =  restTemplate.getForEntity("/cashcards/99", String.class);
		
		/*Expecting the HTTP reponse status code to be 200 OK*/
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		/*Converts the response String into a JSON-aware object with lots of helper methods*/
		DocumentContext dContext = JsonPath.parse(response.getBody());
		
		Number id = dContext.read("$.id");
		Double amount  = dContext.read("$.amount");
		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(123.45);
	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId(){
		ResponseEntity<String> response =  restTemplate.getForEntity("/cashcards/1000", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

}
