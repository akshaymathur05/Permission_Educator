/*
    Code Created by: Javier Ignacio Campos and Ethan Ewoldt
    Code Ver. 0.5
    Current Goals:
    1.) Make permissions from textview clickable
    2.) Update app to fit architecture
    3.) Work on Database
    4.) Write up an Info Page

    Current Issues:
 */

package com.example.permissioneducator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    private Button systemButton;
    private Button storeButton;
    private ImageButton infoButton;
    private ImageButton exitButton;


    //Test for server connection stuff
    private Button clickMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code for the app's buttons
        systemButton = findViewById(R.id.system_button);
        systemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View systemView){
                openSystemAppPage();
            }
        });

        storeButton = findViewById(R.id.store_button);
        storeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View storeView){
                openStoreAppPage();
            }
        });

        infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View infoView){
                setContentView(R.layout.info_page);
            }
        });

        //So this is an interesting tidbit: It is better to let the OS decide to kill the activity then manually close with exit(0)
        exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View exitView){
                Intent exitIntent = new Intent(Intent.ACTION_MAIN);
                exitIntent.addCategory(Intent.CATEGORY_HOME);
                exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(exitIntent);
            }
        });
    }

    public void openSystemAppPage(){
        Intent intent = new Intent(this, SystemAppPage_2.class);
        startActivity(intent);
    }
    public void openStoreAppPage(){
        Intent intent = new Intent(this, StoreAppPage_2.class);
        startActivity(intent);
    }

}