package com.example.calendy.view.monthlyview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendy.R;
import com.example.calendy.data.plan.Plan;
import com.example.calendy.data.plan.Schedule;
import com.example.calendy.data.plan.Todo;

import java.util.ArrayList;

import static com.example.calendy.data.PlanType.SCHEDULE;
import static com.example.calendy.data.PlanType.TODO;

public class MonthlyDayPlanListAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // Item click event 를 위한 listener
    public interface OnItemClickEventListener {
        void onItemClick(View a_view, int a_position);
    }
    private OnItemClickEventListener mItemClickListener;
    private ArrayList<Plan> localDataSet;

    //===== 뷰홀더 클래스 =====================================================
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        public ScheduleViewHolder(@NonNull View itemView, OnItemClickEventListener itemClickListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.monthlyDayListScheduleTitleText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a_view) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(a_view, position);
                    }
                }
            });
        }
        public void setTitleView(String title){
            titleView.setText(title);
        }
    }
    public static class TodoViewHolder extends  RecyclerView.ViewHolder {
        private TextView titleView;
        private CheckBox checkBox;
        public TodoViewHolder(@NonNull View itemView,OnItemClickEventListener itemClickListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.monthlyDayListTodoTitleText);
            checkBox = itemView.findViewById(R.id.monthlyDayListTodocheckBox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a_view) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(a_view, position);
                    }
                }
            });
        }
        public void setTitleView(String title, Boolean complete){
            titleView.setText(title);
            checkBox.setChecked(complete);

        }
    }
    //========================================================================

    //----- 생성자 --------------------------------------
    // 생성자를 통해서 데이터를 전달받도록 함
    public MonthlyDayPlanListAdaptor (ArrayList<Plan> dataSet) {
        localDataSet = dataSet;
    }
    //--------------------------------------------------

    @NonNull
    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==SCHEDULE){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.monthly_select_list_item_schedule, parent, false);
            return new ScheduleViewHolder(view,mItemClickListener);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.monthly_select_list_item_todo, parent, false);
            return new TodoViewHolder(view,mItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Plan data=localDataSet.get(position);
        //TODO: change with getPlanType
        if(data.getClass()== Schedule.class){
            ((ScheduleViewHolder)holder).setTitleView(data.getTitle());
        }
        else{
            ((TodoViewHolder)holder).setTitleView(data.getTitle(),((Todo)data).getComplete());
        }

    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return localDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        //TODO: change with getPlanType
        return localDataSet.get(position).getClass()==Schedule.class ? SCHEDULE : TODO;
    }

    public void setLocalDataSet(ArrayList<Plan> localDataSet) {
        this.localDataSet = localDataSet;
    }

    //Todo: update todo data if checkbox state changed
    public void setOnItemClickListener(OnItemClickEventListener a_listener) {
        mItemClickListener = a_listener;
    }
}