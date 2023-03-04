package spotify.oauth2.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTests {

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    String access_token = "<YOUR-SPOTIFY-ACCESS-TOKE>";

    @BeforeClass
    public void beforeClass() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setBaseUri("https://api.spotify.com")
                .setBasePath("/v1")
                .addHeader("Authorization", "Bearer " + access_token)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        requestSpecification = requestSpecBuilder.build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder().log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    @Test
    public void ShouldBeAbleToCreateAPlaylist() {
        String payload = "{\n" +
                "    \"name\": \"New Playlist\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        given(requestSpecification)
                .body(payload)
                .when().post("/users/31jizcpbotwuyg6zxtg7fgdszuoy/playlists")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(201)
                .body("name", equalTo("New Playlist"),
                        "description", equalTo("New playlist description"),
                        "public", equalTo(false));
    }

    @Test
    public void ShouldBeAbleToGetAPlaylist() {
        given(requestSpecification)
                .when().get("/playlists/42D04JU6vNIGIflMggWI07")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(200)
                .body("name", equalTo("New Playlist"),
                        "description", equalTo("New playlist description"),
                        "public", equalTo(false));
    }

    @Test
    public void ShouldBeAbleToUpdateAPlaylist() {
        String payload = "{\n" +
                "    \"name\": \"Updated Playlist\",\n" +
                "    \"description\": \"Updated description\",\n" +
                "    \"public\": false\n" +
                "}";
        given(requestSpecification)
                .body(payload)
                .when().put("/playlists/42D04JU6vNIGIflMggWI07")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void ShouldNotBeAbleToCreateAPlaylistWithoutName() {
        String payload = "{\n" +
                "    \"name\": \"\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        given(requestSpecification)
                .body(payload)
                .when().post("/users/31jizcpbotwuyg6zxtg7fgdszuoy/playlists")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(400)
                .body("error.status", equalTo(400),
                        "error.message", equalTo("Missing required field: name"));
    }

    @Test
    public void ShouldNotBeAbleToCreateAPlaylistWithExpiredToken() {
        String payload = "{\n" +
                "    \"name\": \"New Playlist\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": false\n" +
                "}";
        given()
                .baseUri("https://api.spotify.com")
                .basePath("/v1")
                .header("Authorization", "Bearer " + "ExpiredToken")
                .contentType(ContentType.JSON)
                .log().all()
                .body(payload)
                .when().post("/users/31jizcpbotwuyg6zxtg7fgdszuoy/playlists")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(401)
                .body("error.status", equalTo(401),
                        "error.message", equalTo("Invalid access token"));
    }
}
