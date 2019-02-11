package br.com.kimae.starwarsapp.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "episodeId")
public class Film implements Serializable {

    @JsonProperty("episode_id")
    private final String episodeId;
    private final List<String> characters;
    private final List<String> planets;

    public boolean hasCharacter(final Character character){
        return characters.contains(character.getUrl());
    }
}
