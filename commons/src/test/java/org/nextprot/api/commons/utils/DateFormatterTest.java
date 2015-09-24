package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fnikitin on 02/09/15.
 */
public class DateFormatterTest {

    @Test
    public void testFormatAtDayPrecision() throws Exception {

        DateFormatter formatter = new DateFormatter();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.OCTOBER, 13);

        Date date = calendar.getTime();

        Assert.assertEquals("2015-10-13", formatter.format(date, DateFormatter.DAY_PRECISION));
    }

    @Test
    public void testFormatAtMonthPrecision() throws Exception {

        DateFormatter formatter = new DateFormatter();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.OCTOBER, 13);

        Date date = calendar.getTime();

        Assert.assertEquals("2015-10", formatter.format(date, DateFormatter.MONTH_PRECISION));
    }

    @Test
    public void testFormatAtYearPrecision() throws Exception {

        DateFormatter formatter = new DateFormatter();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.OCTOBER, 13);

        Date date = calendar.getTime();

        Assert.assertEquals("2015", formatter.format(date, DateFormatter.YEAR_PRECISION));
    }

    @Test
    public void testFormatAtUnsupportedPrecision() throws Exception {

        DateFormatter formatter = new DateFormatter();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.OCTOBER, 13);

        Date date = calendar.getTime();

        Assert.assertEquals("", formatter.format(date, 1));
    }

    @Test
    public void testNullDateFormat() throws Exception {

        DateFormatter formatter = new DateFormatter();

        Assert.assertEquals("", formatter.format(null, DateFormatter.YEAR_PRECISION));
    }
}