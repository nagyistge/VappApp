package com.google.samples.quickstart.signin;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstapp.R;
import com.example.myfirstapp.retailerSignUpLogin;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import io.fabric.sdk.android.Fabric;

public class login_activity extends AppCompatActivity {
    CallbackManager callbackManager;
    private TwitterLoginButton loginButton2;
    PopupWindow mPopupWindow;
    View mLinearLayout;
    String lat ="";
    String lng ="";
    RelativeLayout loadingPanel;
    String g ="";
    String emailString ="";
    Boolean exists = null;
    StringBuilder sb = new StringBuilder();
Context mContext;
    String passw="";
    String streetName ="";
    String strtName ="";

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        getActionBar().hide();

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig("9u57HOuYQfeqenEjeNSGqSAtM",
                        "FPoyVBcbEVA6OJUSzOiMgOVfu1dQU0HOPn54MPL3Y4qPtzVDAm");

        Fabric.with(this, new TwitterCore(authConfig));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_login_activity);
//
        loadingPanel = (RelativeLayout)findViewById(R.id.loadingPanel);
         mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = inflater.inflate(R.layout.email_layout, null);
        final EditText email = (EditText) customView.findViewById(R.id.email);
        final TextView createAccountButton = (TextView) findViewById(R.id.createAccount);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED");
                mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                final View customView = inflater.inflate(R.layout.email_layout, null);


                final EditText email = (EditText) customView.findViewById(R.id.email);
                email.clearFocus();
                email.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                final EditText password = (EditText) customView.findViewById(R.id.password);
                password.clearFocus();
                password.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(password, InputMethodManager.SHOW_IMPLICIT);
                    }
                });


