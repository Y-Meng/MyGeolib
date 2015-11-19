package com.mcy.mygeo3d;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mcy.airhockey.AirHockeyActivity;
import com.mcy.particles.ParticlesActivity;

public class GLFeatureActivity extends AppCompatActivity {

    private ListView lvMenu;
    private String[] menus = {"粒子效果","三维桌球"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl_feature);
        lvMenu = (ListView)findViewById(R.id.lvMenu);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(GLFeatureActivity.this,ParticlesActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(GLFeatureActivity.this, AirHockeyActivity.class));
                        break;
                    case 2:
                        break;
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,menus);
        lvMenu.setAdapter(adapter);
    }
}
