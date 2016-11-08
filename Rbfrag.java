package com.fantasysmash.SportsJoust;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fantasysmash.SportsJoust.R;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Rbfrag extends ListFragment{

    private ArrayAdapter<String> mAdapter;

    private final DragSortListView.DropListener mDropListener =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        String item = mAdapter.getItem(from);
                        mAdapter.remove(item);
                        mAdapter.insert(item, to);
                    }
                }
            };

    private final DragSortListView.RemoveListener mRemoveListener =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    mAdapter.remove(mAdapter.getItem(which));
                }
            };

    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = false;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public static Rbfrag newInstance(int headers, int footers) {
        Rbfrag f = new Rbfrag();

        Bundle args = new Bundle();
        args.putInt("headers", headers);
        args.putInt("footers", footers);
        f.setArguments(args);

        return f;
    }

    public Rbfrag() {
        super();
    }

    protected int getLayout() {
        // this DSLV xml declaration does not call for the use
        // of the default DragSortController; therefore,
        // DSLVFragment has a buildController() method.
        return R.layout.dslv_fragment_main;
    }

    /**
     * Return list item layout resource passed to the ArrayAdapter.
     */
    protected int getItemLayout() {
        /*if (removeMode == DragSortController.FLING_LEFT_REMOVE || removeMode == DragSortController.SLIDE_LEFT_REMOVE) {
            return R.layout.list_item_handle_right;
        } else */
        if (removeMode == DragSortController.CLICK_REMOVE) {
            return R.layout.list_item_click_remove;
        } else {
            return R.layout.list_item_handle_left;
        }
    }

    public DragSortController getController() {
        return mController;
    }

    /**
     * Called from DSLVFragment.onActivityCreated(). Override to
     * set a different mAdapter.
     */
    protected void setListAdapter() {

        try {
            String stuff = new GetQb().execute("http://52.24.226.232/mgetRb").get();
            Log.i("please", stuff);

            JSONObject json = new JSONObject(stuff);

            //turn json object to array with function
            JSONArray rbr = json.getJSONArray("rbr");
            String[] rbs = new String[rbr.length()];
            for(int i=0;i<rbr.length();i++)
            {
                JSONObject jb = (JSONObject) rbr.get(i);
                String name = jb.getString("name");
                String opp = jb.getString("opp");
                String info = name +" -- "+ opp;
                rbs[i] = info;
                //Log.i("qb",info);
            }
            //turn array to list
            List<String> list = new ArrayList<String>(Arrays.asList(rbs));

            mAdapter = new ArrayAdapter<String>(getActivity(), getItemLayout(), R.id.text, list);
            setListAdapter(mAdapter);
        }
        catch (Exception e) {
            //Log.d("stuff", "nope");
        }


    }

    /**
     * Called in onCreateView. Override this to provide a custom
     * DragSortController.
     */
    protected DragSortController buildController(DragSortListView dslv) {
        // defaults are
        //   dragStartMode = onDown
        //   removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDslv = (DragSortListView) inflater.inflate(getLayout(), container, false);

        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);

        return mDslv;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDslv = (DragSortListView) getListView();

        mDslv.setDropListener(mDropListener);
        mDslv.setRemoveListener(mRemoveListener);

        Bundle args = getArguments();
        int headers = 0;
        int footers = 0;
        if (args != null) {
            headers = args.getInt("headers", 0);
            footers = args.getInt("footers", 0);
        }

        for (int i = 0; i < headers; i++) {
            addHeader(getActivity(), mDslv);
        }
        for (int i = 0; i < footers; i++) {
            addFooter(getActivity(), mDslv);
        }

        setListAdapter();
    }


    public static void addHeader(Activity activity, DragSortListView dslv) {
        LayoutInflater inflater = activity.getLayoutInflater();
        int count = dslv.getHeaderViewsCount();

        TextView header = (TextView) inflater.inflate(R.layout.header_footer, null);
        header.setText("Header #" + (count + 1));

        dslv.addHeaderView(header, null, false);
    }

    public static void addFooter(Activity activity, DragSortListView dslv) {
        LayoutInflater inflater = activity.getLayoutInflater();
        int count = dslv.getFooterViewsCount();

        TextView footer = (TextView) inflater.inflate(R.layout.header_footer, null);
        footer.setText("Footer #" + (count + 1));

        dslv.addFooterView(footer, null, false);
    }

    class GetQb extends AsyncTask<String, String, String>{
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