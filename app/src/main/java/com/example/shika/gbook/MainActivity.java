package com.example.shika.gbook;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.widget.EditText;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    int row1;
    Double percentage,var;
    Button InsertButton;
    String [] s_name;
    Integer [] s_gender;
    RequestQueue requestQueue;
    Spinner spinner;
    String URL="http://gp.sendiancrm.com/offerall/Tas1_users.php";
    ArrayList<String> UserName;
    ArrayList<Integer> sum;
    private static DecimalFormat df2 = new DecimalFormat(".####");

    String getinfo = "http://gp.sendiancrm.com/offerall/Task1.php";
    int trick;
    TextView po,tit;
    Button bo;
    ArrayList<Integer> fpages ,tpages;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

po = (TextView) findViewById(R.id.point);
        tit = (TextView) findViewById(R.id.tit);
bo = (Button)findViewById(R.id.addpoin);

        fpages = new ArrayList<>();
        tpages = new ArrayList<>();
        sum = new ArrayList<>();

        InsertButton = (Button) findViewById(R.id.addpoin);

        UserName=new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.country_Name);
        loadSpinnerData(URL);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pos=  spinner.getSelectedItemPosition();
                trick =s_gender[pos];

                requestQueue = Volley.newRequestQueue(MainActivity.this);
             //   progressDialog = new ProgressDialog(MainActivity.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, getinfo,
                        new Response.Listener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(String response) {
                                try {

                                    JSONObject jsonObject = new JSONObject(response);

                                    JSONArray branches = jsonObject.getJSONArray("branchs");
                                    for(int i=0 ; i<branches.length();i++)
                                    {
                                        JSONObject branch = branches.getJSONObject(i);
                                        fpages.add(Integer.parseInt(branch.getString("fromPage")));
                                        tpages.add(Integer.parseInt(branch.getString("toPage")));
                                    }


                   sum.add(tpages.get(0)-fpages.get(0));
                        for (int i=1;i<fpages.size();i++){
                             if(isBetween(fpages.get(i - 1), tpages.get(i - 1), fpages.get(i)) && isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i)) ){
                                 continue;
                             }else if (isBetween(fpages.get(i - 1), tpages.get(i - 1), fpages.get(i)) && tpages.get(i)>tpages.get(i-1)){
                                sum.add( tpages.get(i)-tpages.get(i-1));
                             }else if (isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i)) && fpages.get(i)<fpages.get(i-1)){
                                 sum.add(fpages.get(i-1)-fpages.get(i));
                             }else if (!isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i)) && !isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i))){
                                 sum.add(fpages.get(i-1)-fpages.get(i)+(tpages.get(i)-tpages.get(i-1)));

                             }
                        }
                                    percentage = (sum(sum)*100)/70;  // formula to calcuate percentage
                                    String z= df2.format(percentage);

                                    po.setText(String.valueOf(z));
                                    fpages.clear();
                                    tpages.clear();
                                    sum.clear();
                                    row1=0;


                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Can't connect to internet", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params=new HashMap<String, String>();
                        params.put("ID", Integer.toString(trick));
                        return params;
                    }

                };
                Volley.newRequestQueue(MainActivity.this).add(stringRequest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });

        bo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent ino = new Intent(MainActivity.this,RankingActivity.class);
                //send id of the selected user
                ino.putExtra("selected", s_gender[spinner.getSelectedItemPosition()]);
                startActivity(ino);}
        });
            }




// this function to display all users into the spinner
    private void loadSpinnerData(String url) {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);

                    JSONArray jsonArray=jsonObject.getJSONArray("users");
                    s_name   = new String[jsonArray.length()];
                    s_gender = new Integer[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        s_name[i]=jsonObject1.getString("name");  // holds users names
                        s_gender[i] = jsonObject1.getInt("person_id"); // holds users ids
                    }

                    for(int i = 0; i<s_name.length; i++)
                    {
                        UserName.add(s_name[i]+" : "+s_gender[i]);
                    }
                    spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, UserName));
                }catch (JSONException e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }



    public static boolean isBetween(int a, int b, int c) {
        return b > a ? c > a && c < b : c > b && c < a;
    }

    public static double sum(List<Integer> list) {
        double sum = 0;
        for (int i: list) {
            sum += i;
        }
        return sum;
    }
        }
