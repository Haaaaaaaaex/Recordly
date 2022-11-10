package com.example.recordly;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class sched_adapter extends RecyclerView.Adapter<sched_adapter.MyViewHolder>{

    private Context context;




    private ArrayList sched_id, subject, section, time, time_end;
    Activity activity;

    sched_adapter(Activity activity, Context context, ArrayList sched_id, ArrayList subject, ArrayList section,
                  ArrayList time, ArrayList time_end){
        this.activity = activity;
        this.context = context;
        this.sched_id = sched_id;
        this.subject = subject;
        this.section = section;
        this.time = time;
        this.time_end = time_end;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sched_adapter, parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.sched_id_txt.setText(String.valueOf(sched_id.get(position)));
        holder.subject_txt.setText(String.valueOf(subject.get(position)));
        holder.section_txt.setText(String.valueOf(section.get(position)));
        holder.time_txt.setText(String.valueOf(time.get(position)));
        holder.time_end_txt.setText(String.valueOf(time_end.get(position)));

        holder.schedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder submitAlert = new AlertDialog.Builder(context);
                submitAlert.setTitle("Select Operation");
                submitAlert.setMessage("Use this class for attendance or modify the schedule?");
                submitAlert.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, UpdateSchedule.class);
                        intent.putExtra("id",String.valueOf(sched_id.get(position)));
                        intent.putExtra("SUBJECT",String.valueOf(subject.get(position)));
                        intent.putExtra("SECTION",String.valueOf(section.get(position)));
                        intent.putExtra("START TIME",String.valueOf(time.get(position)));
                        intent.putExtra("TIME END",String.valueOf(time_end.get(position)));
                        activity.startActivityForResult(intent, 1);
                    }
                });
                submitAlert.setNeutralButton("Attendance", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, attendance.class);
                        intent.putExtra("SUBJECT",String.valueOf(subject.get(position)));
                        intent.putExtra("SECTION",String.valueOf(section.get(position)));
                        intent.putExtra("START TIME",String.valueOf(time.get(position)));
                        intent.putExtra("TIME END",String.valueOf(time_end.get(position)));
                        activity.startActivityForResult(intent, 1);

                    }
                });
                submitAlert.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return sched_id.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sched_id_txt, subject_txt, section_txt, time_txt, time_end_txt;
        LinearLayout schedLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sched_id_txt = itemView.findViewById(R.id.textView_ID);
            subject_txt = itemView.findViewById(R.id.textViewSubject);
            section_txt = itemView.findViewById(R.id.textViewSection);
            time_txt = itemView.findViewById(R.id.textViewTime);
            time_end_txt = itemView.findViewById(R.id.textViewTimeEnd);
            schedLayout = itemView.findViewById(R.id.schedLayout);

        }
    }


}