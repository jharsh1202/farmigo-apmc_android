package com.example.farmigosample;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.util.ULocale;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.farmigosample.MainActivity.APMCSelected;
import static com.example.farmigosample.MainActivity.APMCindex;
import static com.example.farmigosample.MainActivity.HindiState;
import static com.example.farmigosample.MainActivity.Stateindex;
import static com.example.farmigosample.MainActivity.dataSets;
import static com.example.farmigosample.MainActivity.lineChart;
import static com.example.farmigosample.MainActivity.lineData;
import static com.example.farmigosample.MainActivity.removeLineChart;
import static com.example.farmigosample.MainActivity.removeLineChartState;
import static com.example.farmigosample.MainActivity.setLineChartAPMC;
import static com.example.farmigosample.MainActivity.setLineChartState;

public class MyAdapter extends ArrayAdapter<StateVO> {
    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }

    private Context mContext;
    public ArrayList<StateVO> listAPMC;
    private MyAdapter myAdapter;
    String lang;
    private boolean isFromView = false;
    Resources res = getContext().getResources();

    public MyAdapter(Context context, int resource, List<StateVO> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listAPMC = (ArrayList<StateVO>) objects;
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

        holder.mTextView.setText(listAPMC.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listAPMC.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listAPMC.get(position).setSelected(isChecked);
                    if (holder.mCheckBox.isChecked()){

                        MainActivity.positionSelected=position; //for APMC Label in setLineChart()
                        lang=getContext().getResources().getConfiguration().getLocales().get(0).toString();
                        if(lang.equals("en_US") || lang.equals("en")) {
                                MainActivity.APMCSelected = listAPMC.get(position).getTitle();
                            }
                        else{
                            MainActivity.APMCSelected = HindiState.get(listAPMC.get(position).getTitle());
                        }

                        if (listAPMC.size()>5)
                           {
                               setLineChartAPMC();
                           }
                        else
                            setLineChartState();
                    }
                    else if(!holder.mCheckBox.isChecked() ){

                        if(lang.equals("en_US") || lang.equals("en"))
                            MainActivity.APMCSelected = listAPMC.get(position).getTitle();
                        else
                            MainActivity.APMCSelected = HindiState.get(listAPMC.get(position).getTitle());
                        if(listAPMC.size()>5){
                            removeLineChart(APMCSelected);
                            checkEmpty();
                        }
                        else{
                            removeLineChartState(APMCSelected);
                            checkEmpty();
                        }
                    }
                    /*//if Empty
                    boolean dataPresent=false;
                    if (APMCindex.size()>0 || Stateindex.size()>0)
                        dataPresent=true;
                    if (!dataPresent){
                        MainActivity.dataVals.clear();
                        dataSets.clear();
                        lineData = new LineData(dataSets);
                        MainActivity.lineChart.setData(lineData);
                        if(MainActivity.APMCindex.isEmpty() && MainActivity.Stateindex.isEmpty()){
                            //lineChart.clear();
                            lineChart.setNoDataText(res.getString(R.string.apmcnotselected));
                        }
                    }*/

                }
            }

        });
        return convertView;
    }
    void checkEmpty(){
        Log.i("APMCSize",APMCindex.size()+"a");
        if (APMCindex.isEmpty() && Stateindex.isEmpty()){
            lineChart.clear();
        }
    }


}