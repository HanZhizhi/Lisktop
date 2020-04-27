package com.space.lisktop.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.space.lisktop.R;
import com.space.lisktop.utility.LisktopDAO;

public class TodoAdder extends DialogFragment implements View.OnClickListener {
    private Button btnAdd;
    private EditText etInput;

    private LisktopDAO lisktopDAO;
    private Window win;

    public interface AddListener{
        void onAddBtnClicked(String todo_text);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lisktopDAO=new LisktopDAO(getActivity());

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_todo_adder,null);

        btnAdd=view.findViewById(R.id.btn_add_todo);
        btnAdd.setOnClickListener(this);
        etInput=view.findViewById(R.id.et_dlg_input);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        win = getDialog().getWindow();
        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        win.setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
        /*// 一定要设置Background，如果不设置，window属性设置无效
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );

        WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width =  ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);*/
    }

    @Override
    public void onResume() {
        super.onResume();
        etInput.setFocusable(true);
        etInput.setFocusableInTouchMode(true);
        etInput.requestFocus();

        win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_todo:
                String todoInfo=etInput.getText().toString();
                lisktopDAO.insertTodo(todoInfo);

                AddListener listener= (AddListener) getTargetFragment();
                listener.onAddBtnClicked(etInput.getText().toString());
                dismiss();
                break;
        }
    }


}
