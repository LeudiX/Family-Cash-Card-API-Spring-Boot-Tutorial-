package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

/**
 * Starting with TDD (Test Driven Development) approach
 */
@JsonTest /*Using the Jackson framework to provide extensive JSON testing and parsing support*/
public class CashCardJsonTest {
    
    @Autowired /*Create an object of the requested type specified below*/
    private JacksonTester<CashCard> json; /*Wrapper to handle serialization and deserialization of JSON objects*/
    
    @Test
    void cashCardSerializationTest() throws IOException{
        CashCard cashcard = new CashCard(99L,123.45);

        assertThat(json.write(cashcard)).isStrictlyEqualToJson("expected.json");
        assertThat(json.write(cashcard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashcard)).extractingJsonPathNumberValue("@.id").isEqualTo(99);
        assertThat(json.write(cashcard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashcard)).extractingJsonPathNumberValue("@.amount").isEqualTo(123.45);

    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45
                }
                """;
        assertThat(json.parse(expected))
                .isEqualTo(new CashCard(99L, 123.45));
        assertThat(json.parseObject(expected).id()).isEqualTo(99);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }
}
