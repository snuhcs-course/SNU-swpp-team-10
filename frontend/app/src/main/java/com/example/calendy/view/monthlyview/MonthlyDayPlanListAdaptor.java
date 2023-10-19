package com.example.calendy.view.monthlyview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendy.R;
import com.example.calendy.data.Schedule;

import java.util.ArrayList;

public class MonthlyDayPlanListAdaptor extends RecyclerView.Adapter<MonthlyDayPlanListAdaptor.ViewHolder>{

    private ArrayList<String> localDataSet;

    //===== 뷰홀더 클래스 =====================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
        public TextView getTextView() {
            return textView;
        }
    }
    //========================================================================

    //----- 생성자 --------------------------------------
    // 생성자를 통해서 데이터를 전달받도록 함
    public MonthlyDayPlanListAdaptor (ArrayList<String> dataSet) {
        localDataSet = dataSet;
    }
    //--------------------------------------------------

    @NonNull
    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public MonthlyDayPlanListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monthly_popup_plans_item, parent, false);
        MonthlyDayPlanListAdaptor.ViewHolder viewHolder = new MonthlyDayPlanListAdaptor.ViewHolder(view);

        return viewHolder;
    }

    @Override   // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull MonthlyDayPlanListAdaptor.ViewHolder holder, int position) {
        String title = localDataSet.get(position);
        holder.textView.setText(title);
    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return localDataSet.size();
    }
}