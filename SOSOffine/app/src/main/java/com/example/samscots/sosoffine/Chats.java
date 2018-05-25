package com.example.samscots.sosoffine;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment {

    public static List<UsersChat> userschatList;
    public RecyclerView rv;
    public View layout;
    public static RecyclerView.Adapter adapt;
    public SQLDB sqldbb;
    public Cursor sqldbb_cursor;
    public String[] strarr;
    public int i=0,count;


    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout=inflater.inflate(R.layout.fragment_chats, container, false);
        return layout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rv=(RecyclerView) layout.findViewById(R.id.chat_frag_recycler_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        userschatList=new ArrayList<>();
        adapt=new UserChatAdapter(userschatList,getContext());
        rv.setAdapter(adapt);


        sqldbb=new SQLDB(getContext());
        sqldbb_cursor=sqldbb.getAllData();
        i=0;
        count=0;
        strarr=new String[sqldbb_cursor.getCount()];
        Log.d("Chats","Total Present - "+sqldbb_cursor.getCount());
        try{
            if(sqldbb_cursor.getCount()>0)
            while (sqldbb_cursor.moveToNext()){
                UsersChat uc=new UsersChat(sqldbb_cursor.getString(1),sqldbb_cursor.getString(2),sqldbb_cursor.getString(3),sqldbb_cursor.getString(4));
                strarr[i]=sqldbb_cursor.getString(1);
                Log.d("Chats","Current ID - "+sqldbb_cursor.getString(1)+" position "+i);
                for(int j=0;j<=i;j++){
                    String s=strarr[j];
                    Log.d("Chats","Id - "+s);
                    if(s.equals(sqldbb_cursor.getString(1)))
                        count++;
                }
                Log.d("Chats","Count - "+count);
                if(count==1) {
                    userschatList.add(uc);
                    adapt.notifyDataSetChanged();
                }
                i++;
                count=0;
            }
        }catch (Exception e) {
            Toast.makeText(getContext(), "Error Loading Data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sqldbb.delete();
        for(int i=0;i<userschatList.size();i++){
        UsersChat uuc=userschatList.get(i);
        sqldbb.addsqlData(uuc.getIdenty(),uuc.getDisplay_name(),uuc.getLast_message(),uuc.getProfile_uri());
        }
    }

}
