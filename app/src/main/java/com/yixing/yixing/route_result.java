package com.yixing.yixing;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class route_result extends AppCompatActivity {
    private TextView busNumber;
    private TextView arrivalTime;
    private TextView personNumber;
    private TextView ticket;
    private String bus_number = "";
    private String Url = "http://192.168.43.92:80/yixing/bus/route.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_result);

        busNumber = (TextView)findViewById(R.id.BusNumber);
        arrivalTime = (TextView)findViewById(R.id.ArrivalTime);
        personNumber = (TextView)findViewById(R.id.PersonNumber);
        ticket = (TextView)findViewById(R.id.Ticket);
        bus_number += getIntent().getStringExtra("number");

        CreateNewProduct createNewProduct = new route_result.CreateNewProduct(route_result.this);
        createNewProduct.execute();
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {
        private Context context;
        CreateNewProduct(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... args) {
            String result = null;
            //ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("bus_number", bus_number));
            try{
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("bus_number", bus_number));
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Url);
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF_8");
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();
                result = EntityUtils.toString(responseEntity, "utf-8");

                Log.i("~route", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try
            {
                JSONObject jsonObject1 = new JSONObject(result);
                JSONArray jsonArray = jsonObject1.getJSONArray("route");
                busNumber.setText(bus_number + "路");
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
                    arrivalTime.setText("到站时间：" + jsonObject2.getString("arrival_time"));
                    personNumber.setText("车上人数：" + jsonObject2.getString("person_total") + "人");
                    ticket.setText("票价：" + jsonObject2.getString("ticket"));
                }
            }
            catch (JSONException e) {
               e.printStackTrace();
            }

            Log.i("~route", "结束查询");
        }
    }
}
