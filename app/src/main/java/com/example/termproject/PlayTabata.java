package com.example.termproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class PlayTabata extends AppCompatActivity {
    TextView musicname;
    TextView content;
    TextView cname;
    String[] result;

    long count=0;

    private boolean isPaused = false;
    private boolean isCanceled = false;

    long PreE = 0;
    long first =0;
    int N=3;
    int firstCount =0;
    private TextView countTxt;
    private CountDownTimer countDownTimer;
    private long timeRemaining = 0;
    private long timepartRemaining =0;
    private static final int MILLISINFUTURE = 5 * 1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;//고정값


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tabata);
        musicname = (TextView) findViewById(R.id.nameofmusic);
        content = (TextView) findViewById(R.id.contentofcose);
        countTxt = (TextView) findViewById(R.id.count_txt);
        final String cose = getIntent().getExtras().getString("Cose_Name");
        final TextView tView = (TextView) findViewById(R.id.tv);
        final Button btnStart = (Button) findViewById(R.id.btn_start);
        final Button btnPause = (Button) findViewById(R.id.btn_pause);
        final Button btnResume = (Button) findViewById(R.id.btn_resume);
        final Button btnCancel = (Button) findViewById(R.id.btn_cancel);


        //Initially disabled the pause, resume and cancel button
        btnPause.setEnabled(false);
        btnResume.setEnabled(false);
        btnCancel.setEnabled(false);
//        cname.setText("??");
        try {
            String cont = "";
            FileInputStream fis = openFileInput(cose + ".txt");
            //파일 읽기 모드로 name+".txt"라는 이름의 파일을 읽어들임
            byte[] buffer = new byte[fis.available()];
            //버퍼를 생성한 후 읽기모드로 연 파일의 내용을 저장
            fis.read(buffer);//버퍼에 저장된 내용을 읽어들임
            cont = new String(buffer);
            //cont TextView에 버퍼에 저장된 내용을 출력


            result = cont.split("/");
            for (int i = 0; i < result.length; i++) {
                if (i == 0)
                    musicname.setText(result[i]);
                else
                    content.setText(content.getText() + " " + result[i]);
            }
            //result[0]에는 노래 제목이 들어가 있고
            //result[1+4n]에는 effort
            //result[2+4n]에는 Minutees
            //result[3+4n]에는 Seconds
            //result[4+4n]에는 Name이 들어간다
            for(int i = 3; i < result.length; i=i+4) {
                firstCount += parseInt(result[i]);
                //걸리는 시간의 총합을 구해준다
            }


            for (int i = 0; i < result.length; i = i + 4) {
                /*
                result[1+4*i]
                result[2+4*i]
                result[3+4*i]
                result[4+4*i]
                */
            }

            fis.close();//읽기모드를 끝냄

        } catch (IOException e) {//파일 입력 관련 API를 사용하기 때문에 IOException처리를 해준다
            e.printStackTrace();
        };


        //Set a Click Listener for start button
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                isPaused = false;
                isCanceled = false;

                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enabled the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);
                CountDownTimer timer;
                final long millisInFuture = (firstCount)*1000;

                long countDownInterval = 1000; //1 second
                first=millisInFuture;
                count= parseInt(result[3])*1000;
                N=3;
                PreE= parseInt(result[3])*1000;
                //Initialize a new CountDownTimer instance
                timer = new CountDownTimer(millisInFuture,countDownInterval){
                    public void onTick(long millisUntilFinished){



                        if((first-millisUntilFinished) >= PreE){
                            N=N+4;
                            if(N<result.length)
                                count= parseLong(result[N])*1000;
                            PreE=PreE+count;
                        }



                        if(isPaused || isCanceled)
                        {
                            //If the user request to cancel or paused the
                            //CountDownTimer we will cancel the current instance
                            cancel();
                        }
                        else {
                            //Display the remaining seconds to app interface
                            //1 second = 1000 milliseconds


                            tView.setText("" + millisUntilFinished / 1000);
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                            timepartRemaining = timeRemaining - (first - PreE) ;
                            countTxt.setText(""+timepartRemaining / 1000);


                        }
                    }
                    public void onFinish(){
                        //Do something when count down finished
                        tView.setText("Done");
                        countTxt.setText(String.valueOf("Finish ."));

                        //Enable the start button
                        btnStart.setEnabled(true);
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                    }
                }.start();

            }
        });

        //Set a Click Listener for pause button
        btnPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //When user request to pause the CountDownTimer
                isPaused = true;

                //Enable the resume and cancel button
                btnResume.setEnabled(true);
                btnCancel.setEnabled(true);
                //Disable the start and pause button
                btnStart.setEnabled(false);
                btnPause.setEnabled(false);
            }
        });

        //Set a Click Listener for resume button
        btnResume.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Disable the start and resume button
                btnStart.setEnabled(false);
                btnResume.setEnabled(false);
                //Enable the pause and cancel button
                btnPause.setEnabled(true);
                btnCancel.setEnabled(true);

                //Specify the current state is not paused and canceled.
                isPaused = false;
                isCanceled = false;

                //Initialize a new CountDownTimer instance
                long millisInFuture = timeRemaining;

                long countDownInterval = 1000;
                new CountDownTimer(millisInFuture, countDownInterval){
                    public void onTick(long millisUntilFinished){

                        if((first-millisUntilFinished) >= PreE){
                            N=N+4;
                            if(N<result.length)
                                count= parseLong(result[N])*1000;
                            PreE=PreE+count;
                        }

                        //Do something in every tick
                        if(isPaused || isCanceled)
                        {
                            //If user requested to pause or cancel the count down timer
                            cancel();
                        }
                        else {
                            tView.setText("" + millisUntilFinished / 1000);
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                            timepartRemaining = PreE -(first-timeRemaining) ;
                            countTxt.setText(""+timepartRemaining / 1000);
                        }
                    }
                    public void onFinish(){
                        //Do something when count down finished
                        tView.setText("Done");
                        countTxt.setText(String.valueOf("Finish ."));
                        //Disable the pause, resume and cancel button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        //Enable the start button
                        btnStart.setEnabled(true);
                    }
                }.start();

                //Set a Click Listener for cancel/stop button
                btnCancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //When user request to cancel the CountDownTimer
                        isCanceled = true;

                        //Disable the cancel, pause and resume button
                        btnPause.setEnabled(false);
                        btnResume.setEnabled(false);
                        btnCancel.setEnabled(false);
                        //Enable the start button
                        btnStart.setEnabled(true);

                        //Notify the user that CountDownTimer is canceled/stopped
                        tView.setText("CountDownTimer Canceled/stopped.");
                    }
                });
            }
        });

        //Set a Click Listener for cancel/stop button
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //When user request to cancel the CountDownTimer
                isCanceled = true;

                //Disable the cancel, pause and resume button
                btnPause.setEnabled(false);
                btnResume.setEnabled(false);
                btnCancel.setEnabled(false);
                //Enable the start button
                btnStart.setEnabled(true);

                //Notify the user that CountDownTimer is canceled/stopped
                tView.setText("CountDownTimer Canceled/stopped.");
            }
        });


    }
/*
    public void countDownTimer() {
        //메인에서 인텐트로 (전체시간+1) * 1000]을 전달하자
        countDownTimer = new CountDownTimer((firstCount+1)*1000 , COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                count--;
                countTxt.setText(String.valueOf(count));
                if(count==0 ){
                    N=N+4;
                    if(N<result.length)
                        count= parseInt(result[N]);
                }
            }


            public void onFinish() {
                countTxt.setText(String.valueOf("Finish ."));
            }
        };

    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }

}
