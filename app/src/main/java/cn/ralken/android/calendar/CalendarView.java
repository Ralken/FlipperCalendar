package cn.ralken.android.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.coresuite.android.modules.OnViewPageInterceptListener;
import com.coresuite.android.utilities.DrawableCache;
import com.coresuite.android.utilities.DrawableCache.DrawableMode;
import com.coresuite.android.widgets.calendar.MonthFlipperView.FlipperListener;
import com.coresuite.android.widgets.calendar.MonthFlipperView.OnCalendarListener;
import com.coresuite.coresuitemobile.R;

@SuppressLint("SimpleDateFormat")
public class CalendarView extends FrameLayout implements OnClickListener {
	private final DateFormat monthNameFormat;
	private final DateFormat weekdayNameFormat;
	
	private MonthFlipperView calFlipper;
	private OnViewPageInterceptListener mOnViewPageInterceptListener;
	private Button mInfoBtn;
	private OnInfoClickListener mOnInfoClickListener;
	private TextView calendarTitleView;
	private TextView calendarTempTitleView;
	private Button mInfoTempBtn;
	private View mTitleLayout;
	private View mTmpTitleLayout;
	private ImageView mPreviousMonth;
	private ImageView mNextMonth;
	private static final int ANIMATION_DURATION = 400;
	
	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		monthNameFormat = new SimpleDateFormat("MMMM yyyy");
		weekdayNameFormat = new SimpleDateFormat("EEE");
		
		//set up content view
		inflate(context, R.layout.calendar_widget, this);
		
		initViews();
	}

	private void initViews() {
		initWeekHeaderRow();
		
		calendarTitleView = (TextView) findViewById(R.id.title);
		calendarTempTitleView = (TextView) findViewById(R.id.mTmpTitle);
		mTitleLayout = findViewById(R.id.mTitleLayout);
		mTmpTitleLayout = findViewById(R.id.mTmpTitleLayout);
		mPreviousMonth = (ImageView)findViewById(R.id.mPreviousMonth);
		mPreviousMonth.setOnClickListener(this);
		mPreviousMonth.setImageDrawable((DrawableCache.getInstance().getDrawable(
				R.drawable.popover_arrow_left_calendar, DrawableMode.GRAY, DrawableMode.BLACK)));
		
		mNextMonth = (ImageView)findViewById(R.id.mNextMonth);
		mNextMonth.setOnClickListener(this);
		mNextMonth.setImageDrawable((DrawableCache.getInstance().getDrawable(
				R.drawable.popover_arrow_right_calendar, DrawableMode.GRAY, DrawableMode.BLACK)));
		
		mInfoBtn = (Button)findViewById(R.id.mInfoBtn);
		mInfoTempBtn = (Button)findViewById(R.id.mTmpInfoBtn);
		calFlipper = ((MonthFlipperView) findViewById(R.id.flipper_container)); 
	}
	
	public void setSelectedDate(Calendar selCal, OnCalendarListener calendarListener) {
		calFlipper.setCalendarListener(calendarListener);
		calFlipper.init(selCal.getTime());
		calFlipper.setFlipperListener(new FlipperListener() {
			@Override
			public void onStartFlipping(Calendar targetCal) {
				calendarTitleView.setText(monthNameFormat.format(calFlipper.getCurrentCal().getTime()));
			}

			@Override
			public void onMonthChange(boolean isNextMonth) {
				startTitleAnimation(isNextMonth);
			}
		});
	}
	
	private void initWeekHeaderRow() {
		Calendar today = Calendar.getInstance();
		today.setFirstDayOfWeek(Calendar.SUNDAY);
		
		final int todayOfWeek = today.get(Calendar.DAY_OF_WEEK);
		int firstDayOfWeek = today.getFirstDayOfWeek();
		final CalendarRowView headerRow = (CalendarRowView) findViewById(R.id.weekHeaderRow);
		headerRow.setIsHeaderRow(true);
		for (int offset = 0; offset < 7; offset++) {
			today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
			final TextView textView = (TextView) headerRow.getChildAt(offset);
			textView.setText(weekdayNameFormat.format(today.getTime()));
		}
		today.set(Calendar.DAY_OF_WEEK, todayOfWeek);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.mPreviousMonth && calFlipper.isPreMonthPrepared()) {
			Date selDate = calFlipper.getMaxDate(calFlipper.getPreCalendar());
			calFlipper.setPreMonthSelected(selDate);
			calFlipper.handleClickedIfHasExtraValue(selDate);
			calFlipper.showPrevious();
			startTitleAnimation(false);
		} else if (v.getId() == R.id.mNextMonth && calFlipper.isNextMonthPrepared()) {
			Date selDate = calFlipper.getMinDate(calFlipper.getNextCalendar());
			calFlipper.setNextMonthSelected(selDate);
			calFlipper.handleClickedIfHasExtraValue(selDate);
			calFlipper.showNext();
			startTitleAnimation(true);
		}else if (v.getId() == R.id.mInfoBtn || v.getId() == R.id.mTmpInfoBtn) {
			if (mOnViewPageInterceptListener != null) {
				mOnInfoClickListener.onInfoBtnClick(v);
			}
		}
	}
	
	private void startTitleAnimation(boolean isNext) {
		mPreviousMonth.setEnabled(false);
		mNextMonth.setEnabled(false);
		final String targetCal = monthNameFormat.format(isNext ? calFlipper.getNextCalendar().getTime() : calFlipper.getPreCalendar().getTime());
		Animation titleAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, 
																						Animation.RELATIVE_TO_SELF, 0,
																						Animation.RELATIVE_TO_SELF, 0, 
																						Animation.RELATIVE_TO_PARENT, isNext ? -1.0f : 1.0f);
		titleAnimation.setDuration(ANIMATION_DURATION);
		titleAnimation.setInterpolator(new DecelerateInterpolator());
		titleAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				calendarTitleView.setText(targetCal);
