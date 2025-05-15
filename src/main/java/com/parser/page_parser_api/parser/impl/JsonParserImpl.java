package com.parser.page_parser_api.parser.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parser.page_parser_api.entity.Event;
import com.parser.page_parser_api.entity.League;
import com.parser.page_parser_api.entity.Market;
import com.parser.page_parser_api.entity.Runner;
import com.parser.page_parser_api.exception.JsonParerException;
import com.parser.page_parser_api.parser.JsonParser;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class JsonParserImpl implements JsonParser {

  private final ObjectMapper mapper;

  public Set<League> parseLeagueByCategory(String json, String category) {
    Set<League> leagues = new HashSet<>();
    try {
      JsonNode root = mapper.readTree(json);
      for (JsonNode node : root) {
        if (StringUtils.equalsIgnoreCase(node.get("family").asText(), category)) {
          for (JsonNode region : node.path("regions")) {
            for (JsonNode leagueNode : region.path("leagues")) {
              if (Objects.isNull(leagueNode) || !leagueNode.get("top").asBoolean()) {
                continue;
              }
              leagues.add(this.buildLeague(leagueNode));
            }
          }
        }
      }
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      throw new JsonParerException(e);
    }
    return leagues;
  }

  private League buildLeague(JsonNode league) {
    Long id = league.get("id").asLong();
    String name = league.get("name").asText();
    return League.builder()
        .id(id)
        .name(name)
        .build();
  }

  public Set<Event> parseEventsByLeague(String json) {
    Set<Event> eventList = new HashSet<>();
    try {
      JsonNode root = mapper.readTree(json);
      JsonNode data = root.get("data");

      for (int i = 0; i < 2; i++) {
        JsonNode event = data.get(i);
        long id = event.get("id").asLong();
        String name = event.get("name").asText();
        Long kickoff = event.get("kickoff").asLong();
        Set<Market> marketList = new HashSet<>();
        if (Objects.nonNull(event.get("markets"))) {
          for (JsonNode market : event.get("markets")) {
            marketList.add(this.parseMarket(market));
          }
          eventList.add(Event.builder()
              .id(id)
              .name(name)
              .date(Instant.ofEpochMilli(kickoff))
              .markets(marketList)
              .build());
        }
      }
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      throw new JsonParerException(e);

    }
    return eventList;
  }

  private Market parseMarket(JsonNode market) {
    String marketName = market.get("name").asText();
    List<Runner> runners = new ArrayList<>();

    for (JsonNode runner : market.get("runners")) {
      runners.add(this.parseRunner(runner));
    }

    return Market.builder()
        .name(marketName)
        .runners(runners)
        .build();
  }

  private Runner parseRunner(JsonNode runner) {
    return Runner.builder()
        .id(runner.get("id").asLong())
        .name(runner.get("name").asText())
        .price(BigDecimal.valueOf(runner.get("price").asDouble()))
        .build();
  }
}
