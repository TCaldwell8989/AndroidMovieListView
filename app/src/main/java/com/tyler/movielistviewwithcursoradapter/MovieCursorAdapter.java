package com.tyler.movielistviewwithcursoradapter;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MovieCursorAdapter extends CursorAdapter {

	private static final String TAG = "MOVIE CURSOR ADAPTER";
	RatingChangedListener ratingChangedListener;

	//Correpond to column numbers in database
	private static final int ID_COL = 0;
	private static final int MOVIE_COL = 1;
	private static final int RATING_COL = 2;
	private static final int YEAR_COL = 3;
	private static final int DATE_COl = 4;

	public MovieCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);

		//Convert context to RatingChangedListener, just like adding a listener to a Fragment
		if (context instanceof RatingChangedListener) {
			this.ratingChangedListener = (RatingChangedListener) context;
		} else {
			throw new RuntimeException(context.toString() + " must implement RatingChangedListener");
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.movie_list_item, parent, false);
		return v;
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor) {

		//Get references to components that will contain data from database
        TextView nameTV = (TextView) view.findViewById(R.id.movie_title_list_text_view);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.movie_rating_list_rating_bar);
        TextView yearTV = (TextView) view.findViewById(R.id.movie_year_list_text_view);
        TextView dateTV = (TextView) view.findViewById(R.id.movie_review_date_text_view);

        //Cursor is set to the correct database now, that corresponds to this row of the list
        //Get data by reading the column needed
        nameTV.setText(cursor.getString(MOVIE_COL));
        ratingBar.setRating(cursor.getFloat(RATING_COL));
        yearTV.setText(String.valueOf(cursor.getInt(YEAR_COL)));

        Date date = new Date(cursor.getInt(DATE_COl)*1000L);
        String reviewDate = new SimpleDateFormat("MM-dd-YYYY", Locale.US).format(date);

        dateTV.setText(reviewDate);

        //Need this to update data - good idea to use a primary key
        final int movie_id = cursor.getInt(ID_COL);

		//TODO register listener for user changing RatingBar to change rating for this movie
		//TODO If user changes rating, notify the RatingChangedListener
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //This is called any time the rating is changed, including when the view is created
                //Don't need to update the database in this event.
                //So, check to see if the change was actually made by the user before requesting DB update
                if (fromUser) {
                    ratingChangedListener.notifyRatingChanged(movie_id, rating);
                }

            }
        });

	}


	interface RatingChangedListener {
		void notifyRatingChanged(int movieID, float newRating);
	}
}