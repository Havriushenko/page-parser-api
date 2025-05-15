package com.parser.page_parser_api.parser;

import com.parser.page_parser_api.entity.Event;
import com.parser.page_parser_api.entity.League;
import java.util.Set;

public interface JsonParser {

  Set<League> parseLeagueByCategory(String json, String category);

  Set<Event> parseEventsByLeague(String json);
}
