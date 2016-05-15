package com.nanodegree.android.popmovies;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO for GSON conversion of API response
 */
public class MovieListResponse {
  private int page;
  private ArrayList<Movie> results;
  private long totalResults;
  private long totalPages;

  public MovieListResponse() {
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public ArrayList<Movie> getResults() {
    return results;
  }

  public void setResults(ArrayList<Movie> results) {
    this.results = results;
  }

  public long getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(long totalResults) {
    this.totalResults = totalResults;
  }

  public long getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(long totalPages) {
    this.totalPages = totalPages;
  }
}
