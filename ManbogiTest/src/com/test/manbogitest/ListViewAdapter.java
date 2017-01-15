package com.test.manbogitest;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listViewItemList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listViewItemList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
        final Context context = parent.getContext();
        
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }
        
        TextView dateTextView = (TextView) convertView.findViewById(R.id.listTextView1);
        TextView cntTextView = (TextView) convertView.findViewById(R.id.listTextView2);
        TextView disTextView = (TextView) convertView.findViewById(R.id.listTextView3);
        
        ListViewItem listViewItem = listViewItemList.get(arg0);
        
        dateTextView.setText(listViewItem.getDate());
        cntTextView.setText(listViewItem.getCnt()+"");
        disTextView.setText(listViewItem.getDis());
        
		return convertView;
	}
	
	public void addItem(String date, int cnt, String dis) {
        ListViewItem item = new ListViewItem();

        item.setDate(date);
        item.setCnt(cnt);
        item.setDis(dis);

        listViewItemList.add(item);
    }

}
