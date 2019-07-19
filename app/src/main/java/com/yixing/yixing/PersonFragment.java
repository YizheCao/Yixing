package com.yixing.yixing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class PersonFragment extends BaseFragment {
    private TextView Login_register;
    private TextView Help;
    private TextView Set;
    private TextView Logout;
    private LinearLayout linearLayout;
    private int flag;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        Login_register = (TextView)view.findViewById(R.id.login_register);
        Help = (TextView)view.findViewById(R.id.help);
        Set = (TextView)view.findViewById(R.id.set);
        Logout = (TextView)view.findViewById(R.id.logout);
        linearLayout = (LinearLayout)view.findViewById(R.id.linear_layout);
        flag = 0;

        preferences = this.getActivity().getSharedPreferences("user",MODE_PRIVATE);
        String save_name = preferences.getString("name",null);
        String save_password = preferences.getString("password",null);
        if(save_name == null && save_password == null){
            Login_register.setClickable(true);
            linearLayout.setVisibility(linearLayout.INVISIBLE);
        }
        else{
            Login_register.setText(save_name);
            Login_register.setClickable(false);
            linearLayout.setVisibility(linearLayout.VISIBLE);
            flag = 1;
        }

        Login_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(flag == 1){
                    Intent intent = new Intent(getActivity(),RegisterActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getActivity(),Login_registerActivity.class);
                    startActivity(intent);
                }
            }
        });
        Help.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HelpActivity.class);
                startActivity(intent);
            }
        });
        Set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SetActivity.class);
                startActivity(intent);
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除用户登录记录
                editor = preferences.edit();
                editor.clear();
                editor.commit();

                Login_register.setText("登录/注册");
                Login_register.setClickable(true);
                linearLayout.setVisibility(linearLayout.INVISIBLE);
                flag = 0;

                Log.i("~user", "退出登录");
            }
        });

        return view;
    }

    @Override
    protected void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void getDataFromServer() {

    }
}
