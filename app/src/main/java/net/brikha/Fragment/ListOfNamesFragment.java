package net.brikha.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.brikha.Model.BabyName;
import net.brikha.R;

import java.util.List;

public class ListOfNamesFragment extends Fragment {

    public static List<BabyName> babyNameList, mbabyNameList, fbabyNameList,historybabyNameList;

    public static OnListClickListener mListClickListener;

    public interface OnListClickListener{
        void OnListSelected(int position, int fragmentNumber);
    }

    public ListOfNamesFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListClickListener = (OnListClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_baby_name,container,false);

        return rootView;
    }
}
