package com.example.android.dota2proplayers;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.dota2proplayers.Adapter.FavoriteAdapter;
import com.example.android.dota2proplayers.Adapter.ProPlayerPOGO;
import com.example.android.dota2proplayers.Adapter.ProPlayerRecycleAdapter;
import com.example.android.dota2proplayers.Utils.NetworkUtils;
import com.facebook.stetho.Stetho;

import java.net.URL;
import java.util.List;

import static com.example.android.dota2proplayers.Data.Contract.PlayersContract.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecycleView;
    private ProPlayerRecycleAdapter mAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAYER_LOADER_ID = 0;

    private FavoriteAdapter mFavoritaAdapter;
    private final static String MENU_SELECTED = "selected";
    private int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);



        mRecycleView = findViewById(R.id.rv_numbers);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
//        URL final_url = NetworkUtils.builtURL();
//        new ProPlayerAsyncTask().execute(final_url);

        if (savedInstanceState == null) {
            build();

            //            build("heroes");
//            URL url = NetworkUtils.builtURL();
//            new HeroAsyncTask().execute(url);
        } else {
            if (savedInstanceState != null) {
                selected = savedInstanceState.getInt(MENU_SELECTED);

                if (selected == -1) {
                    build();

                } else if (selected == R.id.fav) {
                    getActionBar().setTitle("Favorite Heroes");
                    getLoaderManager().restartLoader(PLAYER_LOADER_ID, null, this);
                    mFavoritaAdapter = new FavoriteAdapter(new ProPlayerRecycleAdapter.ListItemClickListener() {
                        @Override
                        public void onListItemClick(ProPlayerPOGO heroes) {
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            intent.putExtra("data", heroes);
                            startActivity(intent);
                        }
                    }, this);
                    mRecycleView.setAdapter(mFavoritaAdapter);
                }
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mHeroData = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mHeroData != null) {
                    deliverResult(mHeroData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {

                    return getContentResolver().query(CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mHeroData = data;
                super.deliverResult(data);
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoritaAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoritaAdapter.swapCursor(null);
    }



    @SuppressLint("StaticFieldLeak")
    public class ProPlayerAsyncTask extends AsyncTask<URL, Void, List<ProPlayerPOGO>> {

        @Override
        protected List<ProPlayerPOGO> doInBackground(URL... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<ProPlayerPOGO> result = NetworkUtils.fetchDotaHeroesData(urls[0]);
            return result;

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
            } else {
                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(MENU_SELECTED, selected);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.all:
                //  getActionBar().setTitle("ALL");
                //build("heroes");
                selected = id;
                break;
            case R.id.fav:
                selected = id;
                getLoaderManager().restartLoader(PLAYER_LOADER_ID, null,this);
                mFavoritaAdapter = new FavoriteAdapter(new ProPlayerRecycleAdapter.ListItemClickListener() {
                    @Override
                    public void onListItemClick(ProPlayerPOGO proPlayerPOGO) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("data", proPlayerPOGO);
                        startActivity(intent);
                    }
                }, this);
                mRecycleView.setAdapter(mFavoritaAdapter);
        }
        return super.onOptionsItemSelected(item);
    }


    private void build() {
        URL final_Url = NetworkUtils.builtURL();
        new ProPlayerAsyncTask().execute(final_Url);
    }
}
