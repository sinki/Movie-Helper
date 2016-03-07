package com.nanodegree.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

  private long id;
  private String poster_path;
  private String overview;
  private String release_date;
  private String original_title;
  private String title;
  private float vote_average;

  public Movie() {
  }

  public Movie(Parcel in) {
    readFromParcel(in);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getPoster_path() {
    return poster_path;
  }

  public void setPoster_path(String poster_path) {
    this.poster_path = poster_path;
  }

  public String getOverview() {
    return overview;
  }

  public void setOverview(String overview) {
    this.overview = overview;
  }

  public String getRelease_date() {
    return release_date;
  }

  public void setRelease_date(String release_date) {
    this.release_date = release_date;
  }

  public String getOriginal_title() {
    return original_title;
  }

  public void setOriginal_title(String original_title) {
    this.original_title = original_title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public float getVote_average() {
    return vote_average;
  }

  public void setVote_average(float vote_average) {
    this.vote_average = vote_average;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeLong(id);
    parcel.writeString(title);
    parcel.writeString(original_title);
    parcel.writeString(release_date);
    parcel.writeString(overview);
    parcel.writeString(poster_path);
    parcel.writeFloat(vote_average);

  }

  private void readFromParcel(Parcel in) {
    id = in.readLong();
    title = in.readString();
    original_title = in.readString();
    release_date = in.readString();
    overview = in.readString();
    poster_path = in.readString();
    vote_average = in.readFloat();
  }

  public static final Parcelable.Creator CREATOR =
      new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
          return new Movie(in);
        }

        public Movie[] newArray(int size) {
          return new Movie[size];
        }
      };
}
