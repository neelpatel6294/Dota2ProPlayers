package com.example.android.dota2proplayers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.dota2proplayers.Adapter.ProPlayerPOGO;
import com.example.android.dota2proplayers.Adapter.ProPlayerRecycleAdapter;
import com.example.android.dota2proplayers.Utils.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycleView;
    private ProPlayerRecycleAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecycleView = findViewById(R.id.rv_numbers);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        URL final_url = NetworkUtils.builtURL();
        new ProPlayerAsyncTask().execute(final_url);
    }

    @SuppressLint("StaticFieldLeak")
    public class ProPlayerAsyncTask extends AsyncTask<URL, Void, List<ProPlayerPOGO>> {

        @Override
        protected List<ProPlayerPOGO> doInBackground(URL... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            return NetworkUtils.fetchDotaHeroesData(urls[0]);

        }

        @Override
        protected void onPostExecute(List<ProPlayerPOGO> proPlayerPOGOS) {

            if (proPlayerPOGOS != null) {
                mAdapter = new ProPlayerRecycleAdapter(MainActivity.this, proPlayerPOGOS,
                        new ProPlayerRecycleAdapter.ListItemClickListener() {
                            @Override
                            public void onListItemClick(ProPlayerPOGO proPlayerPOGO) {
                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                intent.putExtra("data", proPlayerPOGO);
                                startActivity(intent);
                            }
                        });

                mRecycleView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
