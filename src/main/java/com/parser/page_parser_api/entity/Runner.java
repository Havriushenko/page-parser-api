package com.parser.page_parser_api.entity;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record Runner(Long id, String name, BigDecimal price) {

}
