package com.space.lisktop.adapters;

import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.space.lisktop.R;

import java.util.ArrayList;
import java.util.Collections;

public class TodoAdapter extends RecyclerView.Adapter {
    private ArrayList<String> todos;
    private String strTodo;

    private OnItemClickListener clickListener;     //点击事件接口

    class TodoHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;

        public TodoHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.cb_todo);
            checkBox.setChecked(false);
            setIsRecyclable(false);
        }
    }

    public TodoAdapter(ArrayList<String> data){
        todos=data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_layout,parent,false);
        TodoHolder tdHolder=new TodoHolder(v);
        tdHolder.checkBox.setChecked(false);
        return tdHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TodoHolder){
            strTodo=todos.get(position);
            ((TodoHolder) holder).checkBox.setText(strTodo);
            Log.i("recadapter","onBindViewHolder");

            /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null) {
                        int pos = holder.getLayoutPosition();
                        clickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });*/

            ((TodoHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(clickListener!=null){
                        int pos=holder.getAdapterPosition();
                        if(isChecked){
                            //delete(pos);
                            clickListener.onChecked(((TodoHolder) holder).checkBox,pos);
                        }
                    }
                }
            });

            /*holder.itemView.setLongClickable(true);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(clickListener != null) {
                        int pos = holder.getLayoutPosition();
                        clickListener.onItemLongClick(holder.itemView, pos);
                    }
                    //表示此事件已经消费，不会触发单击事件
                    return true;
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        return todos==null ? 0: todos.size();
    }

    public void addNewItem(String str1) {
        if(todos == null) {
            todos = new ArrayList<>();
        }
        todos.add(0, str1);
        notifyItemInserted(0);
    }

    public String delete(int position) {
        if(todos == null || todos.isEmpty()) {
            return null;
        }
        String ret=todos.get(position);
        todos.remove(position);
        notifyItemRemoved(position);
        return ret;
    }


    public void move(int fromPosition, int toPosition) {
        //String prev = todos.remove(fromPosition);
        //todos.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        Collections.swap(todos,fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

        /**
         * 设置回调监听
         *
         * @param listener
         */
    public void setOnItemClickListener(TodoAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onChecked(View view, int position);
    }
}
