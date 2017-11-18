package com.seekhoney.multipleviewmap;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.amap.api.services.core.PoiItem;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.constant.TestConst;
import com.seekhoney.library_gdmap.listener.OnCheckWhinAreaListener;
import com.seekhoney.library_gdmap.listener.OnLocationCommonListener;
import com.seekhoney.library_gdmap.listener.OnSelectedPoiListener;
import com.seekhoney.library_gdmap.logic.MapHelper;
import com.seekhoney.library_gdmap.model.PositionEntity;
import com.seekhoney.library_gdmap.utils.Utils;

/**
 * Created by ryan on 17/6/15.
 */

public class NewMainActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout rlr_locate;
    private RelativeLayout rlt_sign;
    private RelativeLayout rlt_use; //调起第三方应用按钮
    private NewMainActivity ins;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        init();

        //获取权限
        MapHelper.getIns(this).getCurrentLocation(this, new OnLocationCommonListener() {
            @Override
            public void onLocationDataBack(PositionEntity entity) {

            }
        });

    }

    private void init() {
        rlr_locate = (RelativeLayout) findViewById(R.id.rlr_locate);
        rlr_locate.setOnClickListener(this);
        rlt_sign = (RelativeLayout) findViewById(R.id.rlt_sign);
        rlt_sign.setOnClickListener(this);
        rlt_use = (RelativeLayout) findViewById(R.id.rlt_use);
        rlt_use.setOnClickListener(this);
        ins = this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlr_locate) {
            MapHelper.getIns(this).gotosetLocate(ins, null, null, "设置签到位置(测试)", 500f, new OnSelectedPoiListener() {
                @Override
                public void selectedPoiCallback(PoiItem item) {
                    //// TODO: 17/11/18
                }
            });
        } else if (id == R.id.rlt_sign) {
            Location location = null;
            if (TestConst.latLng != null) {
                location = new Location("dd");
                location.setLongitude(TestConst.latLng.longitude);
                location.setLatitude(TestConst.latLng.latitude);
            } else {
                Toast.makeText(ins, "未设置签到位置", Toast.LENGTH_SHORT).show();
            }

            MapHelper.getIns(this).gotoSignMark(ins, location, null, null, "测试打卡", "这是高地", "这是你", "在高地在高地", "不在高地", 500f, new OnCheckWhinAreaListener() {
                @Override
                public void getWithinAreaCode(int code) {
                    if (code == Const.BAD_REQUEST) {
                        //// TODO: 17/11/18
                    } else if (code == Const.WITHIN_AREA) {
                        //// TODO: 17/11/18
                    } else if (code == Const.WITHOUT_AREA) {
                        //// TODO: 17/11/18
                    }
                }
            });
        } else if (id == R.id.rlt_use) {
            Location location = null;
            if (TestConst.latLng != null) {
                location = new Location("ee");
                location.setLongitude(TestConst.latLng.longitude);
                location.setLatitude(TestConst.latLng.latitude);
            }
            Utils.checkMaps(this, location, "目的地");
        }
    }
}
