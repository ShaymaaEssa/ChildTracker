package nanodegree.mal.udacity.android.childtracker;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //params for Post method in volley
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    EditText etxt_email;
    EditText etxt_password;
    Button btn_login;

    String url;
    String userId;
    String userName;
    String userEmail;

    boolean userLogged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<<  to hide action bar
        setContentView(R.layout.activity_login);

        initControls();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etxt_email.getText().toString();
                String Password = etxt_password.getText().toString();
                if (username.isEmpty() || Password.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "You Must Enter Your Email And Password", Toast.LENGTH_LONG).show();
                } else {
                    checkLogin();

                }


            }
        });

    }

//    private void registerComponents() {
//        this.registerReceiver(new NetworkChangeReceiver(),new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
//    }

    private void checkLogin() {
        //check login info with the info in database
        url = "http://medicalapp.site88.net/ChildTracker/login.php";
        StringRequest checkLoginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("userinfo")){
                    try{
                        JSONObject userInfo = new JSONObject(response);
                        JSONArray userArray = userInfo.getJSONArray("userinfo");

                        JSONObject currentObject = userArray.getJSONObject(0);
                        userId = currentObject.getString("user_id");
                        userName = currentObject.getString("user_name");
                        userEmail = currentObject.getString("email");

                        MyPreferences.setUserInfo(userId,userName,userEmail);
                        userLogged = true;
                        Toast.makeText(LoginActivity.this,"Welcome "+userName+"!",Toast.LENGTH_SHORT).show();

                        MyPreferences.setFirst(false);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    }catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this,"Error in handling Json Data",Toast.LENGTH_SHORT).show();
                    }



                }else{
                    Toast.makeText(LoginActivity.this,response,Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap params = new HashMap();
                params.put(KEY_EMAIL, etxt_email.getText().toString());
                params.put(KEY_PASSWORD, etxt_password.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(checkLoginRequest);
    }



    private void initControls() {
        etxt_email = (EditText)findViewById(R.id.etxt_login_mail);
        etxt_password = (EditText)findViewById(R.id.etxt_login_password);
        btn_login = (Button)findViewById(R.id.btn_login_login);
    }
}
