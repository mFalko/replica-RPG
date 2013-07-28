package com.replica.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.replica.R;

public class InventoryActivity extends Activity {

	private ArrayList<ItemData> mItemData;
	ListView inventoryListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_inventory);

		inventoryListView = (ListView) this.findViewById(R.id.invintorylist);

		mItemData = new ArrayList<ItemData>(10);

		for (int i = 0; i < 10; ++i) {
			mItemData.add(new ItemData("", i));
		}
		
		ItemArrayAdapter<ItemData> mItemArrayAdapter = new ItemArrayAdapter<ItemData>(
				this, R.layout.inventory_list_item, -1, -1, mItemData);
		
		
		inventoryListView.setAdapter(mItemArrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private class ItemArrayAdapter<T> extends ArrayAdapter<T> {

		private static final int TYPE_COUNT = 1;
		
		private Context mContext;
		private int mRowResource;
		private int mTextViewResource;
		private int mTextViewResource2;

		public ItemArrayAdapter(Context context, int resource,
				int textViewResourceId, int textViewResourceId2, List<T> objects) {
			super(context, resource, textViewResourceId, objects);
			mContext = context;
			mRowResource = resource;
			mTextViewResource = textViewResourceId;
			mTextViewResource2 = textViewResourceId2;
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}
		
		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}
		
		@Override
		public boolean hasStableIds() {
			return false;
		}
		
		@Override
		public boolean isEmpty() {
			return mItemData.size() > 0;
		}
		
		@Override
        public View getView (int position, View convertView, ViewGroup parent) {
            View sourceView = null;
            
            
			if (convertView != null) {
				sourceView = convertView;
			} else {
				sourceView = LayoutInflater.from(mContext).inflate(
						mRowResource, parent, false);
			}
            	
            TextView view = (TextView)sourceView.findViewById(mTextViewResource);
            if (view != null) {
                view.setText(mItemData.get(position).name);
            }
            
            TextView view2 = (TextView)sourceView.findViewById(mTextViewResource2);
            if (view2 != null) {
                view2.setText(mItemData.get(position).quanity);
            }
            return sourceView;
        }


	}

	// TODO: this will change when the game item class is created
	private class ItemData {
		String name;
		int quanity;
		
		ItemData (String n, int q) {
			name = n;
			quanity = q;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
