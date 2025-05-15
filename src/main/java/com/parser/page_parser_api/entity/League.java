package com.parser.page_parser_api.entity;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class League {

  private Long id;
  private String name;
  private Set<Event> matches;
}
