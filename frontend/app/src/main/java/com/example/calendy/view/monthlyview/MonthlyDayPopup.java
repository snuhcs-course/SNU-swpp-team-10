package com.example.calendy.view.monthlyview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendy.R;

import java.util.ArrayList;

public class MonthlyDayPopup extends Activity {

    TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setTheme(R.style.RoundCornerModalStyle);
        setContentView(R.layout.monthly_select_day_popup);

        //UI 객체생성
//        txtText = (TextView)findViewById(R.id.txtText);
        RecyclerView recyclerView = findViewById(R.id.monthlyDayPlanListRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager);  // LayoutManager 설정


        //데이터 가져오기
        Intent intent = getIntent();
        ArrayList<String> scheduleTitles = intent.getStringArrayListExtra("list");
//        txtText.setText(data);


        MonthlyDayPlanListAdaptor customAdapter = new MonthlyDayPlanListAdaptor(scheduleTitles);
        recyclerView.setAdapter(customAdapter); // 어댑터 설정
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
