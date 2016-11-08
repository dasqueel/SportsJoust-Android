package com.fantasysmash.SportsJoust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasysmash.SportsJoust.R;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class QbsDSLV extends ActionBarActivity implements RemoveModeDialog.RemoveOkListener,
        DragInitModeDialog.DragOkListener, EnablesDialog.EnabledOkListener {

    TextView toRbs;

    private static final String TAG_DSLV_FRAGMENT = "dslv_fragment";

    private int mNumHeaders = 0;
    private int mNumFooters = 0;
    private String pos = "qb";
    private String mtoken;
    private String contest;

    private int mDragStartMode = DragSortController.ON_DRAG;
    private boolean mRemoveEnabled = true;
    private int mRemoveMode = DragSortController.FLING_REMOVE;
    private boolean mSortEnabled = true;
    private boolean mDragEnabled = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qbs);

        //toast
        Toast.makeText(getApplicationContext(), "Rank top 5 QBs", Toast.LENGTH_LONG).show();

        SharedPreferences prefs = getSharedPreferences("SportsJoust", MODE_PRIVATE);
        String token = prefs.getString("mtoken", null);
        mtoken = token;
        //intent variables -- contest
        Intent intent = getIntent();
        String contestCode = intent.getStringExtra("contest");
        contest = contestCode;

        toRbs = (TextView)findViewById(R.id.toRbs);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(TAG_DSLV_FRAGMENT) == null) {
            fm.beginTransaction()
                    .add(R.id.test_bed, getNewDslvFragment(), TAG_DSLV_FRAGMENT)
                    .commit();
        }

        toRbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DSLVFragment f =
                        (DSLVFragment) getSupportFragmentManager().findFragmentByTag(TAG_DSLV_FRAGMENT);
                DragSortListView dslv = (DragSortListView) f.getListView();

                String[] newOrder = new String[dslv.getAdapter().getCount()];
                for (int i = 0; i < dslv.getAdapter().getCount(); i++) {
                    Object obj = dslv.getAdapter().getItem(i);
                    newOrder[i] = obj.toString();
                    //Log.i("adpt", obj.toString());
                }

                setNewRank(pos, newOrder, mtoken, contest);

                //go to rbs
                Intent rbIntent = new Intent(QbsDSLV.this, RbsDSLV.class);
                rbIntent.putExtra("contest", contest);
                startActivity(rbIntent);

            }
        });

    }

    @Override
    public void onRemoveOkClick(int removeMode) {
        if (removeMode != mRemoveMode) {
            mRemoveMode = removeMode;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.test_bed, getNewDslvFragment(), TAG_DSLV_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onDragOkClick(int dragStartMode) {
        mDragStartMode = dragStartMode;
        DSLVFragment f =
                (DSLVFragment) getSupportFragmentManager().findFragmentByTag(TAG_DSLV_FRAGMENT);
        f.getController().setDragInitMode(dragStartMode);
    }

    @Override
    public void onEnabledOkClick(boolean drag, boolean sort, boolean remove) {
        mSortEnabled = sort;
        mRemoveEnabled = remove;
        mDragEnabled = drag;
        DSLVFragment f =
                (DSLVFragment) getSupportFragmentManager().findFragmentByTag(TAG_DSLV_FRAGMENT);
        DragSortListView dslv = (DragSortListView) f.getListView();
        //f.getController().setRemoveEnabled(remove);
        f.getController().setSortEnabled(sort);
        dslv.setDragEnabled(drag);
    }

    private Fragment getNewDslvFragment() {
        DSLVFragmentClicks f = DSLVFragmentClicks.newInstance(mNumHeaders, mNumFooters, pos, mtoken, contest);
        f.removeMode = mRemoveMode;
        f.removeEnabled = mRemoveEnabled;
        f.dragStartMode = mDragStartMode;
        f.sortEnabled = mSortEnabled;
        f.dragEnabled = mDragEnabled;
        return f;
    }

    //post new ranking as json data
    protected void setNewRank(final String pos, final String[] newOrder, final String mtoken, final String contest) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();
                JSONArray jsonRank = new JSONArray(Arrays.asList(newOrder));

                try {
                    HttpPost post = new HttpPost("http://52.24.226.232/msetRank");
                    json.put("pos", "qb");
                    json.put("newOrder", jsonRank);
                    json.put("mtoken", mtoken);
                    json.put("contest", contest);
                    //Log.i("contestCode",json.toString());
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        String resp = readString(in);
                        Log.i("post",resp);
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    //createDialog("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    //function to read opst response
    public static String readString(InputStream inputStream) throws IOException {

        ByteArrayOutputStream into = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        for (int n; 0 < (n = inputStream.read(buf));) {
            into.write(buf, 0, n);
        }
        into.close();
        return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle(contest);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_settings:
                // search action
                return true;
            case R.id.home:
                // location found
                Intent i = new Intent(QbsDSLV.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                // location found
                SharedPreferences prefs = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(QbsDSLV.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
