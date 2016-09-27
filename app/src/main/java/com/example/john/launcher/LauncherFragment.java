package com.example.john.launcher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ZheWei on 2016/9/27.
 */
public class LauncherFragment extends Fragment {
    @BindView(R.id.rv_launcher)
    RecyclerView mRvLauncher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_launcher, container, false);
        ButterKnife.bind(this, view);
        //绑定视图
        initView();
        //
        setupAdapter();

        return view;
    }

    private void setupAdapter() {
        Intent setupIntent = new Intent(Intent.ACTION_MAIN);
        setupIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加种类
        //得到所有可以启动的应用的名字
        final PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(setupIntent, 0);
        //对可启动的应用按照名字进行排序
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(pm).toString()
                        , o2.loadLabel(pm).toString());
            }
        });
        //设置adapter
        mRvLauncher.setAdapter(new LauncherAdapter(activities));
    }

    private void initView() {
        mRvLauncher.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public class LauncherAdapter extends RecyclerView.Adapter {

        private List<ResolveInfo> activitys;

        public LauncherAdapter(List<ResolveInfo> activitys) {
            this.activitys = activitys;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new LauncherHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LauncherHolder launcherHolder = (LauncherHolder) holder;
            launcherHolder.bindResolveInfo(activitys.get(position));
        }

        @Override
        public int getItemCount() {
            return activitys.size();
        }

        private class LauncherHolder extends RecyclerView.ViewHolder {
            private TextView mTextView;
            private ResolveInfo mResolveInfo;

            public LauncherHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView;
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityInfo activityInfo = mResolveInfo.activityInfo;
                        Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName,
                                activityInfo.name);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }

            public void bindResolveInfo(ResolveInfo resolveInfo) {
                mResolveInfo = resolveInfo;
                PackageManager pm = getActivity().getPackageManager();
                String appName = resolveInfo.loadLabel(pm).toString();
                mTextView.setText(appName);
            }
        }
    }
}
