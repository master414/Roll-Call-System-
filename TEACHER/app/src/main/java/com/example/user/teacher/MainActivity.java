package com.example.user.teacher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Button Bt_Da,Bt_Pre,Bt_Se;
    private EditText EeTe_Da,EeTe_Mes,EeTe_Url;
    int SYear, SMonth, SDay,SWeek;                                                               //選擇的日期
    int InYear, InMonth, InDay,InWeek;                                                            //第一週的日期
    String St_Dal,St_We;
    String[] Rece_Da,Rece_We,Rece_Co,Rece_Ur;
    JSONObject MainObj = new JSONObject();
    String Test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bt_Da=(Button)findViewById(R.id.Bt_Da);
        Bt_Pre=(Button)findViewById(R.id.Bt_Pre);
        Bt_Se=(Button)findViewById(R.id.Bt_Se);
        EeTe_Da=(EditText)findViewById(R.id.EeTe_Da);
        EeTe_Mes=(EditText)findViewById(R.id.EeTe_Mes);
        EeTe_Url=(EditText)findViewById(R.id.EeTe_Url);

        InYear=2015;
        InMonth=9;
        InDay=9;
        Calendar ci = new GregorianCalendar( InYear, InMonth-1, InDay); //JAVA中的月份是0~11
        InWeek = ci.get(Calendar.WEEK_OF_YEAR);

        Bt_Da.setOnClickListener(Lin_Bt_Da);
        Bt_Se.setOnClickListener(Lin_Bt_Se);
        Bt_Pre.setOnClickListener(Lin_Bt_Pre);

    }
    //判斷網路是否連接
    public boolean NetworkConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    public void MakeJASON(){
        try {

            JSONObject ItemObj = new JSONObject();
            JSONArray ArrObj = new JSONArray();

            ItemObj.put("Date",St_Dal);
            ItemObj.put("Week",St_We);
            ItemObj.put("Content",EeTe_Mes.getText().toString());
            ItemObj.put("Url_Path",EeTe_Url.getText().toString());

            ArrObj.put(ItemObj);
            MainObj.put("Array_Tr",ArrObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String GetJSON(String url) {

        String Result = "";
        try {
            HttpURLConnection Connect
                    = (HttpURLConnection) new URL(url).openConnection();
            Connect.setRequestMethod("POST");
            Connect.connect();
            if(Connect.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream Input = Connect.getInputStream();
                BufferedReader Reader
                        = new BufferedReader(new InputStreamReader(Input, "utf8"));
                StringBuilder Builder = new StringBuilder();
                String Line;
                while ((Line = Reader.readLine()) != null) {
                    Builder.append(Line);
                }
                Input.close();
                Result = Builder.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }
    public void ParseJSON() {
        String jText = GetJSON("http://140.121.130.158:726/Named_servlet/Servlet");
        try {
            JSONObject jObject = new JSONObject(jText);
            JSONArray jArray = jObject.getJSONArray("Array_Tr");
            Rece_Da = new String [jArray.length()];
            Rece_Co = new String [jArray.length()];
            Rece_We = new String [jArray.length()];
            Rece_Ur = new String [jArray.length()];
            //for (int i = 0; i < jArray.length(); i++) {
               int i = 0;
                Rece_Da[i] = jArray.getJSONObject(i).getString("Date");
                Rece_Co[i] = jArray.getJSONObject(i).getString("Content");
                Rece_We[i] = jArray.getJSONObject(i).getString("Week");
                Rece_Ur[i] = jArray.getJSONObject(i).getString("Url_Path");
            //}
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
   //Send "POST" JSON
    public void SendJSON(){
        String Ur = GetJSON("http://140.121.130.158:726/Named_servlet/Servlet");
        try {
            HttpURLConnection Con
                    = (HttpURLConnection) new URL(Ur).openConnection();
            Con.setRequestMethod("POST"); //use post method
            Con.connect();
            Con.setDoOutput(true); //we will send stuff
            Con.setDoInput(true); //we want feedback
            Con.setUseCaches(false); //no caches
            Con.setAllowUserInteraction(false);
            Con.setRequestProperty("Content-Type", "application/json");

            // Open a stream which can write to the URL******************************************
            OutputStream out = Con.getOutputStream();
            OutputStreamWriter wr = null;
            try {
                wr = new OutputStreamWriter(out);
                wr.write(MainObj.toString());
            }
            catch (IOException e) {
            }
            finally { //in this case, we are ensured to close the output stream
                if (wr != null)
                    wr.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void ShowDatePickerDialog() {
        // 設定初始日期
        Calendar c = Calendar.getInstance();
        SYear = c.get(Calendar.YEAR);
        SMonth = c.get(Calendar.MONTH);
        SDay = c.get(Calendar.DAY_OF_MONTH);

        // 跳出日期選擇器
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Calendar ca = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        SWeek = ca.get(Calendar.WEEK_OF_YEAR)-InWeek+1;
                        St_We=Integer.toString(SWeek);
                        St_Dal = Integer.toString(year) + " / " + Integer.toString(monthOfYear+1) + " / " + Integer.toString(dayOfMonth);

                        // 完成選擇，顯示日期
                        EeTe_Da.setText(St_Dal + "  第 " + St_We +"週");
                    }
                }, SYear, SMonth, SDay);
        dpd.show();
    }
    public void ShowAlertDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("預覽");

        String St_Da = Arrays.toString(Rece_Da);
        String St_We = Arrays.toString(Rece_We);
        String St_Co = Arrays.toString(Rece_Co);
        String St_Ur = Arrays.toString(Rece_Ur);

        dialog.setMessage("Date:" + St_Da + "\n" + "Week:" + St_We + "\n" + "Content:" + St_Co + "\n" + "URL:" + St_Ur+ "\n");
        dialog.setPositiveButton("確定",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialoginterface, int i) {
                    }
                });
        dialog.show();
    }
    Button.OnClickListener Lin_Bt_Pre = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            //判斷是否連線網路
            if(NetworkConnected()){
                ParseJSON();
                ShowAlertDialog();
            }
            else{
                Context Ｒemind = getApplication();
                CharSequence tex = "You are NOT conncted to the internet.";
                int duration2 = Toast.LENGTH_LONG;
                Toast.makeText(Ｒemind, tex, duration2).show();
            }
        }
    };
    Button.OnClickListener Lin_Bt_Da = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            ShowDatePickerDialog();
        }
    };
    Button.OnClickListener Lin_Bt_Se = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            //判斷是否連線網路
            if(NetworkConnected()){
                MakeJASON();
            }
            else{
                Context Ｒemind = getApplication();
                CharSequence tex = "You are NOT conncted to the internet.";
                int duration2 = Toast.LENGTH_LONG;
                Toast.makeText(Ｒemind, tex, duration2).show();
            }
        }
    };
}
