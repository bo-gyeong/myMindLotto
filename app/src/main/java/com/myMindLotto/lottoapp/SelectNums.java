package com.myMindLotto.lottoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lottoapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectNums extends AppCompatActivity {

    ChangeItems changeItems = new ChangeItems();
    Button allSelectBtn, allOutBtn, allInitialBtn, onlySelectCombBtn, mixCombBtn, saveCombNumBtn, callCombNumBtn;

    ArrayList<Integer> selNumsList;
    ArrayList<Integer> outNumsList;
    boolean isMixed, isModify;
    String getTitle;
    int[] getNums;
    SharedPreferences sharedPreferences;

    SQLiteDatabase sqlDB;
    LottoNumDB lottoNumDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_nums);
        setTitle(getString(R.string.selectNBtn));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE);
        checkFirstRun();

        Integer[] numBtnId = new Integer[]{R.id.num1Btn, R.id.num2Btn, R.id.num3Btn, R.id.num4Btn, R.id.num5Btn
        , R.id.num6Btn, R.id.num7Btn, R.id.num8Btn, R.id.num9Btn, R.id.num10Btn,
                R.id.num11Btn, R.id.num12Btn, R.id.num13Btn, R.id.num14Btn, R.id.num15Btn
                , R.id.num16Btn, R.id.num17Btn, R.id.num18Btn, R.id.num19Btn, R.id.num20Btn,
                R.id.num21Btn, R.id.num22Btn, R.id.num23Btn, R.id.num24Btn, R.id.num25Btn
                , R.id.num26Btn, R.id.num27Btn, R.id.num28Btn, R.id.num29Btn, R.id.num30Btn,
                R.id.num31Btn, R.id.num32Btn, R.id.num33Btn, R.id.num34Btn, R.id.num35Btn
                , R.id.num36Btn, R.id.num37Btn, R.id.num38Btn, R.id.num39Btn, R.id.num40Btn,
                R.id.num41Btn, R.id.num42Btn, R.id.num43Btn, R.id.num44Btn, R.id.num45Btn};

        Button[] numBtn = new Button[numBtnId.length];
        final int[] numBtnClickTimes = new int[45];

        Intent intent= getIntent();
        if (intent.getExtras() != null){
            getTitle = intent.getStringExtra("getTitle");
            getNums = intent.getExtras().getIntArray("getNums");
            isModify = intent.getBooleanExtra("isModify", false);

            for (int i=0; i<numBtnId.length; i++){
                numBtn[i] = (Button) findViewById(numBtnId[i]);
                numBtnClickTimes[i] = getNums[i];

                if (numBtnClickTimes[i]%3 == 1){
                    changeItems.selectItems(numBtn, i, getApplicationContext());
                }
                else if (numBtnClickTimes[i]%3 == 2){
                    changeItems.outItems(numBtn, i, getApplicationContext());
                }
                else{
                    changeItems.initialItems(numBtn, i, getApplicationContext());
                }
            }

        } else{
            for (int i=0; i<numBtnId.length; i++){
                numBtn[i] = (Button) findViewById(numBtnId[i]);
                numBtnClickTimes[i] = 0;
            }
        }  //SeeCombNumListView ??????????????? intent??? ?????? ???????????? ?????????????????? ?????? ??????

        for (int i=0; i<numBtnId.length; i++){
            int finalI = i;

            numBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (numBtnClickTimes[finalI]%3 == 0){
                        changeItems.selectItems(numBtn, finalI, getApplicationContext());
                        numBtnClickTimes[finalI]++;
                    }
                    else if (numBtnClickTimes[finalI]%3 == 1){
                        changeItems.outItems(numBtn, finalI, getApplicationContext());
                        numBtnClickTimes[finalI]++;
                    }
                    else{
                        changeItems.initialItems(numBtn, finalI, getApplicationContext());
                        numBtnClickTimes[finalI] = 0;
                    }
                }
            });
        }  //??? ?????? ?????? ??? ?????? ??????

        allSelectBtn = (Button) findViewById(R.id.allSelectBtn);
        allSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<numBtnId.length; i++) {
                    int finalI = i;

                    changeItems.selectItems(numBtn, finalI, getApplicationContext());
                    numBtnClickTimes[finalI] = 1;
                }
            }
        });  //?????? ?????? ??????

        allOutBtn = (Button) findViewById(R.id.allOutBtn);
        allOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<numBtnId.length; i++) {
                    int finalI = i;

                    changeItems.outItems(numBtn, finalI, getApplicationContext());
                    numBtnClickTimes[finalI] = 2;
                }
            }
        });  //?????? ?????? ??????

        allInitialBtn = (Button) findViewById(R.id.allInitialBtn);
        allInitialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<numBtnId.length; i++) {
                    int finalI = i;

                    changeItems.initialItems(numBtn, finalI, getApplicationContext());
                    numBtnClickTimes[finalI] = 0;
                }
            }
        });  //????????? ??????

        onlySelectCombBtn = (Button) findViewById(R.id.onlySelectCombBtn);
        onlySelectCombBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selNumsList = new ArrayList<>();
                outNumsList = new ArrayList<>();
                isMixed = false;

                for (int i=0; i<numBtnId.length; i++) {
                    if(numBtnClickTimes[i] == 1){
                        selNumsList.add(i+1);
                    }  //numBtnClickTimes[i]??? 1??? ????????? ????????? ??? ????????? ?????? ???????????? ??????
                    else if (numBtnClickTimes[i] == 2){
                        outNumsList.add(i+1);
                    }  //numBtnClickTimes[i]??? 2??? ????????? ????????? ??? ????????? ?????? ???????????? ??????
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putIntegerArrayListExtra("selNums", selNumsList);
                intent.putIntegerArrayListExtra("outNums", outNumsList);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                if (selNumsList.size()==6){
                    Toast.makeText(getApplicationContext(), "6?????? ????????? ???????????? ??????????????? ?????? ????????????", Toast.LENGTH_SHORT).show();
                }
                else if (outNumsList.size()>38){
                    Toast.makeText(getApplicationContext(), "?????? 7?????? ????????? ???????????????", Toast.LENGTH_SHORT).show();
                }
                else if (selNumsList.size()<6){
                    Toast.makeText(getApplicationContext(), " ????????? ????????? ????????? ??????\n?????? ????????? ?????? ???????????????", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "??????????????? ???????????????", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });

        mixCombBtn = (Button) findViewById(R.id.mixCombBtn);
        mixCombBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selNumsList = new ArrayList<>();
                outNumsList = new ArrayList<>();
                isMixed = true;

                for (int i=0; i<numBtnId.length; i++) {
                    if(numBtnClickTimes[i] == 1){
                        selNumsList.add(i+1);
                    }  //numBtnClickTimes[i]??? 1??? ????????? ????????? ??? ????????? ?????? ???????????? ??????
                    else if (numBtnClickTimes[i] == 2){
                        outNumsList.add(i+1);
                    }  //numBtnClickTimes[i]??? 2??? ????????? ????????? ??? ????????? ?????? ???????????? ??????
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putIntegerArrayListExtra("selNums", selNumsList);
                intent.putIntegerArrayListExtra("outNums", outNumsList);
                intent.putExtra("isMixed", isMixed);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                if (outNumsList.size()>38){
                    Toast.makeText(getApplicationContext(), "?????? 7?????? ????????? ???????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "??????????????? ???????????????", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });

        lottoNumDB = new LottoNumDB(this);
        saveCombNumBtn = (Button) findViewById(R.id.saveCombNumBtn);
        saveCombNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(SelectNums.this, R.layout.save_dlg_layout, null);
                final EditText titleEdit = (EditText) view.findViewById(R.id.saveCombEditText);

                AlertDialog.Builder dlg = new AlertDialog.Builder(SelectNums.this);
                dlg.setTitle("????????? ??????????????????");
                if (isModify){
                    titleEdit.setText(getTitle);
                }
                dlg.setView(view);
                dlg.setNegativeButton("??????", null);
                dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String titleStr = titleEdit.getText().toString();
                        sqlDB = lottoNumDB.getWritableDatabase();
                        //lottoNumDB.onUpgrade(sqlDB, 1, 2);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);

                        Cursor cursor = null;
                        try {
                            cursor = sqlDB.rawQuery("SELECT combTitle FROM combNum WHERE combTitle = '" + titleStr + "';", null);
                        }catch (Exception e){
                            e.printStackTrace();
                        }  //?????? ?????? ???????????? ???????????? ??????

                        String title = null;
                        if (cursor != null){
                            while (cursor.moveToNext()) {
                                title = cursor.getString(0);
                            }
                        }  //cursor??? ?????? ????????? title??? ??????

                        if (title != null && !isModify){  //title??? null??? ????????? ???????????? ?????? ????????? false??? ????????????
                            Toast.makeText(getApplicationContext(), "?????? ?????? ???????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isModify){  //?????? ????????? ???????????? ???
                                sqlDB.execSQL("DELETE FROM combNum WHERE combTitle = '" + title + "';");
                            }
                            sqlDB.execSQL("INSERT INTO combNum VALUES ('" + titleStr + "', '"
                                    + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "', "
                                    + numBtnClickTimes[0] + ", " + numBtnClickTimes[1] + ", " + numBtnClickTimes[2]
                                    + ", " + numBtnClickTimes[3] + ", " + numBtnClickTimes[4] + ", " + numBtnClickTimes[5]
                                    + ", " + numBtnClickTimes[6] + ", " + numBtnClickTimes[7] + ", " + numBtnClickTimes[8]
                                    + ", " + numBtnClickTimes[9] + ", " + numBtnClickTimes[10]
                                    + ", " + numBtnClickTimes[11] + ", " + numBtnClickTimes[12]
                                    + ", " + numBtnClickTimes[13] + ", " + numBtnClickTimes[14] + ", " + numBtnClickTimes[15]
                                    + ", " + numBtnClickTimes[16] + ", " + numBtnClickTimes[17] + ", " + numBtnClickTimes[18]
                                    + ", " + numBtnClickTimes[19] + ", " + numBtnClickTimes[20]
                                    + ", " + numBtnClickTimes[21] + ", " + numBtnClickTimes[22]
                                    + ", " + numBtnClickTimes[23] + ", " + numBtnClickTimes[24] + ", " + numBtnClickTimes[25]
                                    + ", " + numBtnClickTimes[26] + ", " + numBtnClickTimes[27] + ", " + numBtnClickTimes[28]
                                    + ", " + numBtnClickTimes[29] + ", " + numBtnClickTimes[30]
                                    + ", " + numBtnClickTimes[31] + ", " + numBtnClickTimes[32]
                                    + ", " + numBtnClickTimes[33] + ", " + numBtnClickTimes[34] + ", " + numBtnClickTimes[35]
                                    + ", " + numBtnClickTimes[36] + ", " + numBtnClickTimes[37] + ", " + numBtnClickTimes[38]
                                    + ", " + numBtnClickTimes[39] + ", " + numBtnClickTimes[40]
                                    + ", " + numBtnClickTimes[41] + ", " + numBtnClickTimes[42]
                                    + ", " + numBtnClickTimes[43] + ", " + numBtnClickTimes[44] + ");");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                        }
                        sqlDB.close();
                    }
                });
                dlg.show();
            }
        });

        callCombNumBtn = (Button) findViewById(R.id.callCombNumBtn);
        callCombNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btnIntent = new Intent(getApplicationContext(), SeeCombNumListView.class);
                startActivity(btnIntent);
            }
        });  //???????????? ???????????? ?????? ????????? ????????? ?????????
    }

    public void checkFirstRun() {
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            Intent intent = new Intent(getApplicationContext(), Guide.class);
            startActivity(intent);
        }
    }  //??? ??? ?????? ??? isFirstRun??? true?????? ????????? ?????? ?????????

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }  //???????????? ?????? ?????? ???
}

