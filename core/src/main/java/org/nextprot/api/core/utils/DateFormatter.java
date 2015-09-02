package org.nextprot.api.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Format Date according to a given precision (support day, month or year precision)
 *
 * Created by fnikitin on 02/09/15.
 */
public class DateFormatter {

    private final static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private final static SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    private final static SimpleDateFormat YEAR_MONTH_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public final static int DAY_PRECISION = 10;
    public final static int MONTH_PRECISION = 30;
    public final static int YEAR_PRECISION = 60;

    /**
     * Format date according to precision
     *
     * @param date the date to format
     * @param cvDatePrecisionId the precision id (10 (day precision), 30 (month precision), 60 (year precision))
     * @return String year format by default
     */
    public String format(Date date, int cvDatePrecisionId) {

        if (date != null) {

            if (cvDatePrecisionId != 1)
               return getDateFormat(cvDatePrecisionId).format(date);
        }

        return "";
    }

    /**
     * @param cvDatePrecisionId date precision id
     * @return year format by default
     */
    private SimpleDateFormat getDateFormat(int cvDatePrecisionId) {

        switch (cvDatePrecisionId) {

            case 10:
                return YEAR_MONTH_DATE_FORMAT;
            case 30:
                return YEAR_MONTH_FORMAT;
            case 60:
                return YEAR_FORMAT;
            default:
                return YEAR_FORMAT;
        }
    }
}
