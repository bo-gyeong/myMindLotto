package com.myMindLotto.lottoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lottoapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements Serializable {

    Button f5Btn, saveRandomNumBtn;
    ListView mainListView;

    Intent intent;
    int[] lottoNum = new int[6];
    ArrayList<Integer> selNumsList = new ArrayList<>();
    ArrayList<Integer> outNumsList = new ArrayList<>();
    boolean isMixed;

    SQLiteDatabase sqlDB;
    LottoNumDB lottoNumDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.splashTitleTextView));

        f5Btn = (Button) findViewById(R.id.F5Btn);
        saveRandomNumBtn = (Button) findViewById(R.id.saveRandomNumBtn);

        ImageView[] numImg = new ImageView[6];
        Integer[] numImgId = new Integer[]{R.id.num1ImgView, R.id.num2ImgView
                , R.id.num3ImgView, R.id.num4ImgView, R.id.num5ImgView, R.id.num6ImgView};

        TextView[] numTxt = new TextView[6];
        Integer[] numTxtId = new Integer[]{R.id.num1TextView, R.id.num2TextView
                , R.id.num3TextView, R.id.num4TextView, R.id.num5TextView, R.id.num6TextView};

        for (int i=0; i<numImg.length; i++) {
            numImg[i] = (ImageView) findViewById(numImgId[i]);
            numTxt[i] = (TextView) findViewById(numTxtId[i]);
        }

        intent= getIntent();
        if (intent.getExtras() != null){
            selNumsList = intent.getIntegerArrayListExtra("selNums");
            outNumsList = intent.getIntegerArrayListExtra("outNums");
            isMixed = intent.getBooleanExtra("isMixed", false);
        }  //SelectNums ??????????????? ?????? ??????????????? ??????

        makeRandomNum();

        SetColor setColor = new SetColor();
        for (int i=0; i<numImg.length; i++){
            numTxt[i].setText(String.valueOf(lottoNum[i]));
            setColor.changeColor(numImg, i, Integer.parseInt(numTxt[i].getText().toString()));
        }  //??? ??? ?????? ??? ???????????? ????????? ?????? ??? ??? ??????

        f5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRandomNum();
                for (int i=0; i<numImg.length; i++){
                    numTxt[i].setText(String.valueOf(lottoNum[i]));
                    setColor.changeColor(numImg, i, Integer.parseInt(numTxt[i].getText().toString()));
                }
            }
        });  //???????????? ??? ???????????? ????????? ?????? ??? ??? ??????

        ArrayList<MainItem> mainItems = new ArrayList<>();
        String[] listItemArr = {getString(R.string.selectNBtn), getString(R.string.PrizeNumBtn)
                , getString(R.string.QRScanBtn), getString(R.string.seeRandomNumBtn)};
        int[] listIconArr = {R.drawable.ic_baseline_shuffle_24, R.drawable.ic_baseline_fact_check_24
                            , R.drawable.ic_baseline_qr_code_scanner_24, R.drawable.ic_baseline_dehaze_24};

        for (int i=0; i<listItemArr.length; i++){
            MainItem mainItem = new MainItem();

            mainItem.listItemTextView = listItemArr[i];
            mainItem.listIconImgView = listIconArr[i];

            mainItems.add(mainItem);
        }

        MakeMainList makeMainList = new MakeMainList(mainItems);

        mainListView = findViewById(R.id.mainListView);
        mainListView.setAdapter(makeMainList);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent listIntent;
                switch (position){
                    case 0:
                        listIntent = new Intent(getApplicationContext(), SelectNums.class);
                        startActivity(listIntent);
                        break;
                    case 1:
                        listIntent = new Intent(getApplicationContext(), PrizeNums.class);
                        startActivity(listIntent);
                        break;
                    case 2:
                        listIntent = new Intent(getApplicationContext(), QRWebView.class);
                        startActivity(listIntent);
                        break;
                    case 3:
                        listIntent = new Intent(getApplicationContext(), SeeLottoNumListView.class);
                        startActivity(listIntent);
                }
            }
        });

        lottoNumDB = new LottoNumDB(this);
        saveRandomNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String allLottoNum = lottoNum[0] + "-" + lottoNum[1] + "-"+ lottoNum[2]
                        + "-"+ lottoNum[3] + "-"+ lottoNum[4] + "-" + lottoNum[5];

                sqlDB = lottoNumDB.getWritableDatabase();
                //lottoNumDB.onUpgrade(sqlDB, 1, 2);
                Cursor cursor = null;
                try {
                    cursor = sqlDB.rawQuery("SELECT allLottoNum FROM lottoNum WHERE allLottoNum = " + allLottoNum + ";", null);
                }catch (Exception e){
                    e.printStackTrace();
                }  // ????????? ????????? ?????? lottoNum ???????????? ????????? ??????

                String allNum = null;
                if (cursor != null){
                    while (cursor.moveToNext()) {
                        allNum = cursor.getString(0);
                    }
                }  // cursor??? ?????? ????????? allNum??? ??????

                if (allNum != null){
                    Toast.makeText(getApplicationContext(), "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                } else{
                    sqlDB.execSQL("INSERT INTO lottoNum VALUES (" + allLottoNum + ", " + lottoNum[0] + ", "
                            + lottoNum[1] + ", " + lottoNum[2] + ", " + lottoNum[3] + ", "
                            + lottoNum[4] + ", " + lottoNum[5] + ");");

                    Toast.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                } //allNum??? ?????? ??????????????? ?????? ??? ?????? ?????? ?????? ??????
                sqlDB.close();
            }
        });
    }

    void makeRandomNum(){
        MixNums mixNums = new MixNums();

        if (intent.getExtras() == null){
            mixNums.original();
        }
        else if (isMixed){
            mixNums.mixComb(selNumsList, outNumsList);
        }
        else{
            mixNums.onlySelectComb(selNumsList, outNumsList);
        }

        Arrays.sort(lottoNum);
    }  //SelectNums ??????????????? ????????? ?????? ?????? ?????? ??????

    class MixNums{
        void original(){
            for (int i = 0; i < lottoNum.length; i++) {
                lottoNum[i] = (int) (Math.random() * 45 + 1);
                for (int j = 0; j < i; j++) {
                    if (lottoNum[j] == lottoNum[i]) i--;
                }
            }
        }  //45??? ????????? ?????? ?????? ??????

        void onlySelectComb(ArrayList<Integer> selNumsList, ArrayList<Integer> outNumsList){
            Collections.shuffle(selNumsList);
            int k = 0;
            for (int i = 0; i < lottoNum.length; i++, k++) {
                if (selNumsList.size() > k){
                    lottoNum[i] = selNumsList.get(k);
                } else{
                    lottoNum[i] = (int) (Math.random() * 45 + 1);
                }
                if (outNumsList.contains(lottoNum[i])) {
                    i--;
                    continue;
                }

                for (int j = 0; j < i; j++) {
                    if (lottoNum[j] == lottoNum[i]) i--;
                }
            }
        }  //????????? ??????????????? ?????? ?????? ??????(????????? ????????? 5??? ????????? ??? ????????? ????????? ????????? ???????????? ??????)

        void mixComb(ArrayList<Integer> selNumsList, ArrayList<Integer> outNumsList){
            Collections.shuffle(selNumsList);
            int k = 0;
            boolean isContain = false;

            for (int i = 0; i < lottoNum.length; i++, k++) {
                int randomChoice = (int) (Math.random() * 45);
                randomChoice = randomChoice % 3;

                if (selNumsList.size()>k && randomChoice==1){
                    lottoNum[i] = selNumsList.get(k);
                } else {
                    lottoNum[i] = (int) (Math.random() * 45 + 1);
                }
                if (outNumsList.contains(lottoNum[i])) {
                    i--;
                    continue;
                }

                for (int j = 0; j < i; j++) {
                    if (lottoNum[j] == lottoNum[i]) i--;
                    if (i==lottoNum.length-1 && selNumsList.size()>0){
                        if (selNumsList.contains(lottoNum[j]) || selNumsList.contains(lottoNum[i])){
                            isContain = true;
                        }
                        if (j==lottoNum.length-2 && !isContain){
                            lottoNum[i] = selNumsList.get(0);
                        }
                    }
                }
            }
        }  //???????????????+??????????????? ??????
    } //original() ????????? ???????????? ??????????????? ???????????? ??????
}

class SetColor{
    void changeColor(ImageView[] numImg, int i, int num){
        if (num<11){
            numImg[i].setImageResource(R.drawable.ball_shape_yellow);
        }
        else if (num<21){
            numImg[i].setImageResource(R.drawable.ball_shape_blue);
        }
        else if (num<31){
            numImg[i].setImageResource(R.drawable.ball_shape_red);
        }
        else if (num<41){
            numImg[i].setImageResource(R.drawable.ball_shape_gray);
        }
        else if (num<46){
            numImg[i].setImageResource(R.drawable.ball_shape_green);
        }
    }
}  //????????? ?????? ?????? ??????