class ChangeItems{
    void selectItems(Button[] numBtn, int finalI, Context context){
        if (finalI<10){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ball_shape_yellow));
                numBtn[finalI].setTextColor(Color.WHITE);
            } else {
                numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ball_shape_yellow));
                numBtn[finalI].setTextColor(Color.WHITE);
            }
        }
        else if (9<finalI && finalI<20){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ball_shape_blue));
                numBtn[finalI].setTextColor(Color.WHITE);
            } else {
                numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ball_shape_blue));
                numBtn[finalI].setTextColor(Color.WHITE);
            }
        }
        else if (19<finalI && finalI<30){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ball_shape_red));
                numBtn[finalI].setTextColor(Color.WHITE);
            } else {
                numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ball_shape_red));
                numBtn[finalI].setTextColor(Color.WHITE);
            }
        }
        else if (29<finalI && finalI<40){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ball_shape_gray));
                numBtn[finalI].setTextColor(Color.WHITE);
            } else {
                numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ball_shape_gray));
                numBtn[finalI].setTextColor(Color.WHITE);
            }
        }
        else if (39<finalI && finalI<45){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ball_shape_green));
                numBtn[finalI].setTextColor(Color.WHITE);
            } else {
                numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ball_shape_green));
                numBtn[finalI].setTextColor(Color.WHITE);
            }
        }
    }

    void outItems(Button[] numBtn, int finalI, Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24));
            numBtn[finalI].setTextColor(Color.BLACK);
        } else {
            numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24));
            numBtn[finalI].setTextColor(Color.BLACK);
        }
    }

    void initialItems(Button[] numBtn, int finalI, Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            numBtn[finalI].setBackground(ContextCompat.getDrawable(context, R.drawable.ball_shape));
            numBtn[finalI].setTextColor(Color.WHITE);
        } else {
            numBtn[finalI].setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ball_shape));
            numBtn[finalI].setTextColor(Color.WHITE);
        }
    }
}  //????????? ?????? ??? ??? ??????