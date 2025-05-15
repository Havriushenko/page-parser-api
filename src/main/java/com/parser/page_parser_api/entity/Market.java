package com.parser.page_parser_api.entity;

import java.util.List;
import lombok.Builder;

@Builder
public record Market(String name, List<Runner> runners) {

}
