package nanodegree.mal.udacity.android.childtracker.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import nanodegree.mal.udacity.android.childtracker.MainActivity;
import nanodegree.mal.udacity.android.childtracker.MyPreferences;
import nanodegree.mal.udacity.android.childtracker.R;

/**
 * Created by MOSTAFA on 02/11/2016.
 */

public class JoinParentFragment extends Fragment {

    private Button btn_join;
    private EditText etxt_parentId;
    private String parentId;
    private String userId;
    private String url;

    public JoinParentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userId = getActivity().getSharedPreferences(MyPreferences.MY_PREFERENCES, Context.MODE_PRIVATE).getString(MyPreferences.USER_ID,"0");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joinparent, container, false);
        btn_join = (Button)view.findViewById(R.id.btn_popup_join);
        etxt_parentId = (EditText)view.findViewById(R.id.etxt_popup_parentid);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateParentId()){
                    insertJoiningRecord();
                }
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean validateParentId() {
        parentId = etxt_parentId.getText().toString();
        if(TextUtils.isEmpty(parentId) ) {
            etxt_parentId.setError("Please Enter Your Parent ID");
            return false;
        }
        if (parentId.equals(userId)){ //the parent id can't equal userid
            etxt_parentId.setError("You can't join yourself");
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setCurrentFragment(MainActivity.JOIN_PARENT_FRAGMENT);
    }

    private void insertJoiningRecord() {
        url = "http://medicalapp.site88.net/ChildTracker/joinfollow.php";
        StringRequest registerUserRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responseParsing = new JSONObject(response);
                    String resultVar = responseParsing.getString("result");
                    if (resultVar.contains("Joined Successfully")){
                        Toast.makeText(getActivity(), "Joined Successfully", Toast.LENGTH_SHORT).show();

                        Fragment fragment = new MainFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.linearlayout_joinparent_parent, fragment,null)
                                .commit();

                    }else {
                        Toast.makeText(getActivity(), resultVar, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error in Json Handling", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap map  = new HashMap();
                map.put("parent_id",parentId);
                map.put("user_id",userId);
                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(registerUserRequest);
    }
}
