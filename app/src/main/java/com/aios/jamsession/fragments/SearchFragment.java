package com.aios.jamsession.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.SearchActivity;
import com.aios.jamsession.adapters.PostsAdapter;
import com.aios.jamsession.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchFragment extends Fragment {

  View mView;
  CardView mCardViewJazz;
  CardView mCardViewBlues;
  CardView mCardViewRock;
  CardView mCardViewRap;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        mCardViewJazz = mView.findViewById(R.id.cardViewJazz);
        mCardViewBlues = mView.findViewById(R.id.cardViewBlues);
        mCardViewRock = mView.findViewById(R.id.cardViewRock);
        mCardViewRap = mView.findViewById(R.id.cardViewRap);

        mCardViewJazz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchActivity("JAZZ");
            }
        });

        mCardViewBlues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchActivity("BLUES");
            }
        });

        mCardViewRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchActivity("ROCK");
            }
        });

        mCardViewRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchActivity("RAP");
            }
        });

        return mView;
    }

    private void goToSearchActivity(String genre){
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("genre", genre);
        startActivity(intent);
    }

}

