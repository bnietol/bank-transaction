package com.nietol.tcs.transaction;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nietol.tcs.transaction.model.Transaction;
import com.nietol.tcs.transaction.repository.TransactionRepository;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import io.swagger.model.Error;
import io.swagger.model.TransactionDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  TransactionRepository repo;

  private JacksonTester<TransactionDto> jsonTransaction;
  private JacksonTester<Error> jsonError;
  private JacksonTester<TransactionDto[]> jsonTransactionArray;

  static ResponseSpecification okResponse;
  static ResponseSpecification createResponse;
  static ResponseSpecification noContentResponse;
  static ResponseSpecification badResponse;
  static ResponseSpecification notFoundResponse;

  @BeforeClass
  public static void initialData() {

    ResponseSpecBuilder okBuilder = new ResponseSpecBuilder();
    okBuilder.expectStatusCode(HttpStatus.OK.value());
    okBuilder.expectContentType(ContentType.JSON);
    okResponse = okBuilder.build();

    ResponseSpecBuilder createBuilder = new ResponseSpecBuilder();
    createBuilder.expectStatusCode(HttpStatus.CREATED.value());
    createBuilder.expectContentType(ContentType.JSON);
    createResponse = createBuilder.build();

    ResponseSpecBuilder noContentBuilder = new ResponseSpecBuilder();
    noContentBuilder.expectStatusCode(HttpStatus.NO_CONTENT.value());
    noContentResponse = noContentBuilder.build();

    ResponseSpecBuilder badBuilder = new ResponseSpecBuilder();
    badBuilder.expectStatusCode(HttpStatus.BAD_REQUEST.value());
    badBuilder.expectContentType(ContentType.JSON);
    badResponse = badBuilder.build();

    ResponseSpecBuilder notFoundBuilder = new ResponseSpecBuilder();
    notFoundBuilder.expectStatusCode(HttpStatus.NOT_FOUND.value());
    notFoundBuilder.expectContentType(ContentType.JSON);
    notFoundResponse = notFoundBuilder.build();
  }

  @Before
  public void setup() {
    RestAssured.port = port;
    List<Filter> filters = new ArrayList<>();
    filters.add(new RequestLoggingFilter());
    filters.add(new ErrorLoggingFilter());
    RestAssured.filters(filters);
    JacksonTester.initFields(this, new ObjectMapper());
    // Create a initial transaction
    //@formatter:off
      Transaction transaction = Transaction
              .builder()
              .withReference("reference0")
              .withAccountIban("accountIban")
              .withAmount(BigDecimal.ZERO)
              .withFee(BigDecimal.ZERO)
              .withDescription("description")
              .withDate("3019-07-16T16:55:42.000Z")
              .build();

      repo.save(transaction);
      //@formatter:on
  }

  //****************** TEST - CREATE TRANSACTION ******************
  @Test
  public void create_transaction_without_accountIban() throws IOException {
    JSONObject json = createJSONFromFile("transaction/create_transaction_without_accountIban.json");
    //@formatter:off
    assertThat(jsonError
        .write(RestAssured
                .given()
                .contentType("application/json")
                .body(json.toString())
                .when()
                .post("/transaction")
                .then()
                .spec(badResponse)
                .extract()
                .response()
                .as(Error.class)))
            .isEqualToJson(getClass().getClassLoader().getResourceAsStream(
                    "transaction/response/create_transaction_without_accountIban_fail.json"));
    //@formatter:on
  }

  @Test
  public void create_transaction_without_amount() throws IOException {
    JSONObject json = createJSONFromFile("transaction/create_transaction_without_amount.json");
    //@formatter:off
    assertThat(jsonError
        .write(RestAssured
                .given()
                .contentType("application/json")
                .body(json.toString())
                .when()
                .post("/transaction")
                .then()
                .spec(badResponse)
                .extract()
                .response()
                .as(Error.class)))
            .isEqualToJson(getClass().getClassLoader().getResourceAsStream(
                    "transaction/response/create_transaction_without_amount_fail.json"));
    //@formatter:on
  }

  @Test
  public void create_transaction_without_mandatory_fields() throws IOException {
    JSONObject json =
        createJSONFromFile("transaction/create_transaction_without_mandatory_fields.json");
    //@formatter:off
    assertThat(jsonError
        .write(RestAssured
                .given()
                .contentType("application/json")
                .body(json.toString())
                .when()
                .post("/transaction")
                .then()
                .spec(badResponse)
                .extract()
                .response()
                .as(Error.class)))
            .isEqualToJson(getClass().getClassLoader().getResourceAsStream(
                    "transaction/response/create_transaction_without_mandatory_fields_fail.json"));
    //@formatter:on
  }

  @Test
  public void create_transaction_complete_ok() throws IOException {
    JSONObject json = createJSONFromFile("transaction/create_transaction_complete_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .contentType("application/json")
                    .body(json.toString())
                    .when()
                    .post("/transaction")
                    .then()
                    .spec(createResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/create_transaction_complete_ok.json"));
    //@formatter:on
  }

  @Test
  public void create_transaction_ok() throws IOException {
    JSONObject json = createJSONFromFile("transaction/create_transaction_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .contentType("application/json")
                    .body(json.toString())
                    .when()
                    .post("/transaction")
                    .then()
                    .spec(createResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)));
    //@formatter:on
  }

  @Test
  public void z_create_transaction_random_reference_ok() throws IOException {
    JSONObject json = createJSONFromFile("transaction/z_creation_transaction_random_reference_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .contentType("application/json")
                    .body(json.toString())
                    .when()
                    .post("/transaction")
                    .then()
                    .spec(createResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)));
    //@formatter:on
  }

  //****************** TEST - GET TRANSACTION ******************
  @Test
  public void get_transactions_by_iban_sorted_asc() throws IOException {
    //@formatter:off
    assertThat(jsonTransactionArray
            .write(RestAssured
                    .given()
                    .when()
                    .get("/transaction?accountIban=IBAN1&sortByAmount=ASC")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto[].class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_transactions_by_iban_sorted_asc.json"));
    //@formatter:on
  }

  @Test
  public void get_transactions_by_iban_sorted_des() throws IOException {
    //@formatter:off
    assertThat(jsonTransactionArray
            .write(RestAssured
                    .given()
                    .when()
                    .get("/transaction?accountIban=IBAN1&sortByAmount=DES")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto[].class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_transactions_by_iban_sorted_des.json"));
    //@formatter:on
  }

  @Test
  public void get_transactions_sorted_asc() throws IOException {
    //@formatter:off
    assertThat(jsonTransactionArray
            .write(RestAssured
                    .given()
                    .when()
                    .get("/transaction?sortByAmount=ASC")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto[].class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_transactions_sorted_asc.json"));
    //@formatter:on
  }

  @Test
  public void get_transactions_sorted_des() throws IOException {
    //@formatter:off
    assertThat(jsonTransactionArray
            .write(RestAssured
                    .given()
                    .when()
                    .get("/transaction?sortByAmount=DES")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto[].class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_transactions_sorted_des.json"));
    //@formatter:on
  }

  @Test
  public void get_transactions() throws IOException {
    //@formatter:off
    assertThat(jsonTransactionArray
            .write(RestAssured
                    .given()
                    .when()
                    .get("/transaction")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto[].class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_transactions.json"));
    //@formatter:on
  }

  @Test
  public void get_transactions_by_iban_empty_list() throws IOException {
    //@formatter:off
    assertThat(jsonTransactionArray
            .write(RestAssured
                    .given()
                    .when()
                    .get("/transaction?accountIban=x")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto[].class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_transactions_by_iban_empty_list.json"));
    //@formatter:on
  }

  //****************** TEST - GET TRANSACTION STATUS ******************

  @Test
  public void get_status_transaction_not_found() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_not_found.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_not_found.json"));
    //@formatter:on
  }

  @Test
  public void get_status_transaction_client_date_before_today() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_client_date_before_today_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_client_atm_date_before_today.json"));
    //@formatter:on
  }

  @Test
  public void get_status_transaction_atm_date_before_today() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_atm_date_before_today_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_client_atm_date_before_today.json"));
    //@formatter:on
  }

  @Test
  public void get_status_transaction_internal_date_before_today() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_internal_date_before_today_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_internal_date_before_today.json"));
    //@formatter:on
  }

  @Test
  public void get_status_transaction_client_date_after_today() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_client_date_after_today_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_client_date_after_today.json"));
    //@formatter:on
  }

  @Test
  public void get_status_transaction_atm_date_after_today() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_atm_date_after_today_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_atm_date_after_today.json"));
    //@formatter:on
  }

  @Test
  public void get_status_transaction_internal_date_after_today() throws IOException {
    JSONObject json = createJSONFromFile("transaction/get_status_transaction_internal_date_after_today_ok.json");
    //@formatter:off
    assertThat(jsonTransaction
            .write(RestAssured
                    .given()
                    .when()
                    .contentType("application/json")
                    .body(json.toString())
                    .get("/transaction/status")
                    .then()
                    .spec(okResponse)
                    .extract()
                    .response()
                    .as(TransactionDto.class)))
            .isEqualToJson(getClass().getClassLoader()
                    .getResourceAsStream("transaction/response/get_status_transaction_internal_date_after_today.json"));
    //@formatter:on
  }




  private JSONObject createJSONFromFile(String filePath) {

    JSONObject result = null;

    try {
      // Read file into string builder
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder builder = new StringBuilder();

      for (String line = null; (line = reader.readLine()) != null;) {
        builder.append(line).append("\n");
      }

      // Parse into JSONObject
      String resultStr = builder.toString();
      JSONTokener tokener = new JSONTokener(resultStr);
      result = new JSONObject(tokener);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return result;
  }
}
