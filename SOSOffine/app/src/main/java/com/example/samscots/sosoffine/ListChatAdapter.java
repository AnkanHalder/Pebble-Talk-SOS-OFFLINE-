package com.example.samscots.sosoffine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sam Scots on 12/5/2017.
 */

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {



    private List<ListChat> listChats;
    private Context context;
    private int self;
    Uri uri;



    public ListChatAdapter(List<ListChat> listChats, Context context) {
        this.listChats = listChats;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==0)
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_layout,parent,false);
        else if(viewType==1)
           view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_chat_layout,parent,false);
        else if(viewType==2)
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_image_layout,parent,false);
        else
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_chat_image_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListChat listChat=listChats.get(position);

        if(listChat.getMine()==0 ||listChat.getMine()==1)
        holder.message.setText(listChat.getMesage());

       if(listChat.getMine()==2 ||listChat.getMine()==3) {
           Uri uriFromPath = Uri.fromFile(new File(listChat.getMesage()));
           holder.imageView.setImageURI(uriFromPath);
       }
        if(listChat.getMine()==1 ||listChat.getMine()==3) {
           if(!get_userimage().equals(""))
               uri = Uri.fromFile(new File(get_userimage()));
           Log.d("Adapter 1,3",get_userimage());
            Picasso.with(context).load(uri)
                    .error(R.drawable.profile)
                    .fit().centerInside()
                    .into( holder.cv);
        }
        if(listChat.getMine()==0 ||listChat.getMine()==2) {
            if(!listChat.getUser_image().equals("")) {
                Log.d("Adapter 1,3",listChat.getUser_image());
                Uri uriFromPath = Uri.fromFile(new File(listChat.getUser_image()));
                Picasso.with(context).load(uriFromPath)
                        .error(R.drawable.profile)
                        .fit().centerInside()
                        .into( holder.cv);
            }
        }


    }



    @Override
    public int getItemViewType(int position) {

        ListChat ls=listChats.get(position);

        if(ls.getMine()==0)
            return 0;
        else if(ls.getMine()==1)
            return 1;
        else if(ls.getMine()==2)
            return 2;
        else if(ls.getMine()==3)
            return 3;

        return position;
    }

    @Override
    public int getItemCount() {
        return listChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView message;
        private CircleImageView cv;
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);

            cv=(CircleImageView)itemView.findViewById(R.id.userchat_circleImageView);
            message=(TextView)itemView.findViewById(R.id.friend_text_view);
            imageView=(ImageView)itemView.findViewById(R.id.image_received);
        }
    }
    public String get_userimage(){
       return ChatActivity.image_path;
    }


}
