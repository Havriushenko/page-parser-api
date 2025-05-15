package com.parser.page_parser_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parser.page_parser_api.parser.impl.JsonParserImpl;
import com.parser.page_parser_api.service.OkHttpService;
import com.parser.page_parser_api.service.ParserService;

public class PageParserApiApplication {

  private static final String BASE_URL = "https://leonbets.com";

  public static void main(String[] args) {
    ParserService service = new ParserService(new OkHttpService(new JsonParserImpl(new ObjectMapper())));
    service.getTopLeaguesAsync(BASE_URL);
  }

}
