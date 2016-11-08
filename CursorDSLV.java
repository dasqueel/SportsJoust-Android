package com.fantasysmash.SportsJoust;

import com.fantasysmash.SportsJoust.R;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CursorDSLV extends FragmentActivity {

    //private SimpleDragSortCursorAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qbs);

        SimpleDragSortCursorAdapter adapter;
        String[] cols = {"name"};
        int[] ids = {R.id.text};
        adapter = new MAdapter(this,
                R.layout.list_item_handle_left, null, cols, ids, 0);

        DragSortListView dslv = (DragSortListView) findViewById(android.R.id.list);
        dslv.setAdapter(adapter);

        // build a cursor from the String array
        MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "name"});
        String pos = "qb";
        String url = "http://52.24.226.232/mgetPos?pos="+pos;
        try {
            String res = new GetRank().execute(url).get();
            //Log.i("please", stuff);

            JSONObject json = new JSONObject(res);

            //turn json object to array with function
            JSONArray jsonRank = json.getJSONArray(pos+"r");
            String[] ranking = new String[jsonRank.length()];
            //String[] artistNames = getResources().getStringArray(R.array.jazz_artist_names);
            for(int i=0;i<jsonRank.length();i++)
            {
                JSONObject jb = (JSONObject) jsonRank.get(i);
                String name = jb.getString("name");
                String opp = jb.getString("opp");
                String info = name +" -- "+ opp;
                ranking[i] = info;
                //Log.i("url",url);
            }
            for (int i = 0; i < ranking.length; i++) {
                cursor.newRow()
                        .add(i)
                        .add(ranking[i]);
            }
        }
        catch (Exception e){
            //somethign;
        }
        adapter.changeCursor(cursor);
    }

    private class MAdapter extends SimpleDragSortCursorAdapter {
        private Context mContext;

        public MAdapter(Context ctxt, int rmid, Cursor c, String[] cols, int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            View tv = v.findViewById(R.id.text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "text clicked", Toast.LENGTH_SHORT).show();
                }
            });
            return v;
        }
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
