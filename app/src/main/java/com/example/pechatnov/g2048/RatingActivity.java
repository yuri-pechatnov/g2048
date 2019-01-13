package com.example.pechatnov.g2048;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class RatingActivity extends ActivityWithSettings {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.rating_activity);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        super.onCreate(savedInstanceState);

        // TODO: загрузка score из MainActivity (наверное)

        // TODO: ask server for json

        JSONArray jsonArray = new JSONArray();
        JSONObject jo1 = null;
        JSONObject jo2 = null;
        JSONObject jo3 = null;
        JSONObject jo4 = null;
        JSONObject jo5 = null;
        JSONObject jo6 = null;
        JSONObject jo7 = null;
        JSONObject jo8 = null;
        JSONObject jo9 = null;
        JSONObject jo10 = null;
        try {
             jo1 = new JSONObject()
                    .put("place", 1)
                    .put("name", "jackjack")
                    .put("score", 125);
             jo2 = new JSONObject()
                     .put("place", 2)
                     .put("name", "NotGoodWord")
                     .put("score", 93);
             jo3 = new JSONObject()
                    .put("place", 3)
                    .put("name", "olololololololololololololololololololololololololololololololololololololololololol")
                    .put("score", 1123);
             jo4 = new JSONObject()
                    .put("place", 4)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
             jo5 = new JSONObject()
                    .put("place", 5)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
             jo6 = new JSONObject()
                    .put("place", 6)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
             jo7 = new JSONObject()
                    .put("place", 7)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
             jo8 = new JSONObject()
                    .put("place", 8)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
             jo9 = new JSONObject()
                    .put("place", 9)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
             jo10 = new JSONObject()
                    .put("place", 10)
                    .put("name", "OneBadWord")
                    .put("score", 1244871234);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jo1);
        jsonArray.put(jo2);
        jsonArray.put(jo3);
        jsonArray.put(jo4);
        jsonArray.put(jo5);
        jsonArray.put(jo6);
        jsonArray.put(jo7);
        jsonArray.put(jo8);
        jsonArray.put(jo9);
        jsonArray.put(jo10);

        // Здесь как бы уже работаем с загруженным jsonArray

        for (int i = -1; i < jsonArray.length(); i++) {

            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);

            if (i == -1) {
                tv1.setText(getResources().getString(R.string.place_in_rating));
                tv2.setText(getResources().getString(R.string.name_in_rating));
                tv3.setText(getResources().getString(R.string.score_in_rating));

                tv1.setTypeface(null, Typeface.BOLD);
                tv2.setTypeface(null, Typeface.BOLD);
                tv3.setTypeface(null, Typeface.BOLD);
            }

            else {
                JSONObject json;
                int text1 = 0;
                String text2 = "";
                int text3 = 0;

                try {
                    json = jsonArray.getJSONObject(i);
                    text1 = json.getInt("place");
                    text2 = json.getString("name");
                    text3 = json.getInt("score");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tv1.setText(String.valueOf(text1));
                tv2.setText(text2);
                tv3.setText(String.valueOf(text3));

                tv1.setGravity(Gravity.LEFT);
                tv2.setGravity(Gravity.LEFT);
                tv3.setGravity(Gravity.LEFT);
            }

            LinearLayout rl = new LinearLayout(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            tv1.setLayoutParams(lp);
            tv2.setLayoutParams(lp);
            tv3.setLayoutParams(lp);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
                    ScrollView.LayoutParams.MATCH_PARENT, 1);
            lp2.setMargins(5, 0, 5, 0);
            HorizontalScrollView sv1 = new HorizontalScrollView(this);
            HorizontalScrollView sv2 = new HorizontalScrollView(this);
            HorizontalScrollView sv3 = new HorizontalScrollView(this);
            sv1.setLayoutParams(lp2);
            sv2.setLayoutParams(lp2);
            sv3.setLayoutParams(lp2);
            sv1.addView(tv1);
            sv2.addView(tv2);
            sv3.addView(tv3);

            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rl.setLayoutParams(lp1);
            rl.setWeightSum(3);

//            rl.addView(tv1);
            rl.addView(sv1);
//            rl.addView(tv2);
            rl.addView(sv2);
//            rl.addView(tv3);
            rl.addView(sv3);
            LinearLayout ll = findViewById(R.id.scroll_view_linear_layout);
            ll.addView(rl);
        }
    }
}
