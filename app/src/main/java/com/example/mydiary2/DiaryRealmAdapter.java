package com.example.mydiary2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class DiaryRealmAdapter extends RealmRecyclerViewAdapter<Diary, DiaryRealmAdapter.DiaryViewHolder> {

    Context context;
    static String[] arrayTagString = {"タグなし","１","２","３","４","５","６","７","８","９","１０"};

    private Realm realm;

    //ビューホルダー
    public  static  class  DiaryViewHolder extends RecyclerView.ViewHolder{
        protected TextView textTitle;
        protected TextView textDate;
        protected TextView textTag;
        protected TextView textBodyText;
        protected ImageView imageView;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textCardTitle);
            textDate = itemView.findViewById(R.id.textCardDate);
            textTag = itemView.findViewById(R.id.textCardTag);
            textBodyText = itemView.findViewById(R.id.textCardBody);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    //コンストラクター
    public DiaryRealmAdapter(@Nullable Context context,Realm realm,OrderedRealmCollection<Diary> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
        this.realm = realm;
        String[] array = realm.where(System.class).equalTo("id",1).findAll()
                .first().tag.split(",",-1);
        this.arrayTagString = array;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        final DiaryViewHolder holder = new DiaryViewHolder(itemView);

        //短くリサイクルビューをタップした時のリスナーをセット
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //何番目のホルダー（カードビュー）か取得
                final int position = holder.getAdapterPosition();

                Intent intent = new Intent(context,ShowDiaryActivity.class);
                context.startActivity(intent);
            }
        });
        //長くリサイクルビューをタップした時のリスナーをセット
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //何番目のホルダー（カードビュー）か取得
                final int position = holder.getAdapterPosition();

                MyDialog myDialog = new MyDialog(context,position,realm);
                myDialog.show(((ScrollingActivity)context).getSupportFragmentManager(), "Fragment");
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        Diary diary = getData().get(position);
        holder.textTitle.setText(diary.title);
        holder.textDate.setText(diary.date);
        holder.textTag.setText(arrayTagString[diary.tag]);
        holder.textBodyText.setText(diary.bodyText);


    }

/////////////////////////////////////リサクルビューを長押した時のダイアログ/////////////////////////////////////
    public static class MyDialog extends DialogFragment {
        private int position;
        private Context context;
        private Realm realm;

        public MyDialog(Context context,int position,Realm realm) {
            this.context = context;
            this.position = position;
            this.realm = realm;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final String[] items = {"再編集","削除"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder.setTitle("メニュー").setItems(items,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //再編集をタップ
                            if(i==0){
                                FragmentManager manager = ((ScrollingActivity)context).getSupportFragmentManager();
                                Fragment fragment = new MakeDiaryFragment();
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.baseFrameLayout, fragment, "MakeDiaryFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                            //削除をタップ
                            }else {
                                //再度削除するか確認のダイアログを作成
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setTitle("削除確認");      //タイトル設定
                                alertDialog.setMessage("この日記を削除してもよろしいですか？");  //内容(メッセージ)設定

                                // OK(肯定的な)ボタンの設定
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // OKボタン押下時の処理
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                realm.where(Diary.class).findAll().deleteFromRealm(position);
                                                Snackbar.make(((ScrollingActivity)context).findViewById(R.id.recyclerView),"削除しました",Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                // NG(否定的な)ボタンの設定
                                alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                alertDialog.show();
                            }   //if
                        }  //onClick
            }).create();
        }//onCreateDialog
    }//MyDialog
  }