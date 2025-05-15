package com.parser.page_parser_api.entity;

import java.util.Arrays;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum Category {
  SOCCER("Soccer"),
  TENNIS("Tennis"),
  BASKETBALL("Basketball"),
  HOCKEY("IceHockey");

  private String value;

  Category(String value) {
    this.value = value;
  }

  public static Category getCategoryByCode(String value) {
    return Arrays.stream(Category.values())
        .filter(category -> StringUtils.equalsIgnoreCase(category.getValue(), value))
        .findFirst()
        .orElse(null);
  }
}
