package com.danbai.dbysapp.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.danbai.dbysapp.R;
import com.danbai.dbysapp.activity.YsActivity;

public class YsImg extends FrameLayout {
    private ImageView img;
    private TextView text;
    private TextView yszt;
    private int ysid;
    public YsImg(@NonNull Context context, String imgurl, String pm, String zt,int id, int w) {
        super(context);
        //加载组件
        LayoutInflater.from(context).inflate(R.layout.ysimg, this);
        img = findViewById(R.id.ysimg);
        text = findViewById(R.id.yspm);
        yszt = findViewById(R.id.yszt);
        ysid=id;
        //设置数据
        ViewGroup.LayoutParams layoutParams1 = img.getLayoutParams();
        Glide.with(this).load(imgurl).into(img);
        layoutParams1.width = w;
        layoutParams1.height = (int) (w * 1.4);
        img.setLayoutParams(layoutParams1);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), YsActivity.class);
                intent.putExtra("ysid",ysid);
                v.getContext().startActivity(intent);
            }
        });
        layoutParams1 = text.getLayoutParams();
        layoutParams1.width = w;
        text.setText(pm);
        text.setLayoutParams(layoutParams1);
        if(zt!=null){
            yszt.setLayoutParams(layoutParams1);
            yszt.setText(zt);
        }else {
            yszt.setHeight(0);
        }
    }
    public YsImg(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ysimg, this);
        img = findViewById(R.id.ysimg);
        text = findViewById(R.id.yspm);
    }

    public void updateimgurl(String url) {
        Glide.with(this).load(url).into(img);
    }

    public void updatepm(String pm) {
        text.setText(pm);
    }

    public ImageView getImg() {
        return img;
    }

    public void setImg(ImageView img) {
        this.img = img;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public TextView getYszt() {
        return yszt;
    }

    public void setYszt(TextView yszt) {
        this.yszt = yszt;
    }

    public int getYsid() {
        return ysid;
    }

    public void setYsid(int ysid) {
        this.ysid = ysid;
    }
}
