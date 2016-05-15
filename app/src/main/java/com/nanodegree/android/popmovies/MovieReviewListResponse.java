package com.nanodegree.android.popmovies;

import java.util.List;

/**
 * POJO for GSON conversion of API response
 */
public class MovieReviewListResponse {
  private int page;
  private List<MovieReview> results;
  private long totalResults;
  private long totalPages;


  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public List<MovieReview> getResults() {
    return results;
  }

  public void setResults(List<MovieReview> results) {
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
