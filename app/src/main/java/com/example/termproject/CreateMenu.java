package com.example.termproject;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateMenu extends AppCompatActivity {
    Spinner spinEffort, spinMinutes, spinSeconds, spinmus;
    public ArrayAdapter<String> m_Adapter;//스트링 형태의 어레이어댑터 객체를 생성
    ArrayList<File> Music_File = new ArrayList<File>();//File형태의 어레이리스트 생성
    //저장 경로에 있는 파일들 중 mp3파일들이 저장될 리스트임
    ArrayList<String> values = new ArrayList<>();//리스트 뷰에서 보이게 될 항목
    File musicdir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    //File 변수를 생성하여 음악파일이 있는 디렉토리를 저장
    File files[] = {};//밑에서 다시한번 디렉토리의 파일을 읽어들일 필요가 있으므로 선언
    public final File[] FILELIST = musicdir.listFiles();//위의 저장된 항목들의 리스트를 File배열을 생성해 저장시켜줌
    TextView muname;
    ArrayList<String> spinnerlistEffort = new ArrayList<String>();
    ArrayList<String> spinnerlistMinutes = new ArrayList<String>();
    ArrayList<String> spinnerlistSeconds = new ArrayList<String>();
    Button add_exer;
    Button Save;

    TextView exercose;
    String Effort;
    String Minutes;
    String Seconds;
    int num = 0;
    EditText cose_name;
    EditText exer_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        muname = (TextView)findViewById(R.id.musicname);
        exercose= (TextView)findViewById(R.id.exer_cose);
        spinmus = (Spinner)findViewById(R.id.spin1);
        spinEffort = (Spinner)findViewById(R.id.Effort_edit_effort_level);
        spinMinutes = (Spinner)findViewById(R.id.exercise_edit_minutes);
        spinSeconds = (Spinner)findViewById(R.id.exercise_edit_seconds);
        add_exer = (Button)findViewById(R.id.exeradd);
        Save = (Button)findViewById(R.id.save);
        cose_name = (EditText)findViewById(R.id.cosename);
        exer_name = (EditText) findViewById(R.id.exercise_edit_name);

        spinnerlistEffort.add("HARD");
        spinnerlistEffort.add("EASY");
        spinnerlistEffort.add("REST");

        for(int i= 0; i<24; i++){
            spinnerlistMinutes.add(""+i);
        }
        for(int i= 0; i<60; i++){
            spinnerlistSeconds.add(""+i);
        }

        ArrayAdapter<String> adap1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlistEffort);
        spinEffort.setAdapter(adap1);

        spinEffort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Effort = spinnerlistEffort.get(position);
                //exercose.setText(exercose.getText() + Effort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
/*
        add_exer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercose.setText(exercose.getText() + "(exer: " + Effort + ")/" );
            }
        });
        */

        ArrayAdapter<String> adap2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlistMinutes);
        spinMinutes.setAdapter(adap2);

        spinMinutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Minutes = spinnerlistMinutes.get(position);
                //exercose.setText(exercose.getText() + Effort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
/*
        add_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercose.setText(exercose.getText() + "(rest: " + rest + ")/" );
            }
        });

*/

        ArrayAdapter<String> adap3 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlistSeconds);
        spinSeconds.setAdapter(adap3);

        spinSeconds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Seconds = spinnerlistSeconds.get(position);
                //exercose.setText(exercose.getText() + Effort);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        /*
        add_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercose.setText(exercose.getText() + "(rest: " + rest + ")/" );
            }
        });

*/

        m_Adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, values);
        spinmus.setAdapter(m_Adapter);
        int num = 0;//인트형 변수를 하나 선언하여 초기화
        files = musicdir.listFiles();//위에서 선언했던 files 배열에 디렉토리에 저장되어 있는 파일들을 저장한다
        num = files.length;//배열의 길이를 알아낸다
        // 아래 코드는 files 배열의 길이만큼 for 루프를 돈다
        for (int i = 0; i < num; i++) {
            if(FILELIST[i].getName().endsWith(".mp3") || FILELIST[i].getName().endsWith(".MP3") )//FILELIST의 i번째 원소의 파일 이름의 마지막이
                //.mp3 혹은 .MP3로 끝나는 경우
                Music_File.add(FILELIST[i]);//Music_File리스트에 추가시켜준다.
        }

        for(File file : Music_File) {
            String f_name;//파일의 이름이 저장될 String형 변수를 선언 후
            f_name = file.getName().substring(0, file.getName().length()-4) ;
            //확인중인 파일의 .mp3를 제외한 이름을 저장시킨 뒤
            m_Adapter.add(f_name);//어댑터 객체를 사용해 리스트 뷰에 추가시켜준다
        }

        spinmus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String musicname = files[position].getName().substring(0, files[position].getName().length()-4);
                muname.setText("(music name: " + musicname + ")/");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //추가는 effort duration exer_name에 관해서만해주고
        add_exer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercose.setText(
                        exercose.getText() +"["+Effort +"/" +Minutes +":"+ Seconds + "/" + exer_name.getText().toString() +"]");

            }
        });
        //저장 제목을 :cose_name 내용을 음악 + [운동 정보에 대해 넣어준다]
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cose_check = cose_name.getText().toString();
                String name_check = exer_name.getText().toString();

                if(cose_check.equals("") || name_check.equals(""))
                    Toast.makeText(CreateMenu.this, "코스가 없음", Toast.LENGTH_SHORT).show();
                else {
                    FileOutputStream fos;
                    //쓰기 파일을 열기 위한 FileOutputStream 객체를 생성
                    try {
                        fos = openFileOutput(cose_name.getText().toString() + ".txt", Context.MODE_PRIVATE);
                        //nameofmemo에 저장된 이름+.txt를 통해 텍스트 파일을 만들어주고
                        //나만 사용가능한 모드로 설정해준다.
                        fos.write(muname.getText().toString().getBytes());
                        fos.write(exercose.getText().toString().getBytes());
                        //fos.write()
                        //생성한 텍스트 파일에 contentofmemo에 적혀있는 글들을 입력해준다.
                        fos.close();//입력을 마쳤으므로 쓰기 파일을 종료시킨다.
                    } catch (IOException e) {//파일 출력 관련 API를 사용하기 때문에 IOException처리를 해준다
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
