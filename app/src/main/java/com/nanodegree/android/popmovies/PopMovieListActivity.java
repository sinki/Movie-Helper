package com.nanodegree.android.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.nanodegree.android.popmovies.domain.Movie;


public class PopMovieListActivity extends AppCompatActivity
    implements PopMovieListFragment.Callbacks {


  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_popmovie_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setTitle(getTitle());

    if (findViewById(R.id.popmovie_detail_container) != null) {
      mTwoPane = true;
    } else {
      mTwoPane = false;
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_movie_list, menu);
    return true;
  }

  @Override
  public void onItemSelected(Movie movie) {
    if (mTwoPane) {
      Bundle arguments = new Bundle();
      arguments.putParcelable("movie", movie);
      arguments.putBoolean("mTwoPane", mTwoPane);
      PopMovieDetailFragment fragment = new PopMovieDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.popmovie_detail_container, fragment)
          .commit();

    } else {
      Intent intent = new Intent(this, PopMovieDetailActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("movie", movie);
      intent.putExtra("mTwoPane", mTwoPane);
      startActivity(intent);
    }
  }
}
