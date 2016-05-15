package com.nanodegree.android.popmovies.utils;

public class ImageUrlHelper {

  public static String getPicassoImageUrl(String posterPath, String size) {
    if (posterPath == null) {
      return null;
    }
    if (size == null) {
      //set default size
      size = PopMovieAppConstants.PICASSO_THUMBNAIL_SIZE_SMALL;
    }
    return PopMovieAppConstants.PICASSO_BASE_URL.concat(size).concat(posterPath);
  }

}
