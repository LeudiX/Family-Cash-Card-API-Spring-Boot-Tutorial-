package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Starting with TDD (Test Driven Development) approach
 * Note: Remember design a failing test first and then create a success one
 */

/*
 * start our Spring Boot application and make it available for our test to
 * perform requests to it
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardApplicationTests {

  @Autowired
  private TestRestTemplate restTemplate;/*
   * Injecting a test helper that’ll allow us to make HTTP requests to the
   * locally running application
   */

  private CashCard[] cashCards;

  @BeforeEach
  void setUp() {/* Creating pre-defined CashCard objects */
    cashCards =
      new CashCard[] {
        new CashCard(1L, 123.45, "LeudiX1"),
        new CashCard(2L, 100.50, "Sarah"),
        new CashCard(3L, 325.33, "Lucy2"),
      };

    for (CashCard cashCard : cashCards) {/*
     * Adding multiple CashCard objects via the POST method(Endpoint) in the
     * API
     */
      restTemplate
        .withBasicAuth(
          "LeudiX1",
          "leo123"
        )/* Added basic authentication for LeudiX1 user */
        .postForEntity("/cashcards", cashCard, Void.class);
    }
  }

  @Test
  void shouldReturnACashCardWhenDataIsSaved() {
    /*
     * Using restTemplate to make an HTTP GET request to our application endpoint
     * /cashcards/1
     */
    ResponseEntity<String> response = restTemplate
      .withBasicAuth(
        "LeudiX1",
        "leo123"
      )/* added basic authentication for LeudiX1 user */
      .getForEntity("/cashcards/1", String.class);

    /* Expecting the HTTP reponse status code to be 200 OK */
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    /*
     * Converts the response String into a JSON-aware object with lots of helper
     * methods
     */
    DocumentContext dContext = JsonPath.parse(response.getBody());

    Number id = dContext.read("$.id");
    Double amount = dContext.read("$.amount");
    assertThat(id).isEqualTo(1);
    assertThat(amount).isEqualTo(123.45);
  }

  @Test
  void shouldNotReturnACashCardWithAnUnknownId() {
    ResponseEntity<String> response = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .getForEntity("/cashcards/1000", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isBlank();
  }

  @Test
  void shouldCreateANewCashCard() {
    /*
     * database will create and manage all unique CashCard.id values for us. We
     * shouldn't provide one
     */
    CashCard cashCard = new CashCard(null, 100.0, "LeudiX1");

    ResponseEntity<Void> response = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .postForEntity("/cashcards", cashCard, Void.class);

    /*
     * Expecting the HTTP response status code to be 201 CREATED, which is
     * semantically
     * correct if our API creates a new CashCard from our request.
     */
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    /*
     * Obtaninig the location of the recently created new CashCard resource through
     * the Response Header
     */
    URI locationOfNewCashCard = response.getHeaders().getLocation();
    /* Performing a GET to access the newly created CashCard resource */
    ResponseEntity<String> responseToNewCashCard = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .getForEntity(locationOfNewCashCard, String.class);

    assertThat(responseToNewCashCard.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext docContxt = JsonPath.parse(responseToNewCashCard.getBody());

    Number id = docContxt.read("$.id");
    Double amount = docContxt.read("$.amount");

    assertThat(id).isNotNull();
    assertThat(amount).isEqualTo(100.0);
  }

  /*
   * We should be able to list all CashCards.
   */
  @Test
  void shouldReturnAllCashCardsWhenListIsRequested() {
    ResponseEntity<String> response2 = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .getForEntity("/cashcards", String.class);

    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext docContxt = JsonPath.parse(response2.getBody());

    int cashCardCount = docContxt.read(
      "$.length()"
    );/* calculates the length of the array */

    assertThat(cashCardCount).isEqualTo(3);

    JSONArray ids = docContxt.read(
      "$..id"
    );/* retrieves the list of all id values returned */
    JSONArray amounts = docContxt.read(
      "$..amount"
    );/* retrieves the list of all amount values returned */

    /* while the list contain everything I assert, the order does not matter */
    assertThat(ids).containsExactlyInAnyOrder(1, 2, 3);
    assertThat(amounts).containsExactlyInAnyOrder(123.45, 100.5, 325.33);
  }

  /*
   * Testing paging (Showing 1 page at a time for each CashCard element)
   */
  @Test
  void shouldReturnPagedCashCardsWhenPagingIsRequested() {
    ResponseEntity<String> responseEntity = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .getForEntity("/cashcards?page=0&size=1", String.class);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext docContext = JsonPath.parse(responseEntity.getBody());
    JSONArray page = docContext.read("$[*]");

    assertThat(page.size()).isEqualTo(1);
  }

  /*
   * Testing sorting (Order by amount DESC)
   */
  @Test
  void shouldReturnASortedPageofCashCards() {
    ResponseEntity<String> responseEntity = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    DocumentContext docContext = JsonPath.parse(responseEntity.getBody());
    JSONArray page = docContext.read("$[*]");

    assertThat(page.size()).isEqualTo(1);

    double amount = docContext.read("$[0].amount");
    assertThat(amount).isEqualTo(325.33);
  }

  /*
   * Testing sorting with default values (Order by amount ASC)
   */
  @Test
  void shouldReturnASortedPageofCashCardsWithNoParametersAndUseDefaultValues() {
    ResponseEntity<String> responseEntity = restTemplate
      .withBasicAuth("LeudiX1", "leo123")
      .getForEntity("/cashcards", String.class);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    DocumentContext docContext = JsonPath.parse(responseEntity.getBody());
    JSONArray page = docContext.read("$[*]");

    assertThat(page.size()).isEqualTo(3);

    JSONArray amounts = docContext.read("$[*].amount");
    assertThat(amounts).containsExactly(100.5, 123.45, 325.33);
  }

  /*
   * A user who don't posses a cash card shouldn't be able to get access to Family Card
   */
  @Test
  void shouldRejectAnyUserWhoAreNotACardOwner() {
    ResponseEntity<String> responseEntity = restTemplate
      .withBasicAuth("Sarah", "sara123")
      .getForEntity("/cashcards/2", String.class);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
  /*
   * asserts that my API returns a 404 NOT FOUND when a user attempts to access 
   * a Cash Card they do not own, so the CashCard existence it's not revealed to
   * the user
   */
  @Test
  void usersShouldNotHaveAccessToOtherUsersCards() {
	ResponseEntity<String> responseEntity = restTemplate.withBasicAuth("Sarah", "sara123").getForEntity("/cashcards/3", String.class); //Lucy's CashCard data
	   assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
