package com.petyodrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Creates own adapter to construct a row
 * */

class MyAdapter extends BaseAdapter{
	//Context is a mediator with the system
	private Context context;
	//the text shown on each list item
	String[] options;
	//the icon corresponding to it
	int[] images = {R.drawable.ic_home, R.drawable.ic_check_course, R.drawable.ic_add, R.drawable.ic_manage, R.drawable.exit};
	
	//constructor of the class
	//sets the contect and extracts data from the XML file containing the text for the list items
	MyAdapter(Context context){
		this.context = context;
		options  = context.getResources().getStringArray(R.array.social);
	}
	
	//overwrite methods of Base adapter
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return options.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return options[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = null;
		
		if(convertView == null){
			/*if true => we are creating this row for the first time*/
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			/*
			 * with the inflater object convert the pre made xml custo row into java 
			 * */
			row = inflater.inflate(R.layout.custom_row, parent, false);
		} else {
			row = convertView;
		}
		
		//reference to the elements of each row
		TextView titleTextView = (TextView) row.findViewById(R.id.textView1);
		ImageView titleImageView = (ImageView) row.findViewById(R.id.imageView1);
		
		/*
		 * set the values of elements the row
		 * */
		titleTextView.setText(options[position]);
		titleImageView.setImageResource(images[position]);
		
		return row;
	}
	
	
}