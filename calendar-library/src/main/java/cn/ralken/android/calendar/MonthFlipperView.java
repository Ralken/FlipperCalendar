package cn.ralken.android.calendar;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

/**
 * Android component to allow picking a date from a calendar view (a list of
 * months). Must be initialized after inflation with
 * {@link #init(Date)}. The currently
 * selected date can be retrieved with {@link #getSelectedDate()}.
 */
public class MonthFlipperView extends AnimationFlipper<MonthView> {
	private final Calendar today = Calendar.getInstance();
	private final Calendar selectedCal = Calendar.getInstance();
	/** Just record the current showwing Calendar date */
	private Calendar currentCal = Calendar.getInstance();
	
	private OnCalendarListener calendarListener;
	private FlipperListener flipperListener;
	private boolean isFirstInit = true;
	
	interface FlipperListener{
		/**called when month is to be change*/
		void onStartFlipping(Calendar targetCal);
		
		void onMonthChange(boolean isNextMonth);
	}
	
	public interface OnCalendarListener {
		/**
		 * only called when date is clicked, runs on the UI thread
		 * @param date the attached date of clicked month cell
		 * @param hasData true if the clicked data has event/events, false otherwise
		 */
		void onDateSelected(Date date, boolean hasData);
		
		/**
		 * called when calendar month changed and the month dates is not empty.
		 * Notice: it runs NOT on the UI thread, so you can operate the SQLite safely
		 * @param newMonthDates the entire month dates.
		 * @return the Date collection which has related DB data.
		 */
		List<Date> onMonthChanged(final List<Date> newMonthDates);
	}
	
