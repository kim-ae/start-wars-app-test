package br.com.kimae.starwarsapp.utils;

import static br.com.kimae.starwarsapp.utils.StarWarsEndpoints.BASE_URI;
import static br.com.kimae.starwarsapp.utils.StarWarsEndpoints.CHAR_ENDPOINT;
import static br.com.kimae.starwarsapp.utils.StarWarsEndpoints.FILM_ENDPOINT;
import static br.com.kimae.starwarsapp.utils.StarWarsEndpoints.PLANET_ENDPOINT;
import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class StarWarsApiExecutor {

    private static final String SEARCH_COMMAND = "search";
    private static final String PAGE_COMMAND = "page";
    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_URI = "/%s/?%s=%s";



    public static Response searchPeople(final String character){
        final String getCommand = String.format(DEFAULT_URI, CHAR_ENDPOINT, SEARCH_COMMAND, character);
        return given()
            .baseUri(BASE_URI)
            .contentType(ContentType.JSON)
            .when()
            .get(getCommand);
    }

    public static Response searchFilm(final String film){
        final String getCommand = String.format(DEFAULT_URI, FILM_ENDPOINT, SEARCH_COMMAND, film);
        return given()
            .baseUri(BASE_URI)
            .contentType(ContentType.JSON)
            .when()
            .get(getCommand);
    }

    public static Response getPlanets(){
        final String getCommand = String.format(DEFAULT_URI, PLANET_ENDPOINT, PAGE_COMMAND, DEFAULT_PAGE);
        return given()
            .baseUri(BASE_URI)
            .contentType(ContentType.JSON)
            .when()
            .get(getCommand);
    }

    public static Response getFilms(){
        final String getCommand = String.format(DEFAULT_URI, FILM_ENDPOINT, PAGE_COMMAND, DEFAULT_PAGE);
        return given()
            .baseUri(BASE_URI)
            .contentType(ContentType.JSON)
            .when()
            .get(getCommand);
    }

    public static Response execute(final String url){
        return given()
            .baseUri("")
            .contentType(ContentType.JSON)
            .when()
            .get(url);
    }


}
