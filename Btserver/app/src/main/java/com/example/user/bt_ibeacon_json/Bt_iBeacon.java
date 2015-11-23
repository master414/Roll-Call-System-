package com.example.user.bt_ibeacon_json;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Bt_iBeacon extends AppCompatActivity {
    public TextView Title;
    public ListView List;
    public Item ListItem;
    public ListViewAdapter ListAdapter;
    public ArrayList<Item> ListData;
    public String[] Date;
    public String[] Content;
    public static Context ctx;


    @Override
    public  void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_i_beacon);

        Title=(TextView)findViewById(R.id.Txt);
        List=(ListView)findViewById(R.id.ListView);
        ListData = new ArrayList<>();
        ParseJSON();


        for(int i=0;i<Date.length;i++){
            ListItem=new Item(Date[i],Content[i]);
            ListData.add(ListItem);
        }

        ListAdapter = new ListViewAdapter(this, ListData);
        List.setAdapter(ListAdapter);

        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter.ExpandView(view, position);
            }
        });
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
        String jText = GetJSON("http://140.121.130.158:726/RevlisCMate_NewMsg/RevlisServlet");
        try {
            JSONObject jObject = new JSONObject(jText);
            JSONArray jArray = jObject.getJSONArray("NewMsg");
            Date = new String [jArray.length()];
            Content = new String [jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {
                Date[i] = jArray.getJSONObject(i).getString("Date");
                Content[i] = jArray.getJSONObject(i).getString("Content");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ListViewAdapter extends BaseAdapter {

        public Context Ctx;
        public ArrayList<Item> Data;
        public LayoutInflater inflater;
        public View CurrView = null;
        public int CurrPos = -1;

        public ListViewAdapter(Context ctx, ArrayList<Item> data) {
            super();
            this.Ctx = ctx;
            this.Data = data;
        }

        @Override
        public int getCount() {
            return Data.size();
        }

        @Override
        public Object getItem(int position) {
            return Data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holdview = new ViewHolder();
            Item item = Data.get(position);
            if(convertView == null) {
                inflater = LayoutInflater.from(Ctx);
                convertView = inflater.inflate(R.layout.item, null);
                holdview.DateView = (TextView)convertView.findViewById(R.id.ItemDate);
                holdview.DateView.setTextColor(Color.parseColor("#0000FF"));
                holdview.DateView.setTextSize(25);
                holdview.ContentView = (TextView)convertView.findViewById(R.id.ItemContent);
                holdview.ContentView.setTextColor(Color.parseColor("#000000"));
                holdview.ContentView.setTextSize(20);



                if(position % 2 == 0)
                    holdview.DateView.setBackgroundColor(Color.parseColor("#AAAAAA"));
                else
                    holdview.DateView.setBackgroundColor(Color.parseColor("#DDDDDD"));
                holdview.ContentView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                convertView.setTag(holdview);
            }else{
                holdview = (ViewHolder)convertView.getTag();
            }

            holdview.DateView.setText(item.Date);
            holdview.ContentView.setText(item.Content);
            holdview.ContentView.setVisibility(View.GONE);
            return convertView;
        }

        public void ExpandView(View view, int position) {
            if(CurrView != null && CurrPos != position ) {
                ViewHolder holdview = (ViewHolder)CurrView.getTag();
                if(holdview.ContentView.getVisibility() == View.VISIBLE)
                    holdview.ContentView.setVisibility(View.GONE);
            }

            CurrPos = position;
            CurrView = view;
            ViewHolder holdview = (ViewHolder)view.getTag();
            switch(holdview.ContentView.getVisibility()) {
                case View.GONE:
                    holdview.ContentView.setVisibility(View.VISIBLE);
                    break;
                case View.VISIBLE:
                    holdview.ContentView.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public class ViewHolder
    {
        TextView DateView;
        TextView ContentView;
    }

    public class Item{
        public String Date;
        public String Content;

        public Item(String date,String content){
            this.Date=date;
            this.Content=content;
        }
    }
}