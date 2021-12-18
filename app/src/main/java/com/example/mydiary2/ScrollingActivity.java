package com.example.mydiary2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.realm.Realm;
import io.realm.RealmResults;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class ScrollingActivity extends AppCompatActivity implements DiaryListFragment.OnFragmentInteractionListener{

    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerTag;
    Button buttonDiaryListShow;
    Button buttonMakeDiary;


    int year;
    int month;

    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        //ツールバー作成
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        realm = Realm.getDefaultInstance();

        spinnerTag = findViewById(R.id.spinnerTag);

        //タグデータがあるかチェック
        createRealm();


        //最初のフラグメント配置
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = new DiaryListFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.baseFrameLayout,fragment,"DiaryListShowFragment");
        transaction.commit();

        //日付取得
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;

        //スピナー取得と今現在の年月にセット
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear.setSelection(year-2020);
        spinnerMonth.setSelection(month);

        //日記作成ボタンと日記リスト表示ボタン取得とそのリスナーセット
        buttonDiaryListShow = findViewById(R.id.button_diarylist_show);
        buttonDiaryListShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                Fragment fragment = new DiaryListFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.baseFrameLayout,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        buttonMakeDiary = findViewById(R.id.button_make_diary);
        buttonMakeDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                Fragment fragment = new MakeDiaryFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.baseFrameLayout,fragment,"MakeDiaryFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddDiarySeleted() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),"MyDialog");
    }

    void createRealm(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System system = realm.where(System.class).equalTo("id", 1).findFirst();
                //初めての起動でタグデータがrealmに無いときデータを登録
                if (system == null) {
                    system = realm.createObject(System.class, 1);
                    system.tag = "全て,1,2,3,4,5,6,7,8,9,10";
                }
                //データがあるならそのデータをタグスピナーにセット
                String[] array = realm.where(System.class).equalTo("id",1).findAll().first().tag.split(",",-1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScrollingActivity.this, R.layout.spinner, array);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                spinnerTag.setAdapter(adapter);
//                RealmResults<System> realmResults =realm.where(System.class).equalTo("id",1).findAll();
//                for(System system1:realmResults){
//                    system1.tag ="ji";
//                }

            }
        });
    }
    //リサイクルビューを短く押しした時のリスナー
    public class ListClickListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = new MakeDiaryFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.baseFrameLayout, fragment, "MakeDiaryFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public class MyDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final String[] items = {"再編集","削除"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder.setTitle("メニュー").setItems(items,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                Toast.makeText(getActivity(),items[0],Toast.LENGTH_LONG);
                            }else{
                                Toast.makeText(getActivity(),items[i],Toast.LENGTH_LONG);
                            }
                        }
                    }).create();
        }
    }
}