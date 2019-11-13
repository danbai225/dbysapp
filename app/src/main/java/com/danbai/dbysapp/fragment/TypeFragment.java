package com.danbai.dbysapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.danbai.dbysapp.R;
import com.danbai.dbysapp.entity.Ysb;
import com.danbai.dbysapp.util.PmUtil;
import com.danbai.dbysapp.widget.YsImg;
import com.google.android.flexbox.FlexboxLayout;
import com.xwdz.http.QuietOkHttp;
import com.xwdz.http.callback.StringCallBack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class TypeFragment extends Fragment {
    private static Map<String, TypeFragment> map = new HashMap<>(4);
    private FlexboxLayout fbl;
    private View view;
    private int screenWidth;
    private int page = 1;
    private int maxpage = 2;
    private boolean inlod;
    private String type;

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    static TypeFragment newTypeFragment(String type) {
        TypeFragment view = map.get(type);
        if (view != null) {
            return view;
        }
        TypeFragment fragment = new TypeFragment();
        fragment.setType(type);
        map.put(type, fragment);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.typefragment, container, false);
        fbl = view.findViewById(R.id.typefbl);
        screenWidth = PmUtil.getScreenWidth(view.getContext());
        loddate(type, 1);
        final ScrollView scrollView = view.findViewById(R.id.typescroll);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断scrollview 滑动到底部
                // scrollY 的值和子view的高度一样，这人物滑动到了底部
                if (scrollView.getChildAt(0).getHeight() - scrollView.getHeight()
                        == scrollView.getScrollY()) {
                    if (page < maxpage & !inlod) {
                        page += 1;
                        loddate(type, page);
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void loddate(String type, int page) {
        inlod = true;
        QuietOkHttp.get("https://dbys.vip/gettypeys")
                .addParams("type", type)
                .addParams("page", String.valueOf(page))
                .execute(new StringCallBack() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                    }

                    @Override
                    protected void onSuccess(Call call, String response) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        JSONArray tjarry = jsonObject.getJSONArray("list");
                        maxpage = jsonObject.getIntValue("zys");
                        List<Ysb> list = JSONObject.parseArray(JSONObject.toJSONString(tjarry), Ysb.class);
                        for (Ysb ysb : list) {
                            YsImg dbys = new YsImg(view.getContext(), ysb.getTp(), ysb.getPm(), ysb.getZt(), (screenWidth / 4) - 12);
                            dbys.setId(ysb.getId());
                            fbl.addView(dbys);
                        }
                        inlod = false;
                    }
                });
    }
}
