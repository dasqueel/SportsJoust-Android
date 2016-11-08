package com.fantasysmash.SportsJoust;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.fantasysmash.SportsJoust.R;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortCursorAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test extends Activity
{
    DragSortListView listView;
    ArrayAdapter<String> adapter;
    TextView toRbs;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener()
    {
        @Override
        public void drop(int from, int to)
        {
            if (from != to)
            {
                String item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
            }
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener()
    {
        @Override
        public void remove(int which)
        {
            adapter.remove(adapter.getItem(which));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qbs);

        toRbs = (TextView)findViewById(R.id.toRbs);


        listView = (DragSortListView) findViewById(R.id.listview);
        String pos = "qb";

        String url = "http://52.24.226.232/mgetPos?pos="+pos;
        try {

            String res = new GetRank().execute(url).get();
            //Log.i("please", stuff);

            JSONObject json = new JSONObject(res);

            //turn json object to array with function
            JSONArray jsonRank = json.getJSONArray(pos+"r");
            String[] ranking = new String[jsonRank.length()];
            for(int i=0;i<jsonRank.length();i++)
            {
                JSONObject jb = (JSONObject) jsonRank.get(i);
                String name = jb.getString("name");
                String opp = jb.getString("opp");
                String info = name +" -- "+ opp;
                ranking[i] = info;
                //Log.i("url",url);
            }
            //instead of passing a list, pass a cursor?
            //turn array to list
            //List<String> list = new ArrayList<String>(Arrays.asList(ranking));
            String[] columns = new String[] { "_id", "player" };

            MatrixCursor matrixCursor= new MatrixCursor(columns);
            startManagingCursor(matrixCursor);

            for(int i=0;i<ranking.length;i++)
            {
                matrixCursor.addRow(new Object[] {i, ranking[i] });
            }



            String array[] = new String[matrixCursor.getCount()];
            int i = 0;

            matrixCursor.moveToFirst();
            while (matrixCursor.isAfterLast() == false) {
                array[i] = matrixCursor.getString(0);
                Log.i("player", matrixCursor.getString(matrixCursor.getColumnIndex("player")));
                i++;
                matrixCursor.moveToNext();
            }
            //String[] stuff = {"cat","dog","pig"};
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(ranking));
            adapter = new ArrayAdapter<String>(this,
                    R.layout.list_item_handle_left, R.id.text, list);
            //adapter = new DragSortCursorAdapter(this, matrixCursor);


        }
        catch (Exception e) {
            //Log.d("stuff", "nope");
        }

        listView.setDropListener(onDrop);
        listView.setRemoveListener(onRemove);

        DragSortController controller = new DragSortController(listView);
        controller.setDragHandleId(R.id.drag_handle);
        //controller.setClickRemoveId(R.id.);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(1);
        //controller.setRemoveMode(removeMode);

        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDragEnabled(true);
        /*
        toRbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    String player = adapter.getItem(i);
                    Log.i("playerAdpt", player);
                    Log.i("clicked","clicker");
                }
            }
        });*/

    }

    class GetRank extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            // Log.i("async", responseString);
            return responseString;
        }
    }

}