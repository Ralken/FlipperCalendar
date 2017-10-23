package cn.ralken.android.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthView extends LinearLayout {
	private CalendarGridView grid;
	private Listener listener;
	private View selectCellView;
	private CalendarRowView lastRowView;
	
	/** help fast searching a view through cell */
	Map<String, View> searchMap = new HashMap<String, View>();
	List<Date> monthDates = new ArrayList<Date>();
	
	public static MonthView create(ViewGroup parent, LayoutInflater inflater, Listener listener) {
		final MonthView view = (MonthView) inflater.inflate(R.layout.calendar_month_grid, parent, false);
		view.listener = listener;
		return view;
	}
	
	public static MonthView create(LayoutInflater inflater, Listener listener) {
		return create(null, inflater, listener);
	}

	public MonthView(Context context) {
		super(context);
	}

	public MonthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public void setSelectByDate(Date date){
		String searchKey = genDateTag(date);
		if(searchMap.containsKey(searchKey)){
			View cellView = searchMap.get(searchKey);
			setSelectCellView(cellView);
		}
	}

	public void showHasDataFlag(List<Date> dateList) {
		for (Date date : dateList) {
			String searchKey = genDateTag(date);
			if (searchMap.containsKey(searchKey)) {
				View tView = searchMap.get(searchKey);
				final MonthCellDescriptor descriptor = (MonthCellDescriptor) tView
						.getTag();
				if (null != descriptor) {
					descriptor.setHasExtrarValue(true);
					tView.findViewById(R.id.mCellDot).setVisibility(
							View.VISIBLE);
				}
			}
		}
	}

	public void handleClickedIfHasExtraValue(Date date){
		String searchKey = genDateTag(date);
		if(searchMap.containsKey(searchKey)){
			View tView = searchMap.get(searchKey);
			listener.onCellClicked((MonthCellDescriptor) tView.getTag());
		}
	}
	
	public void setSelectCellView(View selectCellView) {
		disableSelectCell();
		this.selectCellView = selectCellView;
		this.selectCellView.setSelected(true);
	}
	
	public void disableSelectCell() {
		if(null != selectCellView){
			selectCellView.setSelected(false);
		}
	}
	
	public Date getSelectedDate(){
		if(selectCellView != null && selectCellView.getTag() != null){
			return ((MonthCellDescriptor) selectCellView.getTag()).getDate();
		}
		return new Date();
	}
	
	/**
	 * convert cell descriptor to a string for that we can make it as a hashmap
	 * key for searching
	 */
	private static String genDateTag(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH) + "_"
				+ cal.get(Calendar.DAY_OF_MONTH);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		grid = (CalendarGridView) findViewById(R.id.calendar_grid);
	}

	public void init(List<List<MonthCellDescriptor>> cells) {
		//release hashmap reference, or maybe cause memory exceed
		searchMap.clear();
		monthDates.clear();
		lastRowView = null;
		
		final int numRows = cells.size();
		for (int i = 0; i < 6; i++) {
			CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
			weekRow.setListener(listener);
			if (i < numRows) {
				// always hide the last row, if we need show this view just please
				// change it's visibility manully.
				if(i == 5){
					lastRowView = weekRow;
					lastRowView.setVisibility(View.GONE);
				}else {
					weekRow.setVisibility(VISIBLE);
				}
				List<MonthCellDescriptor> week = cells.get(i);
				for (int c = 0; c < week.size(); c++) {
					MonthCellDescriptor cell = week.get(c);
					final RelativeLayout cellLayout = (RelativeLayout) weekRow.getChildAt(c);
					final View dotImg = cellLayout.findViewById(R.id.mCellDot);
					final CheckedTextView cellView = (CheckedTextView) cellLayout.findViewById(R.id.mCellText); 
					//final CheckedTextView cellView = (CheckedTextView) weekRow.getChildAt(c);
					
					cellView.setText(Integer.toString(cell.getValue()));
					cellView.setChecked(!cell.isToday());
					
					//nagetive to color state, fix bug CSAND-370
					if (cell.isToday()) {
						dotImg.setBackgroundColor(getResources().getColor(R.color.calendar_text_selected));
					}
					
					final boolean isSelected = cell.isSelected();
					if(isSelected){
						this.setSelectCellView(cellLayout);		//remember current selecte cell
					}
					dotImg.setVisibility(cell.hasExtrarValue()? View.VISIBLE: View.INVISIBLE);
					cellLayout.setSelected(cell.isSelected());
					if (cell.isCurrentMonth()) {
						cellView.setTextColor(getResources().getColorStateList(R.color.calendar_text_selector));
					} else { 
						cellView.setTextColor(getResources().getColor(R.color.calendar_text_unselectable));
					}
					cellLayout.setTag(cell);
					//add search index
					searchMap.put(genDateTag(cell.getDate()), cellLayout);
					monthDates.add(cell.getDate());
				}
			} else {
				weekRow.setVisibility(GONE);
			}
		}
	}

	public List<Date> getMonthDates(){
		return monthDates;
	}
	
	public void showLastRowViewIfNecessary() {
		if(null != lastRowView){
			lastRowView.setVisibility(View.VISIBLE);
		}
	}
	
	public interface Listener {
		void onCellClicked(MonthCellDescriptor cell);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int mAction = ev.getAction();
		switch (mAction) {
		case MotionEvent.ACTION_MOVE:
			ev.setAction(MotionEvent.ACTION_DOWN);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:

			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
