package com.example.mydiary2;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.realm.Realm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeDiaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeDiaryFragment extends Fragment {
    Button buttonDate;
    Button buttonSave;
    Button buttonMakeTag;
    Spinner spinnerWeather;
    Spinner spinnerTag;
    TextInputEditText editTitle;
    TextInputEditText editBodyText;

    int year;
    int month;
    int weather = 0;
    int tag = 0;
    String date;

    private DiaryListFragment.OnFragmentInteractionListener mLis;
    private Realm realm;

    public MakeDiaryFragment() {
        // Required empty public constructor
    }
    public static MakeDiaryFragment newInstance(String param1, String param2) {
        MakeDiaryFragment fragment = new MakeDiaryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //リアムデータベース取得
        realm = Realm.getDefaultInstance();

        //日付ボタンに今日の日付をセット
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        date = String.format("%d年%02d月%02d日",  calendar.get(Calendar.YEAR),  calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //各ビューのインスタンスを取得
        View view = inflater.inflate(R.layout.fragment_make_diary, container, false);
        buttonDate = view.findViewById(R.id.buttonDate);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonMakeTag = view.findViewById(R.id.buttonMakeTag);
        spinnerTag = view.findViewById(R.id.spinnerTag);
        spinnerWeather = view.findViewById(R.id.spinnerWeather);
        editBodyText = view.findViewById(R.id.editBodyText);
        editTitle = view.findViewById(R.id.editTitle);

        // 日付ボタンに日付セット
        buttonDate.setText(date);
        //日付ボタンにリスナーセット、押すとカレンダーを起動
        buttonDate.setOnClickListener(new onButtonDateListener());

        //タグ作成ボタンにリスナーセット
        buttonMakeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Fragment fragment = new MakeTagFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.baseFrameLayout,fragment,"MakeTagFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //保存ボタンにリスナーセット、押すと保存
        buttonSave.setOnClickListener(new onButtonSaveListener());

        //天気スピナーにリスナーをセット
        spinnerWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                weather = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //タグスピナーにリスナーをセット
        String[] array = realm.where(System.class).equalTo("id",1).findAll().first().tag.split(",",-1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, array);
        spinnerTag.setAdapter(adapter);
        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tag = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return view;

    }
    //日付ボタンリスナー
    private class onButtonDateListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Calendarインスタンスを取得
            final Calendar finalCalender = Calendar.getInstance();
            //DatePickerDialogインスタンスを取得
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year2, int month2, int dayOfMonth) {
                            //setした日付を取得して表示
                            date = String.format("%d年%02d月%02d日", year2, month2 + 1, dayOfMonth);
                            year = year2;
                            month = month2;
                            buttonDate.setText(date);
                        }
                    },
                    finalCalender.get(Calendar.YEAR),
                    finalCalender.get(Calendar.MONTH),
                    finalCalender.get(Calendar.DATE)
            );
            //dialogを表示
            datePickerDialog.show();
        }
    }
    //保存ボタンリスナー
    private class onButtonSaveListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                   // Diary diary = realm.createObject(Diary.class,"djiaas");
                    Diary diary = realm.where(Diary.class).equalTo("date",date).findFirst();

                    if(diary==null){
                        diary = realm.createObject(Diary.class,date);
                    }
                    diary.title =editTitle.getText().toString();
                    diary.bodyText = editBodyText.getText().toString();
                    diary.year = year;
                    diary.month = month;
                    diary.tag = tag;
                    diary.weather = weather;

                }
            });
            //Toast.makeText(getActivity(),"保存しました",Toast.LENGTH_LONG);
            Snackbar.make(view,"保存しました",Snackbar.LENGTH_SHORT).show();
        }
    }



}