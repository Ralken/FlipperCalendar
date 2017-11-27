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
 */
package cn.ralken.android.calendar;

import java.util.Calendar;

import static java.util.Calendar.DAY_OF_MONTH;

public class CalendarUtility {
	
	public static Calendar getPreMonthCalendar(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cal.getTimeInMillis());
		calendar.set(DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, -1);
		return calendar;
	}
	
	public static Calendar getNextMonthCalendar(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cal.getTimeInMillis());
		calendar.add(Calendar.MONTH, 1);
		calendar.set(DAY_OF_MONTH, 1);
		return calendar;
	}
	
	public static Calendar getPreWeekCalendar(Calendar cal){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cal.getTimeInMillis());
		calendar.add(Calendar.DAY_OF_YEAR, -7);
		return calendar;
	}
	
	public static Calendar getNextWeekCalendar(Calendar cal){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cal.getTimeInMillis());
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		return calendar;
	}
	
	public static Calendar getPreYearCalendar(Calendar cal){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cal.getTimeInMillis());
		calendar.add(Calendar.YEAR, -1);
		return calendar;
	}
	
	public static Calendar getNextYearCalendar(Calendar cal){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cal.getTimeInMillis());
		calendar.add(Calendar.YEAR, 1);
		return calendar;
	}
	
	public static Calendar getFirstDateOfWeek(Calendar calendar){
		Calendar cal = Calendar.getInstance();  
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setTime(calendar.getTime());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);  
		return cal;
	}
	
	public static Calendar getLastDateOfWeek(Calendar calendar){
		Calendar cal = Calendar.getInstance();  
		cal.setFirstDayOfWeek(Calendar.SUNDAY);
		cal.setTime(calendar.getTime());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);  
		return cal;
	}
	
	public static Calendar calFirstDayOfMonth(Calendar calendar){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(calendar.getTimeInMillis());
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
		return cal;
	}

	public static Calendar calLastDayOfMonth(Calendar calendar){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(calendar.getTimeInMillis());
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		return cal;
	}
	
	/** return the first date of given calendar */
	/*public static Date getFirstDate(final Calendar startCal) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startCal.getTime());
		cal.set(DAY_OF_MONTH, 1);
		int firstDayOfWeek = cal.get(DAY_OF_WEEK);
		cal.add(DATE, cal.getFirstDayOfWeek() - firstDayOfWeek);
		return cal.getTime();
	}*/

	/** return the first date of given calendar */
	/*public static Date getLastDate(final Calendar startCal) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startCal.getTime());
		cal.set(DAY_OF_MONTH, startCal.getActualMaximum(DAY_OF_MONTH));
		int dayOfWeek = cal.get(DAY_OF_WEEK);
		cal.add(DATE, 7 - dayOfWeek);
		return cal.getTime();
	}*/
	
}
