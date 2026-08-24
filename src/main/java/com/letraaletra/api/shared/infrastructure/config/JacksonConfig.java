package com.letraaletra.api.shared.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class JacksonConfig {

    @Bean
    JsonMapper jsonMapper(List<RoomRequestHandler<?>> handlerList) {
        NamedType[] subtypes = handlerList.stream()
                .map(RoomRequestHandler::getType)
                .map(type -> {
                    JsonTypeName typeName = type.getAnnotation(JsonTypeName.class);

                    if (typeName == null) {
                        throw new IllegalStateException(
                                "WsRequest sem @JsonTypeName: " + type.getName());
                    }

                    return new NamedType(type, typeName.value());
                })
                .toArray(NamedType[]::new);

        return JsonMapper.builder()
                .registerSubtypes(subtypes)
                .build();
    }
}
