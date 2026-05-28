package com.example.moviesbymood.converters;

import com.example.moviesbymood.dto.ActorDto;
import com.example.moviesbymood.models.Actor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ActorConverter implements Converter<Actor, ActorDto> {
    @Override
    public ActorDto convert(Actor actor) {
        return ActorDto.builder()
                .actorId(actor.getActorId())
                .actorFullName(actor.getActorFullName())
                .actorBirthDate(actor.getActorBirthDate())
                .actorBiography(actor.getActorBiography())
                .actorPhoto(actor.getActorPhoto() != null
                        ? "/files/" + actor.getActorPhoto().getFileInfoFilename()
                        : null)
                .build();
    }
}
