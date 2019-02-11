package br.com.kimae.starwarsapp.domain;

import static java.util.Optional.ofNullable;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Getter
public class Planet implements Serializable {

    private final String population;
    private final String url;

    public boolean isInhabited(){
        return ofNullable(population)
            .map(p -> p.equals("0"))
            .orElse(false);
    }
}
