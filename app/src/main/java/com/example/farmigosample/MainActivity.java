package com.example.farmigosample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    static LineDataSet lineDataSetState1;
    static LineDataSet lineDataSetState2;
    static HashMap<String, LineDataSet> APMCindex = new HashMap<>();
    static HashMap<String, LineDataSet> Stateindex = new HashMap<>();
    static HashMap<String, String> HindiState = new HashMap<>();
    static LineChart lineChart;
    static Cursor dataOnionPrice;
    static SQLiteDatabase sqLiteDatabase;
    public static String APMCSelected;
    public static int positionSelected;
    static ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    static LineData lineData;
    static TypedArray ta;
    static int[] colors;
    static ArrayList<Entry> dataVals;
    Spinner spinnerAPMC,spinnerState;
    ArrayList<StateVO> listState,listAPMC;
    RadioButton radioButtonAPMC,getRadioButtonState;

    static String[] apmcmarkets,states;
    static String[] apmcs={"Select_APMC","Mumbai","Pune","Nagpur","Bhopal","Indore","Bangalore",
            "Belagavi","Mysore","Ahmedabad", "Surat"};
    static String[] Maharashtra={"Nagpur","Pune","Mumbai"};
    static String[] MadhyaPradesh={"Bhopal","Indore"};
    static String[] Karnataka={"Mysore","Belagavi","Bangalore"};
    static String[] Gujarat={"Surat","Ahmedabad"};





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hindi Support
        hindiSupport();
        //APMC Spinner Data
        setAPMCSpinner();
        //State Spinner Data
        setStateSpinner();

        //radio
        setRadioButtons();

        //for LineChart customization
        configureLineChart();

        //Adding Onion Price data to local database
        try {
            sqLiteDatabase = this.openOrCreateDatabase("OnionPricesDb", 0, null);
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019", null);
        } catch (Exception e) {
            databaseSetup();
            sqLiteDatabase = this.openOrCreateDatabase("OnionPricesDb", 0, null);
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019", null);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < apmcs.length; i++) {
            listAPMC.get(i).setSelected(false);
            removeLineChart(apmcs[i]);
        }
        for (int i = 0; i < states.length; i++) {
            listState.get(i).setSelected(false);
            removeLineChart(states[i]);
        }

        APMCindex.clear();
        dataVals.clear();
        dataSets.clear();
        lineData = new LineData(dataSets);
        MainActivity.lineChart.setData(lineData);

        MainActivity.lineChart.clear();
        MainActivity.lineChart.setNoDataText(getResources().getString(R.string.apmcnotselected));
    }

    static void setLineChartAPMC() {
        lineDataSetState1 = new LineDataSet(APMC(), apmcmarkets[positionSelected]);
        APMCindex.put(APMCSelected, lineDataSetState1);
        lineDataSetState1.setLineWidth(2);
        for (int i = 0; i < dataSets.size(); i++) {
            colors[i] = ta.getColor(i, 0);
            lineDataSetState1.setColor(colors[i]);
        }
        dataSets.add(lineDataSetState1);
        lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    void setRadioButtons(){
        radioButtonAPMC=findViewById(R.id.radioButtonAPMC);
        getRadioButtonState=findViewById(R.id.radioButtonState);
        spinnerAPMC.setEnabled(false);
        spinnerState.setEnabled(false);

        radioButtonAPMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerAPMC.setEnabled(true);
                spinnerState.setEnabled(false);
            }
        });
        getRadioButtonState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerState.setEnabled(true);
                spinnerAPMC.setEnabled(false);
            }
        });
    }

    static String[] setState(String statename){
        switch (statename){
            case "Maharashtra":return Maharashtra;
            case "Gujarat":return Gujarat;
            case "Karnataka":return Karnataka;
            case "Madhya Pradesh":return MadhyaPradesh;
            default:return Maharashtra;
        }
    }

    static void setLineChartState() {
        int count=0;
        String State=APMCSelected;

        for (String M:setState(APMCSelected)){
            if(count==1){
                lineDataSetState2 = new LineDataSet(APMC(), State);
                lineChart.getLegend().setEnabled(true);
                count++;
            }
            APMCSelected=M;
            lineDataSetState2 = new LineDataSet(APMC(), "");
            lineChart.getLegend().setEnabled(true);
            Stateindex.put(M, lineDataSetState2);
            lineDataSetState2.setLineWidth(2);
            int i=positionSelected;
            colors[i] = ta.getColor(i, 0);
            lineDataSetState2.setColor(colors[i]);
            dataSets.add(lineDataSetState2);
        }

        lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    void hindiSupport(){
        HindiState.put("मुंबई","Mumbai");
        HindiState.put("नागपुर","Nagpur");
        HindiState.put("पुणे","Pune");
        HindiState.put("बेलगावी","Belagavi");
        HindiState.put("सूरत","Surat");
        HindiState.put("अहमदाबाद","Ahmedabad");
        HindiState.put("भोपाल","Bhopal");
        HindiState.put("इंदौर","Indore");
        HindiState.put("मैसूर","Mysore");
        HindiState.put("बैंगलोर","Bangalore");
        HindiState.put("महाराष्ट्र","Maharashtra");
        HindiState.put("कर्नाटक","Karnataka");
        HindiState.put("गुजरात","Gujarat");
        HindiState.put("मध्यप्रदेश","Madhya Pradesh");

    }

    public static void removeLineChart(String a) {
        try {
            if (!dataSets.isEmpty())
            {
                dataSets.remove(APMCindex.get(a));
                APMCindex.remove(APMCindex.get(a));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lineChart.clear();
        lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public static void removeLineChartState(String state) {
        try {
            if (!dataSets.isEmpty())
            {
                Stateindex.remove(state);
                for (String APMC:setState(state)) {
                    dataSets.remove(Stateindex.get(APMC));
                    APMCindex.remove(Stateindex.get(APMC));
                }
                Stateindex.remove(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lineChart.clear();
        lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    static protected ArrayList<Entry> APMC() {

        dataVals = new ArrayList<>();

        if (APMCSelected != null)
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019 where apmc = " + '"' + APMCSelected + '"', null);
        else
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019", null);

        int monthIndex = dataOnionPrice.getColumnIndex("month");
        int priceIndex = dataOnionPrice.getColumnIndex("price");

        dataOnionPrice.moveToFirst();

        while (!dataOnionPrice.isAfterLast()) {
            dataVals.add(new Entry(monthToNumerical(dataOnionPrice.getString(monthIndex)), dataOnionPrice.getInt(priceIndex)));
            dataOnionPrice.moveToNext();
        }
        return dataVals;
    }


    void setAPMCSpinner() {
        apmcmarkets = new String[]{getResources().getString(R.string.select_APMC),
                getResources().getString(R.string.mumbai),getResources().getString(R.string.pune),
                getResources().getString(R.string.nagpur),getResources().getString(R.string.bhopal),
                getResources().getString(R.string.indore),getResources().getString(R.string.bangalore),
                getResources().getString(R.string.belagavi),getResources().getString(R.string.mysore),
                getResources().getString(R.string.ahmedabad),getResources().getString(R.string.surat)};

        spinnerAPMC = findViewById(R.id.spinnerAPMC);
        listAPMC = new ArrayList<>();
        ta = this.getResources().obtainTypedArray(R.array.colors);
        colors = new int[ta.length()];
        for (int i = 0; i < apmcmarkets.length; i++) {
            StateVO stateAPMC = new StateVO();
            stateAPMC.setTitle(apmcmarkets[i]);
            stateAPMC.setSelected(false);
            listAPMC.add(stateAPMC);
        }
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, 0, listAPMC);
        spinnerAPMC.setAdapter(myAdapter);
    }

    void setStateSpinner() {
        states = new String[]{getResources().getString(R.string.select_State),
                getResources().getString(R.string.maharashtra),getResources().getString(R.string.madhyapradesh),
                getResources().getString(R.string.karnataka),getResources().getString(R.string.gujrat)};

        spinnerState = findViewById(R.id.spinnerState);
        listState = new ArrayList<>();
        ta = this.getResources().obtainTypedArray(R.array.colors);
        colors = new int[ta.length()];
        for (int i = 0; i < states.length; i++) {
            StateVO state = new StateVO();
            state.setTitle(states[i]);
            state.setSelected(false);
            listState.add(state);
        }
        MyAdapter stateAdapter=new MyAdapter(MainActivity.this,0,listState);
        spinnerState.setAdapter(stateAdapter);
    }

    private void configureLineChart() {
        lineChart = findViewById(R.id.graphss);
        lineChart.setNoDataText(getResources().getString(R.string.apmcnotselected));
        Description desc = new Description();
        desc.setText(getResources().getString(R.string.linechertdesc));

        desc.setTextSize(16);
        lineChart.setDescription(desc);
        lineChart.isDrawBordersEnabled();
        lineChart.setBorderWidth(1);
        lineChart.setDrawBorders(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setLabelCount(11);
        lineChart.getAxisRight().setEnabled(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] month={getResources().getString(R.string.jan),getResources().getString(R.string.feb),
                getResources().getString(R.string.mar),getResources().getString(R.string.apr),
                getResources().getString(R.string.may),getResources().getString(R.string.jun),
                getResources().getString(R.string.jul),getResources().getString(R.string.aug),
                getResources().getString(R.string.sep),getResources().getString(R.string.oct),
                getResources().getString(R.string.nov),getResources().getString(R.string.dec)};

        //Setting labels in x-Axis
        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(month));


        for (int i = 0; i < dataSets.size(); i++) {
            dataSets.get(i).getColor(1);
        }
    }

    void databaseSetup() {
        try {

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("OnionPricesDb", 0, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS onionprice2019 ( id INT(5)," +
                    " state Varchar(20), apmc Varchar(20),month varchar(5),price int(8))");

            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (1, 'Maharashtra', 'Mumbai', 'Jan', 663)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (2, 'Maharashtra', 'Mumbai', 'Feb', 552)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (3, 'Maharashtra', 'Mumbai', 'Mar', 636)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (4, 'Maharashtra', 'Mumbai', 'Apr', 842)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (5, 'Maharashtra', 'Mumbai', 'May', 972)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (6, 'Maharashtra', 'Mumbai', 'Jun', 1369)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (7, 'Maharashtra', 'Mumbai', 'Jul', 1389)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (8, 'Maharashtra', 'Mumbai', 'Aug', 1736)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (9, 'Maharashtra', 'Mumbai', 'Sep', 3050)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (10, 'Maharashtra', 'Mumbai', 'Oct', 3271)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (11, 'Maharashtra', 'Mumbai', 'Nov', 5332)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (12, 'Maharashtra', 'Mumbai', 'Dec', 7500)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (13, 'Maharashtra', 'Nagpur', 'Jan', 711)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (14, 'Maharashtra', 'Nagpur', 'Feb', 682)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (15, 'Maharashtra', 'Nagpur', 'Mar', 684)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (16, 'Maharashtra', 'Nagpur', 'Apr', 842)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (17, 'Maharashtra', 'Nagpur', 'May', 850)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (18, 'Maharashtra', 'Nagpur', 'Jun', 1119)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (19, 'Maharashtra', 'Nagpur', 'Jul', 1101)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (20, 'Maharashtra', 'Nagpur', 'Aug', 1416)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (21, 'Maharashtra', 'Nagpur', 'Sep', 2690)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (22, 'Maharashtra', 'Nagpur', 'Oct', 2921)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (23, 'Maharashtra', 'Nagpur', 'Nov', 3864)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (24, 'Maharashtra', 'Nagpur', 'Dec', 5781)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (25, 'Maharashtra', 'Pune', 'Jan', 591)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (26, 'Maharashtra', 'Pune', 'Feb', 389)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (27, 'Maharashtra', 'Pune', 'Mar', 404)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (28, 'Maharashtra', 'Pune', 'Apr', 582)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (29, 'Maharashtra', 'Pune', 'May', 702)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (30, 'Maharashtra', 'Pune', 'Jun', 1149)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (31, 'Maharashtra', 'Pune', 'Jul', 1232)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (32, 'Maharashtra', 'Pune', 'Aug', 1623)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (33, 'Maharashtra', 'Pune', 'Sep', 2492)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (34, 'Maharashtra', 'Pune', 'Oct', 1911)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (35, 'Maharashtra', 'Pune', 'Nov', 2486)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (36, 'Maharashtra', 'Pune', 'Dec', 4817)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (37, 'Madhya Pradesh', 'Bhopal', 'Jan', 600)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (38, 'Madhya Pradesh', 'Bhopal', 'Feb', 521)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (39, 'Madhya Pradesh', 'Bhopal', 'Mar', 434)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (40, 'Madhya Pradesh', 'Bhopal', 'Apr', 493)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (41, 'Madhya Pradesh', 'Bhopal', 'May', 454)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (42, 'Madhya Pradesh', 'Bhopal', 'Jun', 660)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (43, 'Madhya Pradesh', 'Bhopal', 'Jul', 840)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (44, 'Madhya Pradesh', 'Bhopal', 'Aug', 881)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (45, 'Madhya Pradesh', 'Bhopal', 'Sep', 1452)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (46, 'Madhya Pradesh', 'Bhopal', 'Oct', 2756)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (47, 'Madhya Pradesh', 'Bhopal', 'Nov', 2260)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (48, 'Madhya Pradesh', 'Bhopal', 'Dec', 3200)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (49, 'Madhya Pradesh', 'Indore', 'Jan', 588)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (50, 'Madhya Pradesh', 'Indore', 'Feb', 405)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (51, 'Madhya Pradesh', 'Indore', 'Mar', 414)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (52, 'Madhya Pradesh', 'Indore', 'Apr', 548)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (53, 'Madhya Pradesh', 'Indore', 'May', 604)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (54, 'Madhya Pradesh', 'Indore', 'Jun', 774)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (55, 'Madhya Pradesh', 'Indore', 'Jul', 975)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (56, 'Madhya Pradesh', 'Indore', 'Aug', 1329)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (57, 'Madhya Pradesh', 'Indore', 'Sep', 2283)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (58, 'Madhya Pradesh', 'Indore', 'Oct', 2707)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (59, 'Madhya Pradesh', 'Indore', 'Nov', 3914)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (60, 'Madhya Pradesh', 'Indore', 'Dec', 4100)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (61, 'Karnataka', 'Bangalore', 'Jan', 554)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (62, 'Karnataka', 'Bangalore', 'Feb', 452)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (63, 'Karnataka', 'Bangalore', 'Mar', 610)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (64, 'Karnataka', 'Bangalore', 'Apr', 720)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (65, 'Karnataka', 'Bangalore', 'May', 757)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (66, 'Karnataka', 'Bangalore', 'Jun', 1105)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (67, 'Karnataka', 'Bangalore', 'Jul', 1100)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (68, 'Karnataka', 'Bangalore', 'Aug', 1456)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (69, 'Karnataka', 'Bangalore', 'Sep', 2436)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (70, 'Karnataka', 'Bangalore', 'Oct', 2422)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (71, 'Karnataka', 'Bangalore', 'Nov', 4263)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (72, 'Karnataka', 'Bangalore', 'Dec', 7636)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (73, 'Karnataka', 'Belagavi', 'Jan', 904)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (74, 'Karnataka', 'Belagavi', 'Feb', 680)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (75, 'Karnataka', 'Belagavi', 'Mar', 653)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (76, 'Karnataka', 'Belagavi', 'Apr', 864)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (77, 'Karnataka', 'Belagavi', 'May', 965)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (78, 'Karnataka', 'Belagavi', 'Jun', 1191)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (79, 'Karnataka', 'Belagavi', 'Jul', 1214)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (80, 'Karnataka', 'Belagavi', 'Aug', 1729)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (81, 'Karnataka', 'Belagavi', 'Sep', 2644)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (82, 'Karnataka', 'Belagavi', 'Oct', 2248)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (83, 'Karnataka', 'Belagavi', 'Nov', 4347)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (84, 'Karnataka', 'Belagavi', 'Dec', 8704)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (85, 'Karnataka', 'Mysore', 'Jan', 829)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (86, 'Karnataka', 'Mysore', 'Feb', 750)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (87, 'Karnataka', 'Mysore', 'Mar', 710)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (88, 'Karnataka', 'Mysore', 'Apr', 843)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (89, 'Karnataka', 'Mysore', 'May', 1011)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (90, 'Karnataka', 'Mysore', 'Jun', 1294)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (91, 'Karnataka', 'Mysore', 'Jul', 1457)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (92, 'Karnataka', 'Mysore', 'Aug', 1652)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (93, 'Karnataka', 'Mysore', 'Sep', 2447)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (94, 'Karnataka', 'Mysore', 'Oct', 2850)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (95, 'Karnataka', 'Mysore', 'Nov', 3081)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (96, 'Karnataka', 'Mysore', 'Dec', 5969)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (97, 'Gujarat', 'Ahmedabad', 'Jan', 533)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (98, 'Gujarat', 'Ahmedabad', 'Feb', 454)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (99, 'Gujarat', 'Ahmedabad', 'Mar', 546)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (100, 'Gujarat', 'Ahmedabad', 'Apr', 638)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (101, 'Gujarat', 'Ahmedabad', 'May', 742)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (102, 'Gujarat', 'Ahmedabad', 'Jun', 1059)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (103, 'Gujarat', 'Ahmedabad', 'Jul', 1145)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (104, 'Gujarat', 'Ahmedabad', 'Aug', 1374)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (105, 'Gujarat', 'Ahmedabad', 'Sep', 2641)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (106, 'Gujarat', 'Ahmedabad', 'Oct', 2636)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (107, 'Gujarat', 'Ahmedabad', 'Nov', 3359)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (108, 'Gujarat', 'Ahmedabad', 'Dec', 2500)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (109, 'Gujarat', 'Surat', 'Jan', 615)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (110, 'Gujarat', 'Surat', 'Feb', 539)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (111, 'Gujarat', 'Surat', 'Mar', 736)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (112, 'Gujarat', 'Surat', 'Apr', 763)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (113, 'Gujarat', 'Surat', 'May', 857)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (114, 'Gujarat', 'Surat', 'Jun', 1204)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (115, 'Gujarat', 'Surat', 'Jul', 1254)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (116, 'Gujarat', 'Surat', 'Aug', 1538)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (117, 'Gujarat', 'Surat', 'Sep', 2700)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (118, 'Gujarat', 'Surat', 'Oct', 3091)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (119, 'Gujarat', 'Surat', 'Nov', 3912)");
            sqLiteDatabase.execSQL("INSERT INTO onionprice2019 (id, state, apmc, month, price) VALUES (120, 'Gujarat', 'Surat', 'Dec', 4500)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int monthToNumerical(String month) {
        switch (month) {
            case "Jan":
                return 0;
            case "Feb":
                return 1;
            case "Mar":
                return 2;
            case "Apr":
                return 3;
            case "May":
                return 4;
            case "Jun":
                return 5;
            case "Jul":
                return 6;
            case "Aug":
                return 7;
            case "Sep":
                return 8;
            case "Oct":
                return 9;
            case "Nov":
                return 10;
            case "Dec":
                return 11;
            default:
                return 12;
        }
    }
}