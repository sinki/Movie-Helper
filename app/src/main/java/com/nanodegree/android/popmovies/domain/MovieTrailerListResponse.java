package com.nanodegree.android.popmovies.domain;

import com.nanodegree.android.popmovies.domain.MovieTrailer;

import java.util.List;

/**
 * POJO for GSON conversion of API response
 */
public class MovieTrailerListResponse {
  private int page;
  private List<MovieTrailer> results;

  public MovieTrailerListResponse() {
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public List<MovieTrailer> getResults() {
    return results;
  }

  public void setResults(List<MovieTrailer> results) {
    this.results = results;
  }


}
