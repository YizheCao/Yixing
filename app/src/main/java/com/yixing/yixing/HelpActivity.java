package com.yixing.yixing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    private EditText Help;
    private Button Submit;
    private String help;
    private String save_name;
    private String save_password;
    private String Url = "http://192.168.43.92:80/yixing/help/advice.php";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Help = (EditText)findViewById(R.id.editText);
        Submit = (Button)findViewById(R.id.submit);

        preferences = getSharedPreferences("user",MODE_PRIVATE);
        save_name = preferences.getString("name",null);
        save_password = preferences.getString("password",null);
        if(save_name == null && save_password == null){
            Toast.makeText(HelpActivity.this, "请先登录", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HelpActivity.this,Login_registerActivity.class);
            startActivity(intent);
            finish();
        }

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("~user", "开始提交");

                if(Help.getText().toString().trim().equals("")){
                    Toast.makeText(HelpActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                HelpActivity.CreateNewProduct createNewProduct = new HelpActivity.CreateNewProduct(HelpActivity.this);
                createNewProduct.execute();
            }
        });
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {
        private Context context;
        CreateNewProduct(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... args) {
            String result = null;
            try{
                help = Help.getText().toString();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_name", save_name));
                params.add(new BasicNameValuePair("advice", help));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Url);
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
                HttpEntity entity = new UrlEncodedFormEntity(params, "UTF_8");
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();
                result = EntityUtils.toString(responseEntity, "utf-8");

                Log.i("~user", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            if(result.trim().equals("success")){
                Toast.makeText(HelpActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                Log.i("~user", "提交成功");

                Intent intent = new Intent(HelpActivity.this,MainActivity.class);
                startActivity(intent);
            }
            else if(result.trim().equals("fail")){
                Toast.makeText(HelpActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                Log.i("~user", "提交失败");
            }

            Log.i("~user", "结束提交");
        }
    }
}
