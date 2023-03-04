package com.spotify.oauth2.tests;

import com.spotify.oauth2.pojo.Playlist;
import com.spotify.oauth2.pojo.Error;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PlaylistTests {

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    String access_token = "BQCBuaIm7S7Fl1TS7UHDfjEnhj_XSxJ5DUZc3yQNpltlkwSSJIEQX5tdCDp7j9e32tZnSTYdAAUgXpygLnKb2FWnIVwY4hwnljAWUAo8B-0vDVmsnGA78FOLW-tySw3C3xN7Qf7WRgERa-AMagNm8NsibIuURawodrQRhHNVzDsvWTAKvALwvfyqQ4LpEw8pnI4gMs0-jVHkqRY3cnJq7dbIeXvq0OSMxuf8SmhOS26vIajaWdmeY_O6ZrQQstMdKIZOqxElXozO0bG3ow";

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
        Playlist requestPlaylist = new Playlist();
        requestPlaylist.setName("New Playlist");
        requestPlaylist.setDescription("New playlist description");
        requestPlaylist.setPublic(false);

        Playlist responsePlaylist = given(requestSpecification)
                .body(requestPlaylist)
                .when().post("/users/31jizcpbotwuyg6zxtg7fgdszuoy/playlists")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(201)
                .extract()
                .response()
                .as(Playlist.class);

        assertThat(responsePlaylist.getName(), equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(), equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.getPublic(), equalTo(requestPlaylist.getPublic()));
    }

    @Test
    public void ShouldBeAbleToGetAPlaylist() {
        Playlist requestPlaylist = new Playlist();
        requestPlaylist.setName("New Playlist");
        requestPlaylist.setDescription("New playlist description");
        requestPlaylist.setPublic(false);

        Playlist responsePlaylist = given(requestSpecification)
                .when().get("/playlists/1nEYt7ljnbvlK1UEHDGHeW")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .as(Playlist.class);
        assertThat(responsePlaylist.getName(), equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription(), equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.getPublic(), equalTo(requestPlaylist.getPublic()));
    }

    @Test
    public void ShouldBeAbleToUpdateAPlaylist() {
        Playlist requestPlaylist = new Playlist();
        requestPlaylist.setName("Updated Playlist");
        requestPlaylist.setDescription("Updated description");
        requestPlaylist.setPublic(false);

        given(requestSpecification)
                .body(requestPlaylist)
                .when().put("/playlists/42D04JU6vNIGIflMggWI07")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void ShouldNotBeAbleToCreateAPlaylistWithoutName() {
        Playlist requestPlaylist = new Playlist();
        requestPlaylist.setName("");
        requestPlaylist.setDescription("New playlist description");
        requestPlaylist.setPublic(false);

        Error error = given(requestSpecification)
                .body(requestPlaylist)
                .when().post("/users/31jizcpbotwuyg6zxtg7fgdszuoy/playlists")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(400)
                .extract()
                .response()
                .as(Error.class);

        assertThat(error.getError().getStatus(), equalTo(400));
        assertThat(error.getError().getMessage(), equalTo("Missing required field: name"));
    }

    @Test
    public void ShouldNotBeAbleToCreateAPlaylistWithExpiredToken() {
        Playlist requestPlaylist = new Playlist();
        requestPlaylist.setName("New Playlist");
        requestPlaylist.setDescription("New playlist description");
        requestPlaylist.setPublic(false);

        Error error = given()
                .baseUri("https://api.spotify.com")
                .basePath("/v1")
                .header("Authorization", "Bearer " + "ExpiredToken")
                .contentType(ContentType.JSON)
                .log().all()
                .body(requestPlaylist)
                .when().post("/users/31jizcpbotwuyg6zxtg7fgdszuoy/playlists")
                .then().spec(responseSpecification)
                .assertThat()
                .statusCode(401)
                .extract()
                .response()
                .as(Error.class);

        assertThat(error.getError().getStatus(), equalTo(401));
        assertThat(error.getError().getMessage(), equalTo("Invalid access token"));
    }
}
