package com.yixing.yixing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StateFragment extends BaseFragment {
    private EditText e_number;
    private Button button_come;
    private Button button_search;
    private Button Poi_search;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_state, container, false);

        e_number = (EditText)view.findViewById(R.id.bus_number);
        button_come = (Button)view.findViewById(R.id.inquire);
        button_search = (Button)view.findViewById(R.id.search);
        Poi_search = (Button)view.findViewById(R.id.abc);

        button_come.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),message_result.class);
                startActivity(intent);
            }
        });
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(e_number.getText().toString().trim().equals("")){
                    Toast.makeText(getActivity(), "公交线路输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(),route_result.class);
                intent.putExtra("number",e_number.getText().toString());
                startActivity(intent);
            }
        });
        Poi_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),POI.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    protected void initView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_state;
    }

    @Override
    protected void getDataFromServer() {

    }
}
