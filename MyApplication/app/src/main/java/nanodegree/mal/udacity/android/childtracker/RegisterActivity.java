package nanodegree.mal.udacity.android.childtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etxt_name;
    EditText etxt_mail;
    EditText etxt_password;
    Button btn_register;

    String url;
    boolean accountCreated = false;

    String userId;
    String validateName ;
    String validateMail ;
    String validatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<<  to hide action bar
        setContentView(R.layout.activity_register);

        initControls();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateParameters()){
                    //insert the data in the DataBase
                    registerUserInDB();


                }
            }
        });



    }

    private void registerUserInDB() {
        url = "http://medicalapp.site88.net/ChildTracker/register.php";
        StringRequest registerUserRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseParsing = new JSONObject(response);
                    String resultVar = responseParsing.getString("result");
                    if (resultVar.contains("created")){
                        userId = responseParsing.getString("user_id");
                        Toast.makeText(RegisterActivity.this, "Account Created,"+userId, Toast.LENGTH_SHORT).show();
                        accountCreated = true;

                        setPreferencesSetting();

                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);

                    }else {
                        Toast.makeText(RegisterActivity.this, resultVar, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, "Error in Json Handling", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap map  = new HashMap();
                map.put("name",validateName);
                map.put("email",validateMail);
                map.put("password",validatePassword);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(registerUserRequest);
    }

    private void setPreferencesSetting() {
        //the account created successfully so next time we don't need to open register screen
        MyPreferences.setFirst(false);

        //set the user info in the shared preferences
        MyPreferences.setUserInfo(userId,validateName,validateMail);
    }


    private void initControls() {
        etxt_name = (EditText)findViewById(R.id.etxt_Register_name);
        etxt_mail = (EditText)findViewById(R.id.etxt_Register_mail);
        etxt_password = (EditText)findViewById(R.id.etxt_Register_password);
        btn_register = (Button)findViewById(R.id.btn_register_register);
    }

    private boolean validateParameters() {
        validateName = etxt_name.getText().toString();
        validateMail = etxt_mail.getText().toString();
        validatePassword = etxt_password.getText().toString();

        if(TextUtils.isEmpty(validateName) || validateName.length() < 4) {
            etxt_name.setError("Please Enter Your Full Name");
            return false;
        }

        if(TextUtils.isEmpty(validateMail) || !validateMail.contains("@") || !validateMail.contains(".")||!android.util.Patterns.EMAIL_ADDRESS.matcher(validateMail).matches()) {
            etxt_mail.setError("Enter a valid email");
            return false;
        }

        if(TextUtils.isEmpty(validatePassword) || validatePassword.length() < 3) {
            etxt_password.setError("Please Enter Your Full Name");
            return false;
        }

        return true;
    }
}
