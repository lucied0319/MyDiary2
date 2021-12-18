package com.example.mydiary2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiaryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiaryListFragment extends Fragment implements View.OnCreateContextMenuListener {

    public OnFragmentInteractionListener mLis;
    private Realm realm;

    //private RecyclerView recyclerView;

    public DiaryListFragment() {
        // Required empty public constructor
    }


    public static DiaryListFragment newInstance(String param1, String param2) {
        DiaryListFragment fragment = new DiaryListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //リアムデータベース取得
        realm = Realm.getDefaultInstance();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        RealmResults<Diary> realmResults = realm.where(Diary.class).findAll();

        DiaryRealmAdapter adapter = new DiaryRealmAdapter(getActivity(),realm,realmResults,true);
        recyclerView.setAdapter(adapter);

        //registerForContextMenu(recyclerView);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener){
            mLis = (OnFragmentInteractionListener)context;
        }else {
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLis = null;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

//        MenuInflater inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.menu_nagaosi,menu);
//        menu.setHeaderTitle("メニュー");

            menu.add(Menu.NONE, 1, Menu.NONE, "コピー");
            menu.add(Menu.NONE, 2, Menu.NONE, "編集");
            menu.add(Menu.NONE, 3, Menu.NONE, "削除");


    }

    public interface OnFragmentInteractionListener{
        void onAddDiarySeleted();
    }



    //リサイクルビューを長押しした時のリスナー
    public class ListLongClickListener implements AdapterView.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {

            MyDialog dialog = new MyDialog();
            dialog.show(getFragmentManager(),"MyDialog");
            //Toast.makeText(getActivity(),"djoajoe",Toast.LENGTH_LONG);
            return true;
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