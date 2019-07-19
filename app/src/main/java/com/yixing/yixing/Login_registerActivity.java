package com.yixing.yixing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

public class Login_registerActivity extends AppCompatActivity  {
    private EditText Username;
    private EditText Password;
    private Button Login;
    private Button Register;
    private String user_name;
    private String password;
    private String Url = "http://192.168.43.92:80/yixing/user/login.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        Username = (EditText)findViewById(R.id.username);
        Password = (EditText)findViewById(R.id.password);
        Login = (Button)findViewById(R.id.login);
        Register = (Button)findViewById(R.id.register);

        Login.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i("~user", "开始登录");

                 if(Username.getText().toString().trim().equals("")){
                     Toast.makeText(Login_registerActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 else if(Password.getText().toString().trim().equals("")){
                     Toast.makeText(Login_registerActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 CreateNewProduct createNewProduct = new CreateNewProduct(Login_registerActivity.this);
                 createNewProduct.execute();
             }
         });

        Register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Login_registerActivity.this,RegisterActivity.class);
                startActivity(intent1);
                finish();
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
                user_name = Username.getText().toString();
                password = Password.getText().toString();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_name", user_name));
                params.add(new BasicNameValuePair("password", password));

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
                Toast.makeText(Login_registerActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Log.i("~user", "登录成功");

                //注册或登录成功就将用户的信息保存到本地数据库中
                SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit(); //SharedPreferences 本身不能读写数据，需要使用Editor
                editor.putString("name", user_name);
                editor.putString("password", password);
                editor.commit();

                Intent intent = new Intent(Login_registerActivity.this,MainActivity.class);
                startActivity(intent);
            }
            else if(result.trim().equals("fail")){
                Toast.makeText(Login_registerActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                Log.i("~user", "用户名或密码错误");
            }

            Log.i("~user", "结束登录");
        }
    }
}
