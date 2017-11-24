/** Copyright Ralken Liao

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Ralken Liao
 * @date  
 */

package cn.ralken.android.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

/**
 * 
 * @author liaoralken
 */
public abstract class AnimationFlipper<T extends View> extends ViewFlipper implements AnimationListener{
	private static final int FLIPPER_DURATION = 600;
	protected static final int TYPE_PREVIOUS = 0x00;
	protected static final int TYPE_NEXT = 0x01;
	protected static final int TYPE_CURRENT = 0x02;
	private int mDirection = TYPE_CURRENT;
	
	protected LayoutInflater inflater = null;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private T previousView;
	private T middleView;
	private T nextView;
	protected T showingView;
	
	private boolean isAnimating = false;
	
	public AnimationFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.inflater = LayoutInflater.from(context);
		init();
	}
	
	private void init() {
		setAnimationDirection(0, FLIPPER_DURATION);
	}

	/**
	 * 
	 * @param orientation <br>0: up-bottom by default <br> 1:left-right
	 * @param speed how long an animation lasts
	 */
	protected void setAnimationDirection(int orientation, long speed) {
		if(orientation == 1){
			// left-right
			slideLeftIn = AnimationUtils.loadAnimation(getContext(), R.anim.push_right_in);
			slideLeftOut = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out);
			slideRightIn = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
			slideRightOut = AnimationUtils.loadAnimation(getContext(), R.anim.push_right_out);
		}else {
			// up-bottom
			slideLeftIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_in);
			slideLeftOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left_out);
			slideRightIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right_in);
			slideRightOut = AnimationUtils.loadAnimation(getContext(),R.anim.slide_right_out);
		}

		slideLeftIn.setAnimationListener(this);
		slideRightIn.setAnimationListener(this);
		
		//just for testing
		slideLeftIn.setDuration(speed);
		slideLeftOut.setDuration(speed);
		slideRightIn.setDuration(speed);
		slideRightOut.setDuration(speed);
	}
	
	//FIXME unused this method yet for lazy-loading
	/***
	 * Set up the children views for position, sub class should call this method
	 * to do initilization.
	 * @param pre the first view.
	 * @param middle the second view.
	 * @param next the next view.
	 */
	protected void setUpContainers(T pre, T middle, T next) {
		this.previousView = pre;
		this.middleView = middle;
		this.nextView = next;
		/** record the first view as showwing */
		this.showingView = pre;
		
		addView(pre);
		addView(middle);
		addView(next);
	}
	
	protected void setUpCurrentView(T current) {
		this.previousView = current;
		/** record the first view as showwing */
		this.showingView = current;

		addView(current);
	}
	
	protected void setUpPreAndNextView(T middle, T next){
		this.middleView = middle;
		this.nextView = next;
		
		addView(middle);
		addView(next);
	}
	
	/** return the currently showwing child view, or null if hasn't showwing yet */
	public T getShowingView() {
		return showingView;
	}
	
	protected T getPreviousView(){
		if(showingView == previousView){
			return nextView;
		}else if(showingView == middleView){
			return previousView;
		} else {
			return middleView;
		}
	}
	
	protected T getNextView(){
		if(showingView == previousView){
			return middleView;
		}else if(showingView == middleView){
			return nextView;
		} else {
			return previousView;
		}
	}
	
	/** recorder the view which is currently showwing. */
	public void setShowingView(T showingView) {
		this.showingView = showingView;
	}
	
	public void setDirection(int direction) {
		this.mDirection = direction;
	}
	
	public boolean isAnimating() {
		return isAnimating;
	}

	@Override
	public void showNext() {
		if (isAnimating) {
			return;
		}
		setInAnimation(slideLeftIn);
		setOutAnimation(slideLeftOut);
		setDirection(TYPE_NEXT);
		this.isAnimating = true;
		super.showNext();
	}
	
	@Override
	public void showPrevious() {
		if (isAnimating) {
			return;
		}
		setInAnimation(slideRightIn);
		setOutAnimation(slideRightOut);
		setDirection(TYPE_PREVIOUS);
		this.isAnimating = true;
		super.showPrevious();
	}
	
	@Override public void onAnimationRepeat(Animation animation) {}

	@Override public void onAnimationStart(Animation animation) {
		onAnimationStart(mDirection);
	}
	
	@Override public void onAnimationEnd(Animation animation){
		switch (mDirection) {
		case TYPE_PREVIOUS:
			setShowingView(getPreviousView());
			break;
		case TYPE_NEXT:
			setShowingView(getNextView());
			break;
		}
		
		this.isAnimating = false;
		
		onAnimationEnd(getPreviousView(), getNextView(), mDirection);
	}
	
	protected abstract void onAnimationStart(int type);
	
	protected abstract void onAnimationEnd(T preView, T nextView, int type);
}
