package com.parser.page_parser_api.service;

import com.parser.page_parser_api.entity.Category;
import com.parser.page_parser_api.entity.Event;
import com.parser.page_parser_api.entity.League;
import com.parser.page_parser_api.exception.OkHttpRequestException;
import com.parser.page_parser_api.parser.JsonParser;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@RequiredArgsConstructor
public class OkHttpService {

  private static final String API_2_GET_LEAGUES = "/api-2/betline/sports?ctag=en-US&flags=urlv2&";
  private static final String API_GET_MATCHES_BY_LEAGUE_ID = "/api-2/betline/changes/all?ctag=en-US&vtag=9c2cd386-31e1-4ce9-a140-28e9b63a9300&league_id=%s&hideClosed=true&flags=reg,urlv2,mm2,rrc,nodup";

  private final JsonParser jsonParser;

  public Set<League> getTopLeaguesByCategory(String baseUrl, Category category) throws OkHttpRequestException {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(this.buildUrl(baseUrl, API_2_GET_LEAGUES, ""))
        .get()
        .build();

    try (Response response = client.newCall(request).execute()) {
      return jsonParser.parseLeagueByCategory(response.body().string(), category.getValue());
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new OkHttpRequestException(e);
    }
  }

  public League getEventByLeague(String baseUrl, League league) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(this.buildUrl(baseUrl, API_GET_MATCHES_BY_LEAGUE_ID, league.getId().toString()))
        .get()
        .build();

    try (Response response = client.newCall(request).execute()) {
      Set<Event> matches = jsonParser.parseEventsByLeague(response.body().string());
      league.setMatches(matches);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new OkHttpRequestException(e);
    }
    return league;
  }

  private String buildUrl(String baseUrl, String postfix, String value) {
    return baseUrl + postfix.formatted(value);
  }
}
