package com.example.hzhm.versionupdate.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.hzhm.versionupdate.R;

public class MainActivity extends Activity {

    private TextView txtTitle;
    private TextView txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTitle = (TextView) findViewById(R.id.title);
        txtTitle.setText("测试版本更新");
        txtContent = (TextView) findViewById(R.id.btn);
        txtContent.setText("点击获取最新版本");
        txtContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionUpdater versionUpdater = new VersionUpdater();
                versionUpdater.checkNewVersion(MainActivity.this, null, true);
            }
        });
    }
}
