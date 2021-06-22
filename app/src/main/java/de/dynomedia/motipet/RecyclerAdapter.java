package de.dynomedia.motipet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<FitnessValue> myValueList;

    /**
     * An Object of this class represents a recycler adapter for the given list.
     * @param currentValueList
     */
    public RecyclerAdapter(ArrayList<FitnessValue> currentValueList) {
        this.myValueList = currentValueList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_day;
        private TextView tv_steps;
        private TextView tv_distance;
        private TextView tv_calories;

        public MyViewHolder (final View view) {
            super(view);
            tv_day = view.findViewById(R.id.tv_moti_day);
            tv_steps = view.findViewById(R.id.tv_eval_steps);
            tv_distance = view.findViewById(R.id.tv_eval_distance);
            tv_calories = view.findViewById(R.id.tv_eval_calories);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       String day = myValueList.get(position).getDay();
       holder.tv_day.setText(day);

        String steps = myValueList.get(position).getSteps();
        holder.tv_steps.setText(steps);

        String distance = myValueList.get(position).getDistance();
        holder.tv_distance.setText(distance);

        String calories = myValueList.get(position).getCalories();
        holder.tv_calories.setText(calories);
    }

    @Override
    public int getItemCount() {
        return myValueList.size();
    }


}
