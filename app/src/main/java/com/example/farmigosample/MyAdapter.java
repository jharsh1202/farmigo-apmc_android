package com.example.farmigosample;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;

import static com.example.farmigosample.MainActivity.APMCSelected;
import static com.example.farmigosample.MainActivity.APMCindex;
import static com.example.farmigosample.MainActivity.apmcs;
import static com.example.farmigosample.MainActivity.dataSets;
import static com.example.farmigosample.MainActivity.lineData;
import static com.example.farmigosample.MainActivity.removeLineChart;
import static com.example.farmigosample.MainActivity.setLineChart;

public class MyAdapter extends ArrayAdapter<StateVO> {

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }

    private Context mContext;
    public static ArrayList<StateVO> listState;
    private MyAdapter myAdapter;
    private boolean isFromView = false;
    Resources res = getContext().getResources();

    public MyAdapter(Context context, int resource, List<StateVO> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<StateVO>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.listviewcheckboxitems, null);
            holder = new ViewHolder();
            holder.mTextView = convertView.findViewById(R.id.text);
            holder.mCheckBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                    if (holder.mCheckBox.isChecked()==true ){
                        MainActivity.APMCSelected = apmcs[position];
                        MainActivity.positionSelected=position; //for APMC Label in setLineChart()
                        //MainActivity.APMCindex.get(MainActivity.APMCSelected);
                        setLineChart();
                    }
                    else if(holder.mCheckBox.isChecked()==false ){
                        //MainActivity.positionSelected=position;
                        MainActivity.APMCSelected = apmcs[position];
                            removeLineChart(APMCSelected);
                    }
                }

                boolean dataPresent=false;
                for (int i = 0; i < apmcs.length; i++) {
                       if (listState.get(i).isSelected()){
                            dataPresent=true;
                       }
                }
                if (!dataPresent){
                    APMCindex.clear();
                    MainActivity.dataVals.clear();
                    dataSets.clear();
                    lineData = new LineData(dataSets);
                    MainActivity.lineChart.setData(lineData);
                    MainActivity.lineChart.clear();
                    MainActivity.lineChart.setNoDataText(res.getString(R.string.apmcnotselected));
                }


            }
        });
        return convertView;
    }


}