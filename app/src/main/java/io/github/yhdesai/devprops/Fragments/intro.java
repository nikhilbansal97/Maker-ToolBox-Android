package io.github.yhdesai.devprops.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.github.yhdesai.devprops.DeveloperMessage;
import io.github.yhdesai.devprops.MessageAdapter;
import io.github.yhdesai.devprops.R;


    public class intro extends android.app.Fragment {
        private static final String TAG = "general";

        public static final String ANONYMOUS = "anonymous";
        public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
        public static final int RC_SIGN_IN = 1;

        private ListView mMessageListView;
        private MessageAdapter mMessageAdapter;
        private ProgressBar mProgressBar;

        private String mUsername;

        // Firebase instance variable
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mMessagesDatabaseReference;
        private ChildEventListener mChildEventListener;
        private FirebaseAuth mFirebaseAuth;
        private FirebaseAuth.AuthStateListener mAuthStateListener;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_general, container, false);

            //  mAdView = (AdView) rootView.findViewById(R.id.adView);
            //  AdRequest adRequest = new AdRequest.Builder().build();
            //    mAdView.loadAd(adRequest);
            FirebaseApp.initializeApp(getActivity());


            mUsername = ANONYMOUS;
            // Initialize Firebase components
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();

            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("intro");




            // Initialize references to views
            mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            mMessageListView = (ListView) rootView.findViewById(R.id.messageListView);
            // Initialize message ListView and its adapter

            List<DeveloperMessage> friendlyMessages = new ArrayList<>();
            mMessageAdapter = new MessageAdapter(getActivity(), R.layout.item_message, friendlyMessages);
            mMessageListView.setAdapter(mMessageAdapter);


            // Initialize progress bar
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);





            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        //User is signed in
                        onSignedInInitialize(user.getDisplayName());
                    } else {
                        // User is signed out
                        onSignedOutCleanup();
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(
                                                Arrays.asList(
                                                        //   new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                        //    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()


                                                ))
                                        .build(),
                                RC_SIGN_IN);


                    }

                }
            };
            return rootView;



        }

        private void onSignedInInitialize(String username) {
            mUsername = username;
            attachDatabaseReadListener();
        }


        private void onSignedOutCleanup(){
            mUsername = ANONYMOUS;
            mMessageAdapter.clear();
            detachDatabaseReadListener();

        }





        private void attachDatabaseReadListener() {

            if (mChildEventListener == null) {


                mChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        DeveloperMessage developerMessage = dataSnapshot.getValue(DeveloperMessage.class);
                        mMessageAdapter.add(developerMessage);
                    }

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onCancelled(DatabaseError databaseError) {
                    }
                };

                mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
            }
        }
        private void detachDatabaseReadListener(){
            if(mChildEventListener != null) {
                mMessagesDatabaseReference.removeEventListener(mChildEventListener);
                mChildEventListener = null;
            }
        }











        @Override
        public void onResume() {
            super.onResume();
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            if (mAuthStateListener != null) {
                mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            }
            detachDatabaseReadListener();
            mMessageAdapter.clear();
        }


    }