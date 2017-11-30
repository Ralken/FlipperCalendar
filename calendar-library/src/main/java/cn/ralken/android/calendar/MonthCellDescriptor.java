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

import java.util.Date;

/** Describes the state of a particular date cell in a {@link MonthView}. */
class MonthCellDescriptor {
	private final Date date;
	private final int value;
	private final boolean isPreviousMonth;
	private final boolean isCurrentMonth;
	private final boolean isNextMonth;
	private final boolean isToday;
	
	private boolean isSelected;
	private boolean hasExtrarValue = false;
	
	MonthCellDescriptor(Date date, boolean previousMonth, boolean currentMonth, boolean nextMonth, boolean selected,
			boolean today, int value) {
		this.date = date;
		isPreviousMonth = previousMonth;
		isCurrentMonth = currentMonth;
		isNextMonth = nextMonth;
		isSelected = selected;
		isToday = today;
		
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public boolean isPreviousMonth() {
		return isPreviousMonth;
	}

	public boolean isNextMonth() {
		return isNextMonth;
	}

	public boolean isCurrentMonth() {
		return isCurrentMonth;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}
	
	public boolean isToday() {
		return isToday;
	}

	public int getValue() {
		return value;
	}
	
	public boolean hasExtrarValue() {
		return hasExtrarValue;
	}

	public void setHasExtrarValue(boolean hasExtrarValue) {
		this.hasExtrarValue = hasExtrarValue;
	}
	
	@Override
	public String toString() {
		return "MonthCellDescriptor{" + "date=" + date + ", value=" + value
				+ ", isCurrentMonth=" + isCurrentMonth + ", isSelected="
				+ isSelected + ", isToday=" + isToday + '}';
	}
}
