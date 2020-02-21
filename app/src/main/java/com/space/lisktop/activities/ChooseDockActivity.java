package com.space.lisktop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.space.lisktop.adapters.ChooserAdapter;
import com.space.lisktop.views.Dock;
import com.space.lisktop.MainActivity;
import com.space.lisktop.R;
import com.space.lisktop.adapters.AppsLvAdapter;
import com.space.lisktop.obj.AppInfo;
import com.space.lisktop.utility.LisktopDAO;
import com.space.lisktop.utility.PackageManageHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChooseDockActivity extends AppCompatActivity  implements View.OnClickListener {
    private ListView lvApps;
    private Button btOK;
    private ArrayList<AppInfo> apps,selApps=new ArrayList<>();
    private AppsLvAdapter adapter;
    private int numSelected=0;
    private LisktopDAO lisktopDAO;

    private RecyclerView recSeldApps;
    private ChooserAdapter chsAdapter;
    private RecyclerView.LayoutManager layManager;
    private ItemTouchHelper recTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_main);
        setTitle(R.string.chser_title);

        lisktopDAO=new LisktopDAO(this);
        apps=new PackageManageHelper(this).getStartableApps(true);

        //selApps=lisktopDAO.getDockApps();
        //Log.i("remove","len-apps:"+apps.size()+",selapps:"+selApps.size());
        //apps.removeAll(selApps);
        //Log.i("remove","len-apps:"+apps.size()+",selapps:"+selApps.size());
        initViews();
    }

    private void initViews()
    {
        btOK=findViewById(R.id.bt_choose_ok);
        lvApps=findViewById(R.id.lv_choose);

        recSeldApps=findViewById(R.id.chsed_apps_recyclerview);
        recSeldApps.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        chsAdapter=new ChooserAdapter(selApps);
        ItemTouchHelper.Callback callback=new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT | ItemTouchHelper.DOWN | ItemTouchHelper.UP, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Collections.swap(selApps,viewHolder.getAdapterPosition(),target.getAdapterPosition());
                chsAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            /**
             * Item被选中时候，改变Item的背景
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                //  item被选中的操作
                if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.parseColor("#aaaaaa"));
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 移动过程中重新绘制Item，随着滑动的距离，设置Item的透明度
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                float x = Math.abs(dX) + 0.5f;
                float width = viewHolder.itemView.getWidth();
                float alpha = 1f - x / width;
                viewHolder.itemView.setAlpha(alpha);
                //viewHolder.itemView.setScaleX(1.2f);
                //viewHolder.itemView.setScaleY(1.2f);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }

            /**
             * 用户操作完毕或者动画完毕后调用，恢复item的背景和透明度
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // 操作完毕后恢复颜色
                viewHolder.itemView.setBackgroundColor(Color.WHITE);
                viewHolder.itemView.setAlpha(1.0f);
                //viewHolder.itemView.setScaleX(1.0f);
                //viewHolder.itemView.setScaleY(1.0f);
                super.clearView(recyclerView, viewHolder);
            }
        };
        recTouchHelper=new ItemTouchHelper(callback);
        recTouchHelper.attachToRecyclerView(recSeldApps);
        chsAdapter.setOnIconClickListener(new ChooserAdapter.IconClickListener() {
            @Override
            public void OnIconClicked(int index) {
                AppInfo appToDel=selApps.get(index);

                selApps.remove(index);
                chsAdapter.notifyItemRemoved(index);

                apps.add(appToDel);
                Collections.sort(apps, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo o1, AppInfo o2) {
                        return String.CASE_INSENSITIVE_ORDER.compare(o1.getAppName(),o2.getAppName());
                    }
                });
                adapter.notifyDataSetChanged();

                numSelected-=1;
            }
        });
        recSeldApps.setAdapter(chsAdapter);

        btOK.setOnClickListener(this);

        // 显示所有可启动应用
        adapter=new AppsLvAdapter(apps,this);
        lvApps.setAdapter(adapter);
        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (numSelected<5)
                {
                    AppInfo ckApp=apps.get(position);

                    selApps.add(ckApp);
                    chsAdapter.notifyItemInserted(position);

                    apps.remove(ckApp);
                    adapter.notifyDataSetChanged();
                    numSelected+=1;
                }
                else
                    Toast.makeText(ChooseDockActivity.this,"最多五个哦~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.bt_choose_ok:
                if (numSelected>0)
                {
                    lisktopDAO.cancelDockApp();
                    boolean sus=lisktopDAO.writeDockApps(selApps);
                    Log.i("write","sucsess:"+sus);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                else {
                    //Toast.makeText(this,"请选择主页应用",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
}
