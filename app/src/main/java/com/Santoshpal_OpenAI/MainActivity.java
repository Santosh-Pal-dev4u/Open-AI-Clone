package com.Santoshpal_OpenAI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


public class MainActivity extends AppCompatActivity {

    CardView cardView,cardView1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.darkgrey));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cardView=findViewById(R.id.Dall_e);
        cardView1=findViewById(R.id.ChatGPT);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Dall_e.class);
                startActivity(intent);

            }
        });
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ChatGpt.class);
                startActivity(intent);
            }
        });




    }
}