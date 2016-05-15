package com.nanodegree.android.popmovies.utils;


public enum MovieSortTypeEnum {

  SORT_BY_POPULARITY(1, "Sort by Popularity", "popularity.desc"),
  SORT_BY_RATING(2, "Sort by Rating", "vote_average.desc");

  private int id;
  private String displayName;
  private String apiParamName;

  MovieSortTypeEnum(int id, String displayName, String apiParamName) {
    this.id = id;
    this.displayName = displayName;
    this.apiParamName = apiParamName;
  }

  public int getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getApiParamName() {
    return apiParamName;
  }

  public static MovieSortTypeEnum getMovieSortTypeById(int id) {
    for (MovieSortTypeEnum sortType : values()) {
      if (sortType.getId() == id) {
        return sortType;
      }
    }
    return SORT_BY_POPULARITY;
  }
}
