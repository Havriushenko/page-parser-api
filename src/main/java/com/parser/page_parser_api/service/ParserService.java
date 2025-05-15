package com.parser.page_parser_api.service;

import com.parser.page_parser_api.entity.Category;
import com.parser.page_parser_api.entity.Event;
import com.parser.page_parser_api.entity.League;
import com.parser.page_parser_api.entity.Market;
import com.parser.page_parser_api.entity.Runner;
import com.parser.page_parser_api.exception.PrintExecutionException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ParserService {

  private final OkHttpService okHttpService;

  public void getTopLeaguesAsync(String baseUrl) {
    try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
      Map<Category, CompletableFuture<List<League>>> futuresMap = new EnumMap<>(Category.class);

      for (Category category : Category.values()) {
        CompletableFuture<List<League>> future = CompletableFuture.supplyAsync(() -> this.fetchTopLeagues(category, baseUrl),
            executor
        );
        futuresMap.put(category, future);
      }
      CompletableFuture.allOf(futuresMap.values().toArray(new CompletableFuture[0])).join();

      this.printTopLeaguesByCategory(futuresMap);
    }
  }

  private List<League> fetchTopLeagues(Category category, String baseUrl) {
    Set<League> topLeagues = okHttpService.getTopLeaguesByCategory(baseUrl, category);
    return topLeagues.stream()
        .map(league -> okHttpService.getEventByLeague(baseUrl, league))
        .toList();
  }

  private void printTopLeaguesByCategory(Map<Category, CompletableFuture<List<League>>> futuresMap) {
    futuresMap.forEach((category, future) -> {
      try {
        List<League> leagues = future.get();
        for (League league : leagues) {
          System.out.printf("%s, %s%n", category.getValue().toUpperCase(), league.getName());
          for (Event match : league.getMatches()) {
            System.out.printf("\t\t%s, %s, %s%n", match.name(), match.date(), match.id());
            for (Market market : match.markets()) {
              System.out.printf("\t\t%s%n", market.name());
              for (Runner runner : market.runners()) {
                System.out.printf("\t\t\t%s, %.2f, %s%n", runner.name(), runner.price(), runner.id());
              }
            }
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error(e.getMessage());
        throw new PrintExecutionException(e);
      }
    });
  }
}
