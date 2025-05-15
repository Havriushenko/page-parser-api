package com.parser.page_parser_api.entity;

import java.time.Instant;
import java.util.Set;
import lombok.Builder;

@Builder
public record Event(Long id, String name, Instant date, Set<Market> markets) {

}
