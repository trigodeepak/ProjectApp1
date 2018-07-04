package com.example.deepak.brikha;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.deepak.brikha.Model.BabyName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {
    final public static String BABY_LIST= "baby_list";
    final public static String HASH_CODE= "hash_set";
    final public static String SHARED_PREFS_FILE = "BrikhasharedPref";
    public static List<BabyName> babyNameList, mbabyNameList,fbabynameList,searchbabyNameList;
    public static Set<Integer> set;
    AllGenderFragment Fragment1;
    MaleFragment Fragment3;
    FemaleFragment Fragment2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo apply admob have done sample admob
        super.onCreate(savedInstanceState);
        babyNameList = new ArrayList<>();
        mbabyNameList = new ArrayList<>();
        fbabynameList = new ArrayList<>();
        searchbabyNameList = new ArrayList<>();
        set = new HashSet<>();

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);

        try {
            babyNameList = (ArrayList<BabyName>) ObjectSerializer.deserialize(prefs.getString(BABY_LIST, ObjectSerializer.serialize(new ArrayList<BabyName>())));
            set = (HashSet<Integer>) ObjectSerializer.deserialize(prefs.getString(HASH_CODE, ObjectSerializer.serialize(new HashSet<Integer>())));
            makeMaleFemaleBabyList();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ReverseList(babyNameList);

        setContentView(R.layout.activity_main);
        Fragment1 = new AllGenderFragment();
        Fragment2 = new FemaleFragment();
        Fragment3 = new MaleFragment();
        ViewPager viewPager =  findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

//        MobileAds.initialize(this, "ca-app-pub-5234423351540636~1347457065");

        Searching_in_List("Ab");

        try {
            new MainActivity.MyTask().execute(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }
//  
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.search:
//                return true;
//            case R.id.order:
//                ReverseList(babyNameList);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(Fragment1, "All");
        adapter.addFragment(Fragment2, "Girl");
        adapter.addFragment(Fragment3, "Boy");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


private class MyTask extends AsyncTask<Object, Void, String> {
    @Override
    protected String doInBackground(Object... params) {
        try {
            StringBuilder sb = new StringBuilder();
            //todo change ip whenever the ip changes
            URL url = new URL("http://172.21.7.249/android_app/get_data.php");
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF8"));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            in.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String str) {
        Log.d("Result",str+" ");
        parseResult(str);
        ViewPager viewPager =  findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        }
}

    public void parseResult(String FetchData) {
        // Function to take json as string and parse it into the Baby Names list
        String result = "{\"Baby_Names_List\":" + FetchData + "}";
        Boolean Flag = false;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("Baby_Names_List");
            for (int i = 0; i < jsonArray.length(); i++) {
                BabyName babyName = new BabyName();
                JSONObject reader = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(reader.getString("id"));
                if (!set.contains(id)){
                    set.add(id);
                    Flag = true;
                    babyName.setName(reader.getString("name"));
                    babyName.setPronunciation(reader.getString("pronunciation"));
                    babyName.setMeaning(reader.getString("meaning"));
                    babyName.setArabic(reader.getString("arabic"));
                    babyName.setArabicMeaning(reader.getString("arabic_meaning"));
                    babyName.setSyriac(reader.getString("syriac"));
                    if (Integer.parseInt(reader.getString("is_male"))==1){
                        babyName.setIs_boy(true);
                    }else
                        babyName.setIs_boy(false);
//                URLEncoder.encode("臺北市", "utf-8")
                babyNameList.add(i,babyName);}
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(Flag){
        makeMaleFemaleBabyList();
        structureBabylist();
        storeToSharedPreferences();}

    }
    public void makeMaleFemaleBabyList(){
        for(int i = 0;i<babyNameList.size();i++){
            if(babyNameList.get(i).getIs_boy())
                mbabyNameList.add(babyNameList.get(i));
            else
                fbabynameList.add(babyNameList.get(i));
        }
    }

    public void structureBabylist(){
        babyNameList = new ArrayList<>();
        int m_size = mbabyNameList.size(),f_size = fbabynameList.size();
        int j=0,k=0;
        for(int i = 0;i<f_size+m_size;i++){
            if(j==m_size || k==f_size)
                break;
            if(i%2==0 && j<m_size) {
                babyNameList.add(mbabyNameList.get(j));
                j+=1;
            }
            else if (k<f_size){
                babyNameList.add(fbabynameList.get(k));
                k+=1;
            }
        }
        while(k<f_size){
            babyNameList.add(fbabynameList.get(k));
            k++;
        }
        while(j<m_size){
            babyNameList.add(mbabyNameList.get(j));
            j++;
        }
    }
    public void storeToSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(BABY_LIST,ObjectSerializer.serialize((Serializable) babyNameList));
            editor.putString(HASH_CODE,ObjectSerializer.serialize((Serializable) set));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }
    public static void Searching_in_List(String s){
        //Works Seamlessly
        searchbabyNameList = new ArrayList<>();
        for(BabyName b:babyNameList){
            if(b.getName().contains(s)){
                Log.d("Find in ",b.getName());
                searchbabyNameList.add(b);
            }
        }
    }

    public void ReverseList(List<BabyName> babyNames){
        //Reversing list works
        Collections.reverse(babyNameList);
        Fragment1 = new AllGenderFragment();
        ViewPager viewPager =  findViewById(R.id.viewpager);
        setupViewPager(viewPager);

    }
}