//                    EditText email= (EditText) findViewById(R.id.email);
//                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm2.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                // Set an elevation value for popup window
                // Call requires API level 21
                if (Build.VERSION.SDK_INT >= 21) {
                    mPopupWindow.setElevation(5.0f);
                }

                // Get a reference for the custom view close button
                Button closeButton = (Button) customView.findViewById(R.id.ib_close);


                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingPanel.setVisibility(View.VISIBLE);
                        mContext = getApplicationContext();

                        EditText email = (EditText) customView.findViewById(R.id.email);
                        EditText password1 = (EditText) customView.findViewById(R.id.password);
                        String password = password1.getText().toString();
                        // Create data variable for sent values to server
                        String userEmail = email.getText().toString();
                        System.out.println();


                        AWSCreateUser(userEmail,password);

                    }
                });
                mLinearLayout = (RelativeLayout) findViewById(R.id.login_activity);

                mPopupWindow.showAtLocation(mLinearLayout, Gravity.CENTER, 0, 0);
                mPopupWindow.update(1300, 1000);
                mPopupWindow.setFocusable(true);
                mPopupWindow.update();
            }
        });
        final EditText textPassword = (EditText) findViewById(R.id.textPassword);

        textPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingPanel.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //add your code here
                                    AWSlogin();
                                }
                            }, 1000);

                        }
                    });

                    return true;
                }
                else {
                    return false;
                }
            }



        });

        final EditText username = (EditText) findViewById(R.id.username);
        @ColorInt final int GREEN1 = 0xFF91a832;
        @ColorInt final int GREEN2 = 0xFF91a832;

        loginButton2 = (TwitterLoginButton) findViewById(R.id.twitter);
        loginButton2.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(login_activity.this, MapsActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        username.getBackground().setColorFilter(GREEN1, PorterDuff.Mode.SRC_OUT);


//        editText.setTextColor(0xffffff);
        final ImageView usernameCheckerImage = (ImageView)findViewById(R.id.usernameCheckerImage);
        final ImageView passwordCheckerImage = (ImageView)findViewById(R.id.passwordCheckerImage);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    username.getBackground().clearColorFilter();
                    textPassword.getBackground().clearColorFilter();
                    username.getBackground().setColorFilter(GREEN2, PorterDuff.Mode.SRC_OUT);
                    if(!username.getText().toString().equals("")){
                        usernameCheckerImage.setImageResource(R.drawable.tickorange);
                    }
                    else{
                        usernameCheckerImage.setImageResource(R.drawable.crossgreen);

                    }
                } else {
                    username.getBackground().clearColorFilter();
                    textPassword.getBackground().setColorFilter(GREEN2, PorterDuff.Mode.SRC_OUT);
                    System.out.println(username.getText().toString());

                    if(!username.getText().toString().equals("")){
                        usernameCheckerImage.setImageResource(R.drawable.tickorange);
                    }
                    else{
                        usernameCheckerImage.setImageResource(R.drawable.crossgreen);

                    }
                }
            }
        });

        textPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
                else{
                    System.out.println(textPassword.getText().toString());

                    if(!textPassword.getText().toString().equals("")){
                        passwordCheckerImage.setImageResource(R.drawable.tickorange);
                    }
                    else{
                        passwordCheckerImage.setImageResource(R.drawable.crossgreen);

                    }
                }}
        }
        );

                final LoginButton fb = (LoginButton) findViewById(R.id.fb);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        final Context context = this;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        fb.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("Success");
                        Intent intent = new Intent(login_activity.this, MapsActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println(exception);
                        System.out.println("onError");

                    }
                });


    }



    public void googleLogin(View v){
        Intent intent = new Intent(this, login_activity.class);
        startActivity(intent);
    }


    public void postRetailOffer(View view) {
        Intent intent = new Intent(login_activity.this, retailerSignUpLogin.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButton2.onActivityResult(requestCode, resultCode, data);

    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public void AWSCreateUser(String userEmail,String password) {

        loadingPanel.setVisibility(View.VISIBLE);

        System.out.println(userEmail);


        final Boolean j = isValidEmail(userEmail);

        if (j == true) {

            System.out.println(userEmail);
            String userPassword = password;
            System.out.println(password);
            String data = null;


            try {
                data = "";
                data += "{\"" + URLEncoder.encode("email", "UTF-8") + "\"" + ":"
                        + "\"" + userEmail + "\"";
                System.out.println("ha");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                data += "," + "\"" + URLEncoder.encode("password", "UTF-8") + "\""
                        + ":" + "\"" + URLEncoder.encode(userPassword, "UTF-8") + "\"" + "}";
                System.out.println("ho");
                System.out.println(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String text = "";
            BufferedReader reader = null;

            // Send data
            try {

                // Defined URL  where to send data
                URL url = new URL("https://9ex1ark3n8.execute-api.us-west-2.amazonaws.com/test/create-registered-user");

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                line = reader.readLine();
                // Append server response in string
                sb.append(line + "\n");
                String f = line;
                g = f.replace("\\", "").trim();


            } catch (Exception ex) {

            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
            System.out.println(g);
            System.out.println(g);

            Gson gson = new Gson();

            JsonParser jsonParser = new JsonParser();
            JsonElement my_json;


            try {
                my_json = jsonParser.parse(g);
                JSONObject obj = new JSONObject(g);
                emailString = obj.getString("email");
                exists = obj.getBoolean("exists");
                passw = obj.getString("password");


                System.out.println(emailString);
                System.out.println(exists);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (exists != false) {
                loadingPanel.setVisibility(View.GONE);


//                            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                            // Inflate the custom layout/view
//                            View customView = inflater.inflate(R.layout.email_layout, null);
                if(!passw.equals(userPassword)){
                    TextView validator = (TextView) findViewById(R.id.validator);
                    validator.setVisibility(View.VISIBLE);
                    System.out.println(passw);
                    System.out.println(userPassword);
                    mPopupWindow.dismiss();
                }
                else {
                    mPopupWindow.dismiss();

                    Toast.makeText(login_activity.this,"You are already registered, please login or use 'Forget Password",Toast.LENGTH_LONG).show();
                    mPopupWindow.dismiss();


//                    RelativeLayout rl4 = (RelativeLayout) findViewById(R.id.activity_retailer_post_offer);
//                    rl4.setVisibility(View.GONE);
//                    RelativeLayout rl5 = (RelativeLayout) findViewById(R.id.activity_retailer_post_offer2);
//                    rl5.setVisibility(View.VISIBLE);

//                            Intent intent = new Intent(retailerSignUpLogin.this, retailerPostOffer.class);
//                            startActivity(intent);

                }
            } else {
                Toast.makeText(login_activity.this,"Thanks for joining, please login as "+userEmail,Toast.LENGTH_LONG).show();
                loadingPanel.setVisibility(View.GONE);

                mPopupWindow.dismiss();


            }


        }
        else{
            Toast.makeText(login_activity.this,"Please enter a valid email address",Toast.LENGTH_LONG).show();
        }
    }

    public void AWSlogin() {

        loadingPanel.setVisibility(View.VISIBLE);
        mContext = getApplicationContext();
        final EditText email = (EditText) findViewById(R.id.username);

        final Boolean j = isValidEmail(email.getText().toString());

        if (j == true) {
            EditText password = (EditText) findViewById(R.id.textPassword);
            // Create data variable for sent values to server
            String userEmail = email.getText().toString();
            System.out.println(userEmail);
            String userPassword = password.getText().toString();
            System.out.println(password.getText().toString());
            String data = null;

            String retailerID2 ="thisISaLogin,RETID unknown ";

            try {
                data = "";
                data += "{\"" + URLEncoder.encode("email", "UTF-8") + "\"" + ":"
                        + "\"" + userEmail + "\"";
                System.out.println("ha");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                data += "," + "\"" + URLEncoder.encode("password", "UTF-8") + "\""
                        + ":" + "\"" + URLEncoder.encode(userPassword, "UTF-8") + "\"" ;
                System.out.println("ho");
                System.out.println(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                data += "," + "\"" + URLEncoder.encode("retailerID", "UTF-8") + "\""
                        + ":" + "\"" + URLEncoder.encode(retailerID2, "UTF-8") + "\"" + "}";
                System.out.println("ho");
                System.out.println(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String text = "";
            BufferedReader reader = null;

            // Send data
            try {

                // Defined URL  where to send data
                URL url = new URL("https://9ex1ark3n8.execute-api.us-west-2.amazonaws.com/test/standard-user-login");

                // Send POST data request

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                line = reader.readLine();
                // Append server response in string
                sb.append(line + "\n");
                String f = line;
                g = f.replace("\\", "").trim();


            } catch (Exception ex) {

            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
            System.out.println(g);
            System.out.println(g);

            Gson gson = new Gson();

            JsonParser jsonParser = new JsonParser();
            JsonElement my_json;


            try {
                my_json = jsonParser.parse(g);
                JSONObject obj = new JSONObject(g);
                emailString = obj.getString("email");
                exists = obj.getBoolean("exists");
                passw = obj.getString("password");
                try{
//                    String address = obj.getString("address");
//                    String urlAddress = address;
//                    String urlAddress2 = urlAddress.replace(" ", "+");
//                    System.out.println(urlAddress2);
//                    JSONObject json = readJsonFromUrl("http://maps.googleapis.com/maps/api/geocode/json?address=" + urlAddress2);
//                    System.out.println(json.toString());
//                    System.out.println(json.get("results"));
//                    JSONArray arr = json.getJSONArray("results");
//
//
//                    JSONObject startofAddrCmpo = arr.getJSONObject(0);
//
//                    JSONArray arr3 = startofAddrCmpo.getJSONArray("address_components");
//                                JSONObject arr2 = arr.getJSONObject(0);
//                    JSONObject firstLngNmeSet = arr3.getJSONObject(0);
//                    String by2 = firstLngNmeSet.toString();
//                    System.out.println(firstLngNmeSet);
//
//                    JSONObject streetNameSection = arr3.getJSONObject(1);
//                    strtName = streetNameSection.getString("long_name");
//                    streetName = streetNameSection.getString("long_name");
//                    System.out.println(strtName);
//                    System.out.println("Street name printed, line 656");
//
//                    JSONObject geometry = startofAddrCmpo.getJSONObject("geometry");
//                    JSONObject location = geometry.getJSONObject("location");
//                    lat = location.getString("lat");
//                    lng = location.getString("lng");


                }catch(Exception e){
                    e.printStackTrace();
                }

                System.out.println(emailString);
//                System.out.println(lat);
//                System.out.println(lng);
                System.out.println(exists);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (exists != false) {
                loadingPanel.setVisibility(View.GONE);


//                            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//
//                            // Inflate the custom layout/view
//                            View customView = inflater.inflate(R.layout.email_layout, null);
                if(!passw.equals(userPassword)){
                    TextView validator = (TextView) findViewById(R.id.validator);
                    validator.setVisibility(View.VISIBLE);
                    System.out.println(passw);
                    System.out.println(userPassword);
                }
                else {
                    TextView validator = (TextView) findViewById(R.id.validator);
                    validator.setVisibility(View.GONE);
                    Intent intent = new Intent(login_activity.this, MapsActivity.class);
                    startActivity(intent);
                    Toast.makeText(login_activity.this,"You are now logged in as "+email.getText().toString(),Toast.LENGTH_LONG).show();
                    email.setText("");

//                    RelativeLayout rl4 = (RelativeLayout) findViewById(R.id.activity_retailer_post_offer);
//                    rl4.setVisibility(View.GONE);
//                    RelativeLayout rl5 = (RelativeLayout) findViewById(R.id.activity_retailer_post_offer2);
//                    rl5.setVisibility(View.VISIBLE);

//                            Intent intent = new Intent(retailerSignUpLogin.this, retailerPostOffer.class);
//                            startActivity(intent);

                }
            } else {
                loadingPanel.setVisibility(View.GONE);

                TextView validator = (TextView) findViewById(R.id.validator);
                validator.setVisibility(View.VISIBLE);

            }


        }
        else{
            Toast.makeText(login_activity.this,"Please enter a valid email address",Toast.LENGTH_SHORT).show();
            loadingPanel.setVisibility(View.GONE);
            email.setText("");
        }
    }
}
