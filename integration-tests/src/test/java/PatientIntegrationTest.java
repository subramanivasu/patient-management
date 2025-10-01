import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class PatientIntegrationTest {

    @BeforeAll
    static void setUp(){
        RestAssured.baseURI = "http://localhost:4004/";
    }

    @Test
    public void shouldReturnPatientsWithValidToken(){
        String loginPayload = """
                {
                    "email":"testuser@test.com",
                    "password":"password123"
                }
                """;

        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token",notNullValue())
                .extract()
                .jsonPath()
                .get("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/patients")
                .then()
                .body("patients",notNullValue());
    }

    @Test
    public void shouldReturnPatientEntityUponCreation(){
        String patientPayload = """
                {
                   "name": "Kylian Mbappe",
                   "address": "No 314, Rue Saint-Denis, Paris, France",
                   "email": "mbappeJr@gmail.com",
                   "dateOfBirth": "1998-12-20",
                   "registeredDate": "2022-09-21"
                 }
                """;
        String loginPayload = """
                {
                    "email":"testuser@test.com",
                    "password":"password123"
                }
                """;

        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token",notNullValue())
                .extract()
                .jsonPath()
                .get("token");

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body(patientPayload)
                .when()
                .post("/api/patients")
                .then()
                .statusCode(200)
                .body("id",notNullValue())
                .extract().response();

        System.out.println("Patient created with Id : " + response.jsonPath().getString("id"));
    }
}
