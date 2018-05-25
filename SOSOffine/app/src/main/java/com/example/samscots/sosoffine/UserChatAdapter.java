package com.example.samscots.sosoffine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sam Scots on 12/30/2017.
 */

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {

    private List<UsersChat> userchat;
    private Context context;

    public UserChatAdapter(List<UsersChat> userchat, Context context) {
        this.userchat = userchat;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_users,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UsersChat item=userchat.get(position);
        try {
            holder.display_name.setText(item.getDisplay_name());
            holder.last_message.setText(item.getLast_message());
            holder.id_of_user.setText(item.getIdenty());
            holder.circularImage.setImageURI(get_URI(item.getProfile_uri()));
            holder.set_in_tv_uri.setText(item.getProfile_uri());

            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent go_chat_frm_userchat=new Intent(context,ChatActivity.class);
                    go_chat_frm_userchat.putExtra("DisplayName",item.getDisplay_name());
                    go_chat_frm_userchat.putExtra("DisplayUCName",item.getIdenty());
                    go_chat_frm_userchat.putExtra("DisplayUID",item.getProfile_uri());
                    context.startActivity(go_chat_frm_userchat);
                }
            });


            holder.rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    CharSequence option[] = new CharSequence[] {"Delete conversation"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   // builder.setTitle("Choose Option");
                    builder.setItems(option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0){
                                SQLiteHelper sqLiteHelper=new SQLiteHelper(context,item.getIdenty());
                                sqLiteHelper.del_table();
                                userchat.remove(position);
                                Chats.adapt.notifyDataSetChanged();
                                //update db
                                SQLDB sqldbb=new SQLDB(context);
                                Cursor sqldbb_cursor=sqldbb.getAllData();
                                sqldbb.delete();
                                for(int i=0;i<Chats.userschatList.size();i++){
                                    UsersChat uuc=Chats.userschatList.get(i);
                                    sqldbb.addsqlData(uuc.getIdenty(),uuc.getDisplay_name(),uuc.getLast_message(),uuc.getProfile_uri());
                                    Toast.makeText(context, "All the messages of you and "+item.getDisplay_name()+" are deleted", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    });
                    builder.show();

                    return true;
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView display_name;
        public TextView last_message;
        public CircleImageView circularImage;
        public TextView id_of_user;
        public TextView set_in_tv_uri;
        public RelativeLayout rl;


        public ViewHolder(View itemView) {
            super(itemView);

            display_name=(TextView)itemView.findViewById(R.id.chat_frag_name);
            last_message=(TextView)itemView.findViewById(R.id.chat_frag_last_message);
            circularImage=(CircleImageView)itemView.findViewById(R.id.chat_fragment_image);
            id_of_user=(TextView)itemView.findViewById(R.id.chat_frag_store_id);
            set_in_tv_uri=(TextView)itemView.findViewById(R.id.chat_frag_image_uri);
            rl=(RelativeLayout)itemView.findViewById(R.id.chat_frag_relative_layout);

        }
    }

    public Uri get_URI(String str){
        Uri uriFromPath = Uri.fromFile(new File(str));
        return uriFromPath;
    }
}
