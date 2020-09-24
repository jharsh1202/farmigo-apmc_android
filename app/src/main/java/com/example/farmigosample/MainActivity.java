package com.example.farmigosample;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LineDataSet lineDataSetState1;
    LineDataSet lineDataSetState2;
    Spinner spinnerStates,spinnerStartMonth,spinnerEndMonth,spinnerAPMC;
    LineChart lineChart;
    Cursor dataOnionPrice;
    SQLiteDatabase sqLiteDatabase;
    String stateSelected=null;
    String APMCSelected=null;


    String[] states = { "Maharashtra", "Madhya Pradesh", "Karnataka", "Gujrat"};
    String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String[] apmcmarkets = { "Mumbai", "Pune", "Nagpur", "Bhopal", "Indore" , "Bangalore",
            "Belagavi", "Mysore","Surat","Ahmedabad"};

    protected ArrayList<Entry>State1() {
        sqLiteDatabase = this.openOrCreateDatabase("OnionPricesDb", 0, null);
        ArrayList<Entry> dataVals=new ArrayList<Entry>();

        /*if(stateSelected!=null)
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019 where state = "+'"'+stateSelected+'"', null);
        else
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019", null);*/
        if(stateSelected!=null)
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019 where state = "+'"'+stateSelected+'"', null);
        else
            dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019", null);

        int idIndex = dataOnionPrice.getColumnIndex("id");
        int stateIndex = dataOnionPrice.getColumnIndex("state");
        int apmcIndex = dataOnionPrice.getColumnIndex("apmc");
        int monthIndex = dataOnionPrice.getColumnIndex("month");
        int priceIndex = dataOnionPrice.getColumnIndex("price");
        dataOnionPrice.moveToFirst(); //ColumnIndexes
        while (!dataOnionPrice.isAfterLast()) {
            Log.i("Results - id", Integer.toString(dataOnionPrice.getInt(idIndex)));
            Log.i("Results - month", dataOnionPrice.getString(monthIndex));
            Log.i("Results - price", Integer.toString(dataOnionPrice.getInt(priceIndex)));

            dataVals.add(new Entry(dataOnionPrice.getInt(idIndex),dataOnionPrice.getInt(priceIndex)));
            dataOnionPrice.moveToNext();
        }
        return dataVals;
    }

    /*protected ArrayList<Entry>State2(){
        ArrayList<Entry> dataVals=new ArrayList<Entry>();
        dataVals.add(new Entry(10,90));
        dataVals.add(new Entry(20,30));
        dataVals.add(new Entry(30,40));
        dataVals.add(new Entry(70,70));
        return dataVals;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*//database setup
        databaseSetup();*/
        /*//print database in log
        logDatabase();*/

        lineChart = findViewById(R.id.graphss);
        spinnerStates = findViewById(R.id.spinnerStates);
        spinnerStartMonth = findViewById(R.id.spinnerStartMonth);
        spinnerEndMonth = findViewById(R.id.spinnerEndMonth);
        spinnerAPMC = findViewById(R.id.spinnerAPMC);

        lineDataSetState1=new LineDataSet(State1(),"State1");
        //lineDataSetState2=new LineDataSet(State2(),"State2");

        //for LineChart customization
        configureLineChart();
        //adding data to spinner
        setSpinnerData();
        //adding data to lineChart
        setLineChart();
        StateFilter();
    }




    void StateFilter(){
        stateSelected="Maharashtra";     //state selected
    }

    void APMCFilter(){
        APMCSelected="Mumbai";
    }


    void setLineChart(){
        ArrayList<ILineDataSet>dataSets=new ArrayList<>();
        dataSets.add(lineDataSetState1);
        //dataSets.add(lineDataSetState2);
        LineData lineData=new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    void setSpinnerData(){
        ArrayAdapter<String> States = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
        ArrayAdapter<String> Apmcmarkets = new ArrayAdapter(this, android.R.layout.simple_spinner_item, apmcmarkets);
        ArrayAdapter<String> Months = new ArrayAdapter(this, android.R.layout.simple_spinner_item, months);

        States.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStates.setAdapter(States);

        Months.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartMonth.setAdapter(Months);
        spinnerEndMonth.setAdapter(Months);

        Apmcmarkets.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAPMC.setAdapter(Apmcmarkets);
    }
    private void configureLineChart() {
        Description desc = new Description();
        desc.setText("Onion Market Price History");
        desc.setTextSize(16);
        lineChart.setDescription(desc);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });

        //lineDataSetState2.setColor(R.color.colorPrimary);
        //lineDataSetState2.setLineWidth(3);
        lineDataSetState1.setLineWidth(3);
    }



    void logDatabase(){
        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("OnionPricesDb", 0, null);
        dataOnionPrice = sqLiteDatabase.rawQuery("SELECT * FROM onionprice2019", null);
        int idIndex = dataOnionPrice.getColumnIndex("id");
        int stateIndex = dataOnionPrice.getColumnIndex("state");
        int apmcIndex = dataOnionPrice.getColumnIndex("apmc");
        int monthIndex = dataOnionPrice.getColumnIndex("month");
        int priceIndex = dataOnionPrice.getColumnIndex("price");
        dataOnionPrice.moveToFirst();

        while (!dataOnionPrice.isAfterLast()) {
            Log.i("Results - id", Integer.toString(dataOnionPrice.getInt(idIndex)));
            Log.i("Results - state", dataOnionPrice.getString(stateIndex));
            Log.i("Results - apmc", dataOnionPrice.getString(apmcIndex));
            Log.i("Results - month", dataOnionPrice.getString(monthIndex));
            Log.i("Results - price", Integer.toString(dataOnionPrice.getInt(priceIndex)));

            dataOnionPrice.moveToNext();
        }

    }
    void databaseSetup(){
//        try {
//            SQLiteDatabase onionDatabase=this.openOrCreateDatabase("onionpricek",MODE_PRIVATE,null);
//            onionDatabase.execSQL("CREATE TABLE if not exists onionprices(id integer,state text,apmc text,month text,price integer)");
//            Cursor c=onionDatabase.rawQuery("SELECT * from onionprices",null);
//            c.moveToFirst();
//            c.move(5);
//            Log.i("databaseqq",c.getString(3));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("OnionPricesDb", 0, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS onionprice2019 ( id INT(5)," +
                    " state Varchar(20), apmc Varchar(20),month varchar(5),price int(8))" );

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
    int monthToNumerical(String month){
        switch (month){
            case "Jan": return 0;
            case "Feb": return 1;
            case "Mar": return 2;
            case "Apr": return 3;
            case "May": return 4;
            case "Jun": return 5;
            case "Jul": return 6;
            case "Aug": return 7;
            case "Sep": return 8;
            case "Oct": return 9;
            case "Nov": return 10;
            case "Dec": return 11;
            default: return 12;
        }
    }
}