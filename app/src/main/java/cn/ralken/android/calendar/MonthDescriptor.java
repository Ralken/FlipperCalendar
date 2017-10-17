// Copyright 2012 Square, Inc.
package cn.ralken.android.calendar;

class MonthDescriptor {
  private final int month;
  private final int year;
  
  
  public MonthDescriptor(int month, int year) {
    this.month = month;
    this.year = year;
  }

  public int getMonth() {
    return month;
  }

  public int getYear() {
    return year;
  }
  
  @Override public String toString() {
    return "MonthDescriptor{"
    	+ ", month="
        + month
        + ", year="
        + year
        + '}';
  }
}