	public MonthFlipperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final int bg = getResources().getColor(R.color.calendar_bg);
		setBackgroundColor(bg);
	}
	
	/**
	 * All date parameters must be non-null and their
	 * {@link Date#getTime()} must not return 0. Time of day will be
	 * ignored. For instance, if you pass in {@code minDate} as 11/16/2012
	 * 5:15pm and {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the
	 * first selectable date and 11/15/2013 will be the last selectable date (
	 * {@code maxDate} is exclusive).
	 * 
	 * @param selectedDate
	 *            Initially selected date. Must be between {@code minDate} and
	 *            {@code maxDate}.
	 */
	public void init(Date selectedDate) {
		if (selectedDate == null || selectedDate.getTime() == 0) {
			throw new IllegalArgumentException("All dates must be non-null or non-zero startDate: " + selectedDate);
		}
		
		// Sanitize input: clear out the hours/minutes/seconds/millis.
		selectedCal.setTime(selectedDate);
		currentCal.setTime(selectedDate);
		setMidnight(selectedCal);
		setMidnight(currentCal);
		
		if (getChildCount() != 0)  removeAllViews();
		
		// the following widgets init only once and necessary
		MonthView mCurrentMonthView = MonthView.create(inflater, listener);
		genMonthDescriptor(selectedCal, mCurrentMonthView);
		
		//setUpContainers(mCurrentMonthView, mNextMonthView, mPreviousMonthView);//
		setUpCurrentView(mCurrentMonthView);
		
		//dispatch events for the first creation
		//requestShowDataAsync();
		refreshCurrentDate();

		genPreAndNextMonthDecriptorDelayed(0);
	}
	
	private void genPreAndNextMonthDecriptorDelayed(long delay){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				MonthView mNextMonthView = MonthView.create(inflater, listener);//
				MonthView mPreviousMonthView = MonthView.create(inflater, listener);//
				
				genMonthDescriptor(CalendarUtility.getNextMonthCalendar(selectedCal), mNextMonthView);//
				genMonthDescriptor(CalendarUtility.getPreMonthCalendar(selectedCal), mPreviousMonthView);//
				
				setUpPreAndNextView(mNextMonthView, mPreviousMonthView);
			}
		}, delay);
	}
	
	public void handleClickedIfHasExtraValue(Date date){
		getShowingView().handleClickedIfHasExtraValue(date);
	}
	
	public void setPreMonthSelected(Date selCal) {
		getPreviousView().setSelectByDate(selCal);
	}

	public void setNextMonthSelected(Date selCal) {
		getNextView().setSelectByDate(selCal);
	}

	public boolean isPreMonthPrepared(){
		return getPreviousView() != null;
	}
	
	public boolean isNextMonthPrepared(){
		return getNextView() != null;
	}
	
	public Calendar getCurrentCal() {
		return currentCal;
	}

	public Date getSelectedDate(){
		return getShowingView().getSelectedDate();
	}
	
	public List<Date> getCurrentMonthDates() {
		return getShowingView().getMonthDates();
	}
	
	/**
	 * 
	 * @param cal
	 *            the calendar to be shown.
	 * @param attachView
	 *            attched view for the calendar
	 */
	private void genMonthDescriptor(final Calendar cal,
			final MonthView attachView) {
		MonthDescriptor monthDescriptor = new MonthDescriptor(cal.get(MONTH),
				cal.get(YEAR));
		List<List<MonthCellDescriptor>> nextMonthCells = getMonthCells(
				monthDescriptor, cal, selectedCal);
		attachView.init(nextMonthCells);
	}
	
	/** Clears out the hours/minutes/seconds/millis of a Calendar. */
	private static void setMidnight(Calendar cal) {
		cal.set(HOUR_OF_DAY, 0);
		cal.set(MINUTE, 0);
		cal.set(SECOND, 0);
		cal.set(MILLISECOND, 0);
	}

	private final MonthView.Listener listener = new MonthView.Listener() {
		@Override
		public void onCellClicked(MonthCellDescriptor cell) {
			//disable click event during flipping
			if(!isFirstInit && (isAnimating() || !isPreMonthPrepared() || !isNextMonthPrepared())) {
				return;
			}
			isFirstInit = false;
			
			//cancel current selected cell
			((MonthView)getCurrentView()).disableSelectCell();
			
			if (cell.isPreviousMonth()) {
				setPreMonthSelected(cell.getDate());
				showPrevious();
				if (flipperListener != null) {
					flipperListener.onMonthChange(false);
				}
			} else if (cell.isNextMonth()) {
				setNextMonthSelected(cell.getDate());
				showNext();
				if (flipperListener != null) {
					flipperListener.onMonthChange(true);
				}
			} else {
				((MonthView)getCurrentView()).setSelectByDate(cell.getDate());
			}
			
			//call back interface if condition is avaliable
			if (null != calendarListener) {
				calendarListener.onDateSelected(cell.getDate(), cell.hasExtrarValue());
			}
		}
	};
	
	List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal, Calendar selectedDate) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		
		cal.setTime(startCal.getTime());
		List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
		cal.set(DAY_OF_MONTH, 1);
		int todayOfWeek = cal.get(DAY_OF_WEEK);
		cal.add(DAY_OF_MONTH, cal.getFirstDayOfWeek() - todayOfWeek);	//DATE
		//so reset the cal to midnight
		//setMidnight(cal);
		//cal.set(HOUR_OF_DAY, 0);
		while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear())
				&& cal.get(YEAR) <= month.getYear()) {
			List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
			cells.add(weekCells);
			for (int c = 0; c < 7; c++) {
				Date date = cal.getTime();
				boolean isPreviousMonth = (cal.get(YEAR) < month.getYear()) ||  
						(cal.get(MONTH) < month.getMonth() && cal.get(YEAR) == month.getYear());
				boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
				boolean isNextMonth = (cal.get(YEAR) > month.getYear()) ||
						(cal.get(MONTH) > month.getMonth() && cal.get(YEAR) == month.getYear());
				boolean isSelected = isCurrentMonth && sameDate(cal, selectedDate);
				boolean isToday = sameDate(cal, today) && isCurrentMonth;
				int value = cal.get(DAY_OF_MONTH);
				MonthCellDescriptor cell = new MonthCellDescriptor(date, isPreviousMonth,
						isCurrentMonth, isNextMonth, isSelected, isToday, value);
				weekCells.add(cell);
				cal.add(DAY_OF_MONTH, 1);
				//cal.set(HOUR_OF_DAY, 0);
			}
		}
		return cells;
	}
 
	private static boolean sameDate(Calendar cal, Calendar selectedDate) {
		return cal.get(MONTH) == selectedDate.get(MONTH)
				&& cal.get(YEAR) == selectedDate.get(YEAR)
				&& cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
	}
	
	/*private static boolean sameMonth(Date cal, Date selectedDate) {
		return cal.getYear() == selectedDate.getYear() &&
				cal.getMonth() == selectedDate.getMonth();
	}*/
	
	public void setFlipperListener(FlipperListener flipperListener) {
		this.flipperListener = flipperListener;
		if(flipperListener != null){
			flipperListener.onStartFlipping(getCurrentCal());
		}
	}

	public void setCalendarListener(OnCalendarListener calendarListener) {
		this.calendarListener = calendarListener;
	}

	@Override
	protected void onAnimationStart(int type) {
//		if (null == flipperListener)
//			return;
//		if (type == TYPE_PREVIOUS) {
//			flipperListener.onStartFlipping(getPreCalendar());
//		} else if (type == TYPE_NEXT) {
//			flipperListener.onStartFlipping(getNextCalendar());
//		}
	}
	
	@Override
	public void showPrevious() {
		// when nextView/previousView is null, it means not been initialized by
		// lazy-loading
		if (getPreviousView() == null)
			return;
		getPreviousView().showLastRowViewIfNecessary();
		super.showPrevious();
	}
	
	@Override
	public void showNext() {
		getNextView().showLastRowViewIfNecessary();
		super.showNext();
	}
	
	@Override
	protected void onAnimationEnd(MonthView preView, MonthView nextView, int type) {
		if(type == TYPE_PREVIOUS){
			currentCal = getPreCalendar();
			genMonthDescriptor(getPreCalendar(), preView);
			genMonthDescriptor(getNextCalendar(), nextView);
		}else if(type == TYPE_NEXT){
			currentCal = getNextCalendar();
			genMonthDescriptor(getPreCalendar(), preView);
			genMonthDescriptor(getNextCalendar(), nextView);
		}else if(type == TYPE_CURRENT){		
			//first initialized, its pre/next descriptors is already generated!
		}
		
		if (flipperListener != null) {
			flipperListener.onStartFlipping(currentCal);
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				requestShowDataAsync();
			}
		}).start();
		
		requestLayout();
	}

	private void requestShowDataAsync() {
		List<Date> monthDates = getCurrentMonthDates();
		if (null != calendarListener && !monthDates.isEmpty()) {
			final List<Date> dateList = calendarListener.onMonthChanged(monthDates);
			if (null != dateList && !dateList.isEmpty()) {
				post(new Runnable() {
					@Override
					public void run() {
						MonthView curView = getShowingView();
						curView.showHasDataFlag(dateList);
						
						if(isFirstInit){
							handleClickedIfHasExtraValue(selectedCal.getTime());
						}
					}
				});
			}
		}
	}
	
	//fragment onstart
	public void refreshCurrentDate() {
		List<Date> monthDates = getCurrentMonthDates();
		if (null != calendarListener && !monthDates.isEmpty()) {
			final List<Date> dateList = calendarListener.onMonthChanged(monthDates);
			//FIX bug CSAND-658
			//if (null != dateList && !dateList.isEmpty()) {
				post(new Runnable() {
					@Override
					public void run() {
						MonthView curView = getShowingView();
						curView.showHasDataFlag(dateList);
						
						handleClickedIfHasExtraValue(selectedCal.getTime());
					}
				});
			//}
		}
	}
	
	public Calendar getPreCalendar() {
		return CalendarUtility.getPreMonthCalendar(currentCal);
	}

	public Calendar getNextCalendar() {
		return CalendarUtility.getNextMonthCalendar(currentCal);
	}

	/** return the min date of given calendar */
	public Date getMinDate(final Calendar cal) {
		int dayOfMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance();
		c.setTime(cal.getTime());
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return c.getTime();
	}

	/** return the max date of given calendar */
	public Date getMaxDate(final Calendar cal) {
		int dayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance();
		c.setTime(cal.getTime());
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return c.getTime();
	}
	
}