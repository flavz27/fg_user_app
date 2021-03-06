package com.example.flavia.fg_user_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private final String url = "https://api.festigeek.ch/v1";
    private String token = null;


    private Unbinder unbinder;
    private OnFragmentInteractionListener mListener;
    @BindView(R.id.greeting)
    protected AppCompatTextView greeting_field;
    @BindView(R.id.mail)
    protected AppCompatTextView mail_field;
    @BindView(R.id.qrcode)
    protected AppCompatImageView qrcode_field;
    @BindView(R.id.username)
    protected AppCompatTextView username_field;
    @BindView(R.id.birthdate)
    protected AppCompatTextView birthdate_field;
    @BindView(R.id.steam_id)
    protected AppCompatTextView steamid_field;
    @BindView(R.id.battle_tag)
    protected AppCompatTextView battle_tag_field;
    @BindView(R.id.lolTag)
    protected AppCompatTextView lol_tag_field;
    //data
    RequestQueue queue = null;

    public AccountFragment() {
        // Required empty public constructor
    }


    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        token = ((MainActivity) getActivity()).getToken();

    }

    public void attemptGetUserInfo() {

        Map<String, String> jsonParams = new HashMap<>();
        //insert the params

        //creating the request and the promise
        // gives verb, url,
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url + "/users/me", new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("/me RESPONSE", response.toString());
                        populateFields(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {  // needs headers for the request, override header function of this object (not the general class)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(jsObjRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, view);

        ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle(R.string.account_title);

        queue = Volley.newRequestQueue(getContext());
        attemptGetUserInfo();

        return view;
    }


    private void populateFields(JSONObject data){
        try{
            // set greeting depending of time of day
            Calendar instance = Calendar.getInstance();
            int hour = instance.get(Calendar.HOUR_OF_DAY);

            String greetingsText;
            if(hour>17 || hour < 3){
                greetingsText = "Bonsoir ";
            } else {
                greetingsText = "Bonjour ";
            }
            greeting_field.setText(greetingsText + data.getString("firstname") + " !");
            mail_field.setText(data.getString("email"));
            username_field.setText(data.getString("username"));


            birthdate_field.setText(data.getString("birthdate"));
            String encodedImage = data.getString("QRCode");
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            qrcode_field.setImageBitmap(decodedByte);
            if(checkIfNull(data.getString("steamID64"))){
                steamid_field.setText(data.getString("steamID64"));
            }
            if(checkIfNull(data.getString("battleTag"))){
                battle_tag_field.setText(data.getString("battleTag"));
            }
            if(checkIfNull(data.getString("lol_account"))){
                lol_tag_field.setText(data.getString("lol_account"));
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public boolean checkIfNull(String string){
        return (!string.equals("null"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
