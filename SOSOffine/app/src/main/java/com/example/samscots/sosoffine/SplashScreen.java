package com.example.samscots.sosoffine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends Activity {

    ImageView g1,g2,pt;
    Animation from_left,from_right,heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        g1=(ImageView)findViewById(R.id.gp1);
        g2=(ImageView)findViewById(R.id.gp2);
        pt=(ImageView)findViewById(R.id.pt);
        animate();




        Thread splash=new Thread(){
            @Override
            public void run() {
                super.run();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                }

                Intent go_main=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(go_main);
                finish();
            }
        };
        splash.start();
    }

    void animate(){
        from_left= AnimationUtils.loadAnimation(this,R.anim.from_left);
        g1.setAnimation(from_left);
        from_right= AnimationUtils.loadAnimation(this,R.anim.from_right);
        g2.setAnimation(from_right);
        heading= AnimationUtils.loadAnimation(this,R.anim.heading);
        pt.setAnimation(heading);
    }
}
