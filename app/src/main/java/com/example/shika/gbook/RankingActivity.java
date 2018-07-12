package com.example.shika.gbook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.communication.IOnItemFocusChangedListener;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {
    String getinfo = "http://gp.sendiancrm.com/offerall/Getusers.php";
    String getinfo2 = "http://gp.sendiancrm.com/offerall/Task1.php";

    Double percentage;
    RequestQueue requestQueue;
    TextView t1, t2, t3, t4, order;
    ArrayList<Integer> fpages, tpages,test,zadtid;
   ArrayList<Reader> reader;
    ArrayList<Integer> allperc;
    ArrayList<String> names,names2;
    int x=0;
    int sel;
    ArrayList<Integer> sum;
    int row1;
    int n;
    private static DecimalFormat df2 = new DecimalFormat(".####");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        t1 = (TextView) findViewById(R.id.name1);
        t2 = (TextView) findViewById(R.id.id1);
        t3 = (TextView) findViewById(R.id.name2);
        t4 = (TextView) findViewById(R.id.id2);
        order = (TextView) findViewById(R.id.ord);

        fpages = new ArrayList<>();
        tpages = new ArrayList<>();
        test = new ArrayList<>();
        allperc = new ArrayList<>();

        reader = new ArrayList<>();
        zadtid = new ArrayList<>();
        sum = new ArrayList<>();
names = new ArrayList<>();
        names2 = new ArrayList<>();

        Intent intent = getIntent();
        sel = intent.getIntExtra("selected", 0);


        requestQueue = Volley.newRequestQueue(RankingActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getinfo,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        //  Toast.makeText(RankingActivity.this, "successfully response", Toast.LENGTH_LONG).show();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray branches = jsonObject.getJSONArray("branchs");

                            for (int i = 0; i < branches.length(); i++) {
                                JSONObject branch = branches.getJSONObject(i);
                                //store users ids in arraylist
                                test.add(Integer.parseInt(branch.getString("user_id")));
                            }

                            for( n=1;n<test.size()+1;n++) {
                                getpercentage(new VolleyCallback() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onSuccess(String name,double result,double id) {
                                   //   t1.setText(Double.toString(result));
                                     reader.add(new Reader(name,result,id));
                                        Collections.sort( reader,new MySalaryComp());

                                        if(n>1){
                                            reader.add(new Reader(name,result,id));
                                            t3.setText( reader.get(1).getName());
                                            t2.setText(String.valueOf( reader.get(0).getId()));
                                        }
                                            names2.add(name);
                                        t1.setText( reader.get(0).getName());
                                        t4.setText(String.valueOf( reader.get(1).getId()));
                                    }

                                }, n);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RankingActivity.this, "Can't connect to internet", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        Volley.newRequestQueue(RankingActivity.this).add(stringRequest);

        if(sel==1){
            order.setText("user order : 2");
        }else if(sel==2){
            order.setText("user order : 1");
        }
    }

    private Double getpercentage(final VolleyCallback callback,final int id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getinfo2,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray branches = jsonObject.getJSONArray("branchs");
                            for (int i = 0; i < branches.length(); i++) {
                                JSONObject branch = branches.getJSONObject(i);
                                fpages.add(Integer.parseInt(branch.getString("fromPage")));
                                tpages.add(Integer.parseInt(branch.getString("toPage")));
                                if (i <1) {
                                    allperc.add(Integer.parseInt(branch.getString("user_id")));
                                    names.add(branch.getString("name"));

                                }

                            }
                            sum.add(tpages.get(0) - fpages.get(0));
                            for (int i = 1; i < fpages.size(); i++) {
                                if (isBetween(fpages.get(i - 1), tpages.get(i - 1), fpages.get(i)) && isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i))) {
                                    continue;
                                } else if (isBetween(fpages.get(i - 1), tpages.get(i - 1), fpages.get(i)) && tpages.get(i) > tpages.get(i - 1)) {
                                    sum.add(tpages.get(i) - tpages.get(i - 1));
                                } else if (isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i)) && fpages.get(i) < fpages.get(i - 1)) {
                                    sum.add(fpages.get(i - 1) - fpages.get(i));
                                } else if (!isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i)) && !isBetween(fpages.get(i - 1), tpages.get(i - 1), tpages.get(i))) {
                                    sum.add(fpages.get(i - 1) - fpages.get(i) + (tpages.get(i) - tpages.get(i - 1)));

                                }
                            }
                            percentage = (sum(sum) * 100) / 70;
                            String z = df2.format(percentage);
                            // pichart section ****************************************************
                            PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
                if(x==0) {
                     mPieChart.addPieSlice(new PieModel("Reading Percentage For June",percentage.intValue(), Color.parseColor("#8d589c")));
                }else if (x==1){
                     mPieChart.addPieSlice(new PieModel("Reading Percentage For Ahmed", percentage.intValue(), Color.parseColor("#FF4081")));

                    }
                    x++;
                            mPieChart.addPieSlice(new PieModel("All Pages", 70, Color.parseColor("#CDA67F")));
                            mPieChart.startAnimation();

                            mPieChart.setOnItemFocusChangedListener(new IOnItemFocusChangedListener() {
                                @Override
                                public void onItemFocusChanged(int _Position) {
                                }
                            });


                                callback.onSuccess(names.get(0),percentage,  allperc.get(0));

                            fpages.clear();
                            tpages.clear();
                            allperc.clear();
                            names.clear();
                            sum.clear();
                            row1 = 0;

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RankingActivity.this, "Can't connect to internet", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ID", Integer.toString(id));
                return params;
            }

        };

        Volley.newRequestQueue(RankingActivity.this).add(stringRequest);
        return percentage;

    }

    // this interface to return user name,percentage,id from onResponse function
    public interface VolleyCallback{
        void onSuccess(String name,double result,double id);
    }
//function to check if number is between two numbers or not
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


// reader model
    class Reader {

        private double ID;
        private String name;
        private double salary;

        public Reader(String n, double s,double id) {
            this.name = n;
            this.salary = s;
            this.ID=id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getSalary() {
            return salary;
        }

        public void setSalary(double id) {
            this.salary = id;
        }

        public double getId() {
            return ID;
        }

        public void setId(double id) {
            this.ID = id;
        }

    }

    //  function to sort users "decending "
    class MySalaryComp implements Comparator<Reader>{

        @Override
        public int compare(Reader e1, Reader e2) {
            if(e1.getSalary() < e2.getSalary()){
                return 1;
            } else {
                return -1;
            }
        }
    }
}


