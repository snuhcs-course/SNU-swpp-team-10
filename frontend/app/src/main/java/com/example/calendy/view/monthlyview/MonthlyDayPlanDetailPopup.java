package com.example.calendy.view.monthlyview;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.calendy.R;
import com.example.calendy.data.PlanType;
import com.example.calendy.data.maindb.plan.Plan;
import com.example.calendy.data.maindb.plan.Schedule;

import java.util.Date;

public class MonthlyDayPlanDetailPopup extends ComponentActivity {

    MonthlyDayPlanDetailViewModel model;
    Plan plan;

    DayPlanDetailUiState uiState;

    // UI elements
    TextView titleView;
    TextView startTimeView;
    TextView endTimeView;
    TextView memoView;
    TextView locationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setTheme(R.style.RoundCornerModalStyle);
        setContentView(R.layout.monthly_day_detail_popup);

        //UI 객체생성
        titleView = findViewById(R.id.monthlyPlanDetailTitle);
        startTimeView = findViewById(R.id.monthlyPlanDetailStartTime);
        endTimeView = findViewById(R.id.monthlyPlanDetailEndTime);
        memoView = findViewById(R.id.monthlyPlanDetailMemo);
        locationView = findViewById(R.id.monthlyPlanDetailLocation);

        //초기 데이터 가져오기
        // gets date data from previous activity
        Intent intent = getIntent();
        int planId = intent.getIntExtra("planId",0);
        com.example.calendy.data.maindb.plan.PlanType planType =
                intent.getIntExtra("planType", PlanType.SCHEDULE) == PlanType.SCHEDULE ?
                        com.example.calendy.data.maindb.plan.PlanType.SCHEDULE : com.example.calendy.data.maindb.plan.PlanType.TODO;

        // fetch data view model
        model = new ViewModelProvider(this, new MonthlyDayPlanDetailViewModel.Factory(getApplication(),planId,planType)).get(MonthlyDayPlanDetailViewModel.class);
//        plan=model.getPlan()
        //use dummy data
        //TODO: use real data
        Schedule dummySchedule = new Schedule(1231,"test1", new Date(2023,9,11,18,00),new Date(2023,9,11,20,00),"memomemo",1232,1,2,true,false);
        plan=dummySchedule;

        SetView();

    }

    public void closeActivity(){
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

    void SetView(){
        //temporary code
        titleView.setText(plan.getTitle());
        startTimeView.setText(((Schedule)plan).getStartTime().toString());
        endTimeView.setText(((Schedule)plan).getEndTime().toString());
        memoView.setText(plan.getMemo());
        //no location in plan scheme
    }
}
