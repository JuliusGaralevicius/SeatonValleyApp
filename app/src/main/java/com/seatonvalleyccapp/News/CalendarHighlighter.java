package com.seatonvalleyccapp.News;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.seatonvalleyccapp.R;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by julius on 10/04/2018.
 */

public class CalendarHighlighter implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();
    private final Drawable highlightDrawable;
    private static final int color = Color.parseColor("#228BC34A");
    private HashMap<Integer, Integer> schedule;

    public CalendarHighlighter(HashMap<Integer, Integer> schedule) {

        ShapeDrawable badge = new ShapeDrawable (new OvalShape());
        badge.setIntrinsicWidth (50);
        badge.setIntrinsicHeight (50);
        badge.getPaint().setColor(Color.rgb(115,46,119));

        highlightDrawable = badge;

        this.schedule = schedule;
    }

    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return schedule.get(weekOfYear) == dayOfWeek;

    }

    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(highlightDrawable);
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
    }
}
