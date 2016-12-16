package com.example.termproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;

public class PlayTabata extends AppCompatActivity {
    TextView name;
    TextView content;
    String[] result;
    Button play, pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tabata);
        name = (TextView)findViewById(R.id.nameofmusic);
        content = (TextView)findViewById(R.id.contentofcose);
        play = (Button)findViewById(R.id.play);
        pause = (Button)findViewById(R.id.pause);

        final String cose = getIntent().getExtras().getString("Cose_Name");

        try {
                String cont = "";
                FileInputStream fis = openFileInput(cose+".txt");
                //파일 읽기 모드로 name+".txt"라는 이름의 파일을 읽어들임
                byte[] buffer = new byte[fis.available()];
                //버퍼를 생성한 후 읽기모드로 연 파일의 내용을 저장
                fis.read(buffer);//버퍼에 저장된 내용을 읽어들임
                cont = new String(buffer);
                //cont TextView에 버퍼에 저장된 내용을 출력
                String regex = "/";
                int limit = 0;
                result = cont.split(regex, limit);
                for(int i = 0; i < result.length; i++) {
                    if(i == 0)
                        name.setText(result[i]);
                    else
                        content.setText(content.getText() + " " + result[i]);
                }

                fis.close();//읽기모드를 끝냄

            } catch (IOException e) {//파일 입력 관련 API를 사용하기 때문에 IOException처리를 해준다
                e.printStackTrace();
            };


    }
}
