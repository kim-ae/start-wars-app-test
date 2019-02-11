package br.com.kimae.starwarsapp;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.kimae.starwarsapp.domain.Character;
import br.com.kimae.starwarsapp.domain.Film;
import br.com.kimae.starwarsapp.domain.Planet;
import br.com.kimae.starwarsapp.utils.StarWarsApiExecutor;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

import static io.restassured.RestAssured.baseURI;

@ActiveProfiles("it")
@RunWith(SpringRunner.class)
public class StarWarsAPIBusinessTest {

    private static final String RESULT = "results";
    private static final String FIRST_RESULT = RESULT + "[0]";
    private static final Function<JsonPath, Character> characterFromResult = jp ->   jp.getObject(FIRST_RESULT, Character.class);
    private static final Function<JsonPath, Film> filmFromResult = jp -> jp.getObject(FIRST_RESULT, Film.class);
    private static final Function<JsonPath, List<Planet>> listPlanetFromResult = jp -> jp.getList(RESULT, Planet.class);
    private static final Function<JsonPath, List<Film>> listFilmFromResult = jp -> jp.getList(RESULT, Film.class);

    @BeforeClass
    public static void setup(){
        baseURI = "blabla";
    }


    @Test
    public void yodaIsntInForceAweken() {
        final String character = "yoda";

        Response yodaResponse = StarWarsApiExecutor.searchPeople(character);

        assertNotNull("Response for yoda search should not return null", yodaResponse);
        assertEquals("Response for yoda search should return 200", HttpStatus.SC_OK, yodaResponse.getStatusCode());

        Character yoda = ofNullable(yodaResponse.getBody())
            .map(ResponseBody::jsonPath)
            .map(characterFromResult)
            .orElse(null);

        assertNotNull("Response body for yoda search should not return null", yoda);
        assertNotNull("URL Should have some value", yoda.getUrl());

        final String film = "The Force Awakens";
        Response theForceAwakensResponse = StarWarsApiExecutor.searchFilm(film);

        assertNotNull("Response for The Force Awakens search should not return null", theForceAwakensResponse);
        assertEquals("Response for The Force Awakens search should return 200", HttpStatus.SC_OK,
            theForceAwakensResponse.getStatusCode());
        Film theForceAwekens = ofNullable(theForceAwakensResponse.getBody())
            .map(ResponseBody::jsonPath)
            .map(filmFromResult)
            .orElse(null);

        assertNotNull("Response body for The Force Awakens search should not return null", theForceAwekens);
        assertNotNull("Should have characters", theForceAwekens.getCharacters());

        assertFalse(theForceAwekens.hasCharacter(yoda));
    }

    @Test
    public void thereIsFilmMostPlanetsInhabited() {

        Response planetsResponse = StarWarsApiExecutor.getPlanets();
        final List<Planet> inhabitedPlanets = new ArrayList<>();

        assertNotNull("Response for planets should not return null", planetsResponse);
        assertEquals("Response for planets should return 200", HttpStatus.SC_OK, planetsResponse.getStatusCode());
        do {
            ofNullable(planetsResponse)
                .map(Response::getBody)
                .map(ResponseBody::jsonPath)
                .map(listPlanetFromResult)
                .map(Collection::stream)
                .orElseGet(() -> Stream.empty())
                .filter(Planet::isInhabited)
                .forEach(inhabitedPlanets::add);

            planetsResponse = getNext(planetsResponse);

        } while (nonNull(planetsResponse));

        final List<String> planetsUrl = inhabitedPlanets.stream().map(Planet::getUrl).collect(Collectors.toList());

        Response filmsResponse = StarWarsApiExecutor.getFilms();

        assertNotNull("Response for films should not return null", filmsResponse);
        assertEquals("Response for films should return 200", HttpStatus.SC_OK, filmsResponse.getStatusCode());

        List<Film> films = new ArrayList<>();

        do{

            ofNullable(filmsResponse)
                .map(Response::getBody)
                .map(ResponseBody::jsonPath)
                .map(listFilmFromResult)
                .map(Collection::stream)
                .orElseGet(() -> Stream.empty())
                .forEach(films::add);

            filmsResponse = getNext(filmsResponse);

        } while(nonNull(filmsResponse));

        Map<Long, List<Film>> inhabitedPlanetsQuantityMap = films.stream()
            .collect(Collectors
                .groupingBy( f -> this.countInhabitedPlanets(f, planetsUrl),
                    Collectors.mapping(Function.identity(), Collectors.toList())));


        final Long greaterKey = Collections.max(inhabitedPlanetsQuantityMap.keySet());

        assertTrue("Should have one film with inhabited planets",greaterKey > 0l);

        assertTrue("Should have only one film with inhabited planets",inhabitedPlanetsQuantityMap.get(greaterKey).size() == 1);
    }

    private long countInhabitedPlanets(final Film film, final List<String> planetsInhabited){
        return planetsInhabited.stream().filter(p -> film.getPlanets().contains(p)).count();
    }

    private Response getNext(Response previousResponse){
        return ofNullable(previousResponse)
            .map(Response::getBody)
            .map(ResponseBody::jsonPath)
            .map(jp -> (String) jp.get("next"))
            .map(StarWarsApiExecutor::execute)
            .orElse(null);
    }
}
