package com.example.sean.locationwheeldemo;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;


public class WheelActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wheel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnWheelClickedListener, View.OnClickListener {
        private final static String TAG = "PlaceholderFragment";
        public static int LOC_INDEX_DISTRICT = 0;
        public static int LOC_INDEX_STREET = 1;
        public static int LOC_INDEX_HOUSE_NO = 2;
        private String mDistrict[] =
                new String[] {"鼓楼区", "玄武区", "江宁区", "河西区", "栖霞区", "白下区", "建邺区", "雨花区"};
        private String mStreat[] =
                new String[] {"天元西路", "龙眠大道", "竹山路", "胜太路", "将军大道", "双龙大道", "新亭东路", "天元东路", "天印大道", "方正大道", "诚信大道"};
        private String mHouseNos[] =
                new String[] {"天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号", "天元西路1号"};
        private WheelView mLocationWheel;
        private TextView mParentText;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wheel, container, false);
            mLocationWheel = (WheelView) rootView.findViewById(R.id.location);
            mLocationWheel.setVisibleItems(10);
            mLocationWheel.setViewAdapter(new LocationAdapter(getActivity(), mDistrict, LOC_INDEX_DISTRICT));
            mLocationWheel.addClickingListener(this);
            mParentText = (TextView) rootView.findViewById(R.id.parent_location);
            mParentText.setOnClickListener(this);
            return rootView;
        }

        @Override
        public void onItemClicked(WheelView wheel, int itemIndex) {
            Log.i(TAG, "onItemClicked itemIndex:" + itemIndex);
            LocationAdapter adapter = (LocationAdapter)mLocationWheel.getViewAdapter();
            if(adapter != null) {
                if(adapter.mLocalIndex == LOC_INDEX_DISTRICT) {
                    mParentText.setText(mDistrict[itemIndex]);
                    mLocationWheel.setViewAdapter(new LocationAdapter(getActivity(), mStreat, LOC_INDEX_STREET));
                } else if(adapter.mLocalIndex == LOC_INDEX_STREET) {
                    mParentText.setText(mStreat[itemIndex]);
                    mLocationWheel.setViewAdapter(new LocationAdapter(getActivity(), mHouseNos, LOC_INDEX_HOUSE_NO));
                }
            }
        }

        @Override
        public void onClick(View v) {
            LocationAdapter adapter = (LocationAdapter)mLocationWheel.getViewAdapter();
            if(adapter != null) {
                if(adapter.mLocalIndex == LOC_INDEX_HOUSE_NO) {
                    mParentText.setText(mDistrict[0]);
                    mLocationWheel.setViewAdapter(new LocationAdapter(getActivity(), mStreat, LOC_INDEX_STREET));
                } else if(adapter.mLocalIndex == LOC_INDEX_STREET) {
                    mParentText.setText(null);
                    mLocationWheel.setViewAdapter(new LocationAdapter(getActivity(), mDistrict, LOC_INDEX_DISTRICT));
                }
            }
        }

        private class LocationAdapter extends AbstractWheelTextAdapter {
            public int mLocalIndex;
            // Countries names
            private String mContents[];
            /**
             * Constructor
             */
            protected LocationAdapter(Context context, String[] contents, int loc_index) {
                super(context, R.layout.location_layout, NO_RESOURCE);
                setItemTextResource(R.id.location_name);
                mContents = contents;
                mLocalIndex = loc_index;
            }

            @Override
            public View getItem(int index, View cachedView, ViewGroup parent) {
                View view = super.getItem(index, cachedView, parent);
                return view;
            }

            @Override
            public int getItemsCount() {
                return mContents.length;
            }

            @Override
            protected CharSequence getItemText(int index) {
                return mContents[index];
            }
        }

    }
}