//				calendarTempTitleView.setVisibility(View.GONE);
//				if (mOnInfoClickListener != null) {
//					mInfoTempBtn.setVisibility(View.GONE);
//				}
//				mTmpTitleLayout.setVisibility(View.GONE);
			}
		});
		
		
		Animation tempTitleAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, 
																								Animation.RELATIVE_TO_SELF, 0,
																								Animation.RELATIVE_TO_PARENT, isNext ? 1.0f : -1.0f, 
																								Animation.RELATIVE_TO_SELF, 0);
		tempTitleAnimation.setDuration(ANIMATION_DURATION);
		tempTitleAnimation.setFillAfter(true);
		tempTitleAnimation.setInterpolator(new DecelerateInterpolator());
		tempTitleAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				calendarTempTitleView.setText(targetCal);
				calendarTempTitleView.setVisibility(View.VISIBLE);
				if (mOnInfoClickListener != null) {
					mInfoTempBtn.setVisibility(View.VISIBLE);
				}
				mTmpTitleLayout.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
//				calendarTempTitleView.setVisibility(View.GONE);
//				if (mOnInfoClickListener != null) {
//					mInfoTempBtn.setVisibility(View.GONE);
//				}
//				mTmpTitleLayout.setVisibility(View.GONE);
				mPreviousMonth.setEnabled(true);
				mNextMonth.setEnabled(true);
			}
		});
		mTitleLayout.clearAnimation();
		mTitleLayout.startAnimation(titleAnimation);
		mTmpTitleLayout.clearAnimation();
		mTmpTitleLayout.startAnimation(tempTitleAnimation);
	}
	
	public List<Date> getCurrentMonthDates() {
		return calFlipper.getCurrentMonthDates();
	}
	
	public Date getSelectDate(){
		return calFlipper.getSelectDate();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if(mOnViewPageInterceptListener != null){
				mOnViewPageInterceptListener.requestDisallowInterceptTouchEvent(true);
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	public void setonViewPageInterceptorListener(
			OnViewPageInterceptListener mOnViewPageInterceptListener) {
		this.mOnViewPageInterceptListener = mOnViewPageInterceptListener;
	}
	
	public void setOnInfoBtnClickListener(OnInfoClickListener listener){
		mInfoBtn.setVisibility(View.VISIBLE);
		mInfoTempBtn.setOnClickListener(this);
		mInfoBtn.setOnClickListener(this);
		this.mOnInfoClickListener = listener;
	}
	
	public interface OnInfoClickListener{
		void onInfoBtnClick(View view);
	}
}
