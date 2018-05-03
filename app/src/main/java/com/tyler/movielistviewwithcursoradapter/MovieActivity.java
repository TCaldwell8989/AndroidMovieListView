package com.tyler.movielistviewwithcursoradapter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MovieActivity extends AppCompatActivity implements MovieCursorAdapter.RatingChangedListener {

	private static final String TAG = "MOVIE ACTIVITY";
	DatabaseManager dbManager;
	MovieCursorAdapter cursorListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie);

		//TODO Create DatabaseManager object
		dbManager = new DatabaseManager(this);

		Button addNew = (Button) findViewById(R.id.add_movie_button);
		final EditText newMovieNameET = (EditText) findViewById(R.id.add_movie_name);
		final RatingBar newMovieRB = (RatingBar) findViewById(R.id.add_movie_rating_bar);
		final EditText newMovieYearET = (EditText) findViewById(R.id.add_movie_year);

		//TODO create cursor
		//TODO create CursorAdapter
		//TODO Configure ListView to use this adapter
		final ListView movieList = (ListView) findViewById(R.id.movie_list_view);
		Cursor cursor = dbManager.getAllMovies();
		cursorListAdapter = new MovieCursorAdapter(this, cursor, true);
		movieList.setAdapter(cursorListAdapter);

		addNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Validation Handling
				if (newMovieYearET.getText().toString().isEmpty() || newMovieNameET.getText().toString().isEmpty()) {
					Toast.makeText(MovieActivity.this, "Error, enter name and year of movie", Toast.LENGTH_LONG).show();
					return;
				}
				// Retrieve data from widgets
				String name = newMovieNameET.getText().toString();
				float rating = newMovieRB.getRating();
				int year = Integer.parseInt(newMovieYearET.getText().toString()) ;

				SimpleDateFormat dfYear = new SimpleDateFormat("YYYY", Locale.US);
				int currentYear = Integer.parseInt(dfYear.format(new Date()));
				// Movies weren't around before 1900 and you can't watch a movie from the future
				if (year < 1900 || year > currentYear) {
					Toast.makeText(MovieActivity.this, "TimeTravel would be nice, " +
							"but please enter a correct year", Toast.LENGTH_LONG).show();
					return;
				}
				// Get the unix time to handle the date field in our Database
				long timestamp = System.currentTimeMillis() / 1000L;

				//TODO Add this movie to the database
				//TODO Update list
				if (dbManager.addMovie(name, rating, year, timestamp)) {
					newMovieNameET.getText().clear();
					newMovieRB.setRating(0);
					newMovieYearET.getText().clear();
				}
				cursorListAdapter.changeCursor(dbManager.getAllMovies());
			}
		});

	}

	public void notifyRatingChanged(int movieID, float rating) {

		//TODO Update DB, and then update the cursor for the ListView if necessary.
		dbManager.updateRating(movieID, rating);

		//Just to make sure the list and db are in sync
		//THis program works fine without this call, but only
		//because the changed the user makes to the list are the same
		//as the list should show.
		//
		//If your program changes data in the database
		//and your list needs to update, then you'll definitely
		// need to recreate the cursor for the list adapter
		cursorListAdapter.changeCursor(dbManager.getAllMovies());
	}


	//Don't forget these! Close and re-open DB as Activity pauses/resumes.

	@Override
	protected void onPause(){
		super.onPause();
		dbManager.close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		dbManager = new DatabaseManager(this);
	}
}
