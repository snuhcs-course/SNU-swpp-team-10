package com.example.calendy.view.monthlyview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendy.R;
import com.example.calendy.data.maindb.plan.Plan;
import com.example.calendy.data.maindb.plan.Schedule;
import com.example.calendy.data.maindb.plan.Todo;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MonthlyDayPlanListPopup extends ComponentActivity {

    CalendarDay date;
    MonthlyDayPlanListViewModel model;
    List<Plan> planList;

    DayPlanListUiState uiState;
    RecyclerView recyclerView;
    TextView dateTextView;  // 날짜
    TextView weekdayTextView; // 요일


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setTheme(R.style.RoundCornerModalStyle);
        setContentView(R.layout.monthly_select_day_popup);

        //UI 객체생성
        recyclerView = findViewById(R.id.monthlyDayPlanListRecycler);
        dateTextView = findViewById(R.id.planListDayNumber);
        weekdayTextView = findViewById(R.id.planListDayText);

        //초기 데이터 가져오기
        // gets date data from previous activity
        Intent intent = getIntent();
        int yy = intent.getIntExtra("year", 2023);
        int mm = intent.getIntExtra("month", 0);
        int dd = intent.getIntExtra("day", 1);
        date = CalendarDay.from(yy, mm, dd);

        // fetch data view model
        model = new ViewModelProvider(this, new MonthlyDayPlanListViewModel.Factory(getApplication(), date)).get(MonthlyDayPlanListViewModel.class);

        planList = model.getPlans().getValue();

        //use dummy data
        //TODO: use real data
        ArrayList dummyDayPlans = new ArrayList<>();
        dummyDayPlans.add(new Schedule(1231, "test1", new Date(2023, 9, 11, 18, 00), new Date(2023, 9, 11, 20, 00), "memomemo", 1232, 1, 2, true, false));
        dummyDayPlans.add(new Schedule(1232, "test2", new Date(2023, 9, 11, 18, 00), new Date(2023, 9, 11, 20, 00), "memomemo", 1232, 1, 2, true, false));
        dummyDayPlans.add(new Schedule(1233, "test3", new Date(2023, 9, 11, 18, 00), new Date(2023, 9, 11, 20, 00), "memomemo", 1232, 1, 2, true, false));
        dummyDayPlans.add(new Todo(1234, "test4", new Date(2023, 9, 11, 18, 00), false, "memomemo", 1232, 1, 2, true, false));
        dummyDayPlans.add(new Todo(1235, "test5", new Date(2023, 9, 11, 18, 00), false, "memomemo", 1232, 1, 2, true, false));


        // list recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager);  // LayoutManager 설정
        MonthlyDayPlanListAdaptor dayPlanListAdaptor = new MonthlyDayPlanListAdaptor(dummyDayPlans);
        // set item onclick listener
        dayPlanListAdaptor.setOnItemClickListener(new MonthlyDayPlanListAdaptor.OnItemClickEventListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MonthlyDayPlanListPopup.this, MonthlyDayPlanDetailPopup.class);
//                intent.putExtra("planId",)
                startActivity(intent);
                Log.d("", "clicked item :" + position);
            }
        });
        recyclerView.setAdapter(dayPlanListAdaptor); // 어댑터 설정


        // set header texts
        // TODO: implement date.getWeekDay
        weekdayTextView.setText("?요일");
        dateTextView.setText(Integer.toString(date.getDay()));
    }

    public void closeActivity() {
        //데이터 전달하기
        Intent intent = new Intent();
//        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//        }
        closeActivity();
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
