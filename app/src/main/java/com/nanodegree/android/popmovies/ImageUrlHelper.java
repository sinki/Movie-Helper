package com.nanodegree.android.popmovies;

public class ImageUrlHelper {

  public static String getPicassoImageUrl(String posterPath, String size) {
    if (posterPath == null) {
      return null;
    }
    if (size == null) {
      //set default size
      size = PopMoviesAppConstants.PICASSO_THUMBNAIL_SIZE_SMALL;
    }
    return PopMoviesAppConstants.PICASSO_BASE_URL.concat(size).concat(posterPath);
  }
}
