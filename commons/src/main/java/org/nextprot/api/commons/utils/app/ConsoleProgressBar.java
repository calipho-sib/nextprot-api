package org.nextprot.api.commons.utils.app;

import java.io.PrintStream;
import java.util.Objects;

/**
 * A simple terminal progress bar.
 * <p/>
 * <h4>Description</h4>
 * <p>
 * A progress bar typically communicates the progress of some work by displaying
 * its percentage of completion and possibly a textual display of this
 * percentage.
 * </p>
 * <p/>
 * <p>
 * A progression bar has 2 modes of execution. Depending on the
 * <em>a priori</em> knowledge of the total steps, we have a Determinate mode or
 * an Indeterminate mode.
 * </p>
 * <p/>
 * <p>
 * The terminal progression bar is constituted of 2 parts:
 * <ol>
 * <li>the current processed step (over all steps) found in the Left Margin (LM)
 * </li>
 * <li>the Progression Bar (PB) itself</li>
 * </ol>
 * Note that each LM and PB space lengths together with the length of the
 * roaming segment in indeterminate mode are configurable. It is even possible
 * to set the refreshing period of progression bar in this last mode.
 * </p>
 * <p/>
 * <h4>Indeterminate Mode</h4>
 * <p/>
 * <pre>
 *   LM      PB
 * < -- >< -------- >
 *    0  [=====     ]
 *    1  [ =====    ]
 *    2  [  =====   ]
 *    3  [   =====  ]
 *    4  [    ===== ]
 *    5  [     =====]
 *    6  [    ===== ]
 *    7  [   =====  ]
 *   ...
 * </pre>
 * <p/>
 * <h4>Determinate Mode</h4>
 * <p/>
 * <pre>
 *   LM      PB
 * < -- >< -------- >
 *  0/10 [          ]
 *  1/10 [=         ]
 *  2/10 [==        ]
 *  3/10 [===       ]
 *  4/10 [====      ]
 *  5/10 [=====     ]
 *  6/10 [======    ]
 *  7/10 [=======   ]
 *  8/10 [========  ]
 *  9/10 [========= ]
 * 10/10 [==========]
 * </pre>
 *
 * @author nikitin
 * @version 1.0
 */
 public class ConsoleProgressBar {

    /**
     * the current number of completed steps
     */
    private int value;

    /**
     * the minimum bar value
     */
    private int minimum;

    /**
     * the maximum bar value
     */
    private int maximum;

    /**
     * true if the maximum value is unknown
     */
    private boolean isIndeterminate;

    /**
     * true if task has been completed
     */
    private boolean hasCompleted;

    private View view;

    private ConsoleProgressBar() {

        this.view = new View();
    }

    public static ConsoleProgressBar determinated(int maximum) {

        ConsoleProgressBar pb = new ConsoleProgressBar();

        pb.setMinimum(0);
        pb.setMaximum(maximum);

        pb.setIndeterminate(false);

        return pb;
    }

    public static ConsoleProgressBar indeterminated() {

        ConsoleProgressBar pb = new ConsoleProgressBar();

        pb.setMinimum(0);
        pb.setIndeterminate(true);

        return pb;
    }

    public void setView(View view) {

        this.view = view;
    }

    public void setTaskName(String name) {

        view.setTaskName(name);
    }


    public final void setMinimum(int minimum) {

        this.minimum = minimum;
    }

    public final void setMaximum(int maximum) {

        this.maximum = maximum;

        view.setLeftMarginLength(String.valueOf(maximum).length() + 1);

        isIndeterminate = false;
    }

    public final void setMaximumDoNoResetLeftMargin(int maximum) {

        this.maximum = maximum;

        isIndeterminate = false;
    }

    public void setIndeterminate(boolean bool) {

        this.isIndeterminate = bool;

        // init indeterminate bar
        if (bool) {
            start();
        }
    }

    /**
     * Initialize progress bar (mandatory to restart bar in Indeterminate mode)
     */
    public void start() {

        hasCompleted = false;

        if (isIndeterminate) {

            minimum = value = 0;
            maximum = Integer.MAX_VALUE;

            view.resetIndeterminateBar();
        } else {

            value = minimum;
        }
    }

    /**
     * Interrupt the task (mandatory to complete Inderminate mode) <h4>Note 1</h4>
     * If you want to restart another progression, you will have to call start()
     * first.
     */
    public void stop() {

        if (!isIndeterminate) {

            if (value < maximum) {

                view.ps.println(" " + view.incompletedMessage);
            }
            value = minimum;
        } else {

            if (!hasCompleted) {

                this.hasCompleted = true;

                view.refreshIndeterminateBar(value, hasCompleted);
            }

            value = 0;
        }

    }

    public void setValue(int completed) {

        if (completed < minimum) {

            value = minimum;
        } else if (completed > maximum) {

            value = maximum;
        } else {

            this.value = completed;
        }

        if (isIndeterminate) {

            view.refreshIndeterminateBar(value, hasCompleted);
        } else {

            view.refreshDeterminateBar(value, maximum);
        }
    }

    public void incrementValue() {

        setValue(value + 1);
    }

    public int getValue() {

        return value;
    }

    public final boolean isIndeterminate() {

        return isIndeterminate;
    }

    public void setPrintStream(PrintStream ps) {

        view.setPrintStream(ps);
    }

    public static class View {

        /**
         * the default bar length
         */
        private static final int DEFAULT_BAR_LENGTH = 50;

        /**
         * the default length of indeterminated segment
         */
        private static final int INDETERMINATE_SEGMENT_LENGTH_RATIO = 2;

        /**
         * the default refresh period
         */
        private static final int DEFAULT_INDETERMINATED_BAR_REFRESH_PERIOD = 2;

        private static final int DEFAULT_LEFT_MARGIN_LENGTH = 5;

        /**
         * the default bar char segment
         */
        private static final char DEFAULT_SEGMENT = '=';

        private static final String DEFAULT_DONE_MESSAGE;

        static {
            DEFAULT_DONE_MESSAGE = "Done";
        }

        private static final String DEFAULT_UNDONE_MESSAGE = "Incomplete";

        /**
         * progress bar length in chars
         */
        private int barLength;

        /**
         * the bar char display element
         */
        private final char segment;

        /**
         * the segment length of indeterminate progress bar
         */
        private int indeterminateSegmentLength;

        /**
         * the message displayed when process is over
         */
        private String completedMessage;

        /**
         * the message displayed when process has been interrupted
         */
        private String incompletedMessage;

        /**
         * the period of bar refresh for indeterminate bar
         */
        private int barRefreshPeriod;

        /**
         * the number of update calls for indeterminate bar
         */
        private int updateCount;

        /**
         * the last indetermined bar status
         */
        private StringBuilder lastIndeterminedBarSb;

        /**
         * the left margin length
         */
        private int leftMarginLength;

        /**
         * the task name appearing in the left margin
         */
        private String taskName;

        /**
         * the current position of cursor for indeterminate bar
         */
        private int currentCursorPosition;

        private boolean isCurrentTowardPositiveInfinity;

        /**
         * the print stream for progress bar display
         */
        private PrintStream ps;

        public View() {

            barLength = DEFAULT_BAR_LENGTH;
            segment = DEFAULT_SEGMENT;

            completedMessage = DEFAULT_DONE_MESSAGE;
            incompletedMessage = DEFAULT_UNDONE_MESSAGE;

            ps = System.out;
        }

        public void resetIndeterminateBar() {

            currentCursorPosition = 0;
            isCurrentTowardPositiveInfinity = true;
            updateCount = 0;
            computeIndeterminateSegmentlength();

            if (barRefreshPeriod == 0) {

                barRefreshPeriod = View.DEFAULT_INDETERMINATED_BAR_REFRESH_PERIOD;
            }

            if (leftMarginLength == 0) {

                leftMarginLength = View.DEFAULT_LEFT_MARGIN_LENGTH;
            }
        }

        /**
         * Set the left margin length (with completion infos).
         *
         * @param length the left margin length.
         */
        public void setLeftMarginLength(int length) {

            this.leftMarginLength = length;
        }

        /**
         * Set the progress bar length.
         *
         * @param length the given length.
         */
        public void setBarLength(int length, boolean isIndetermined) {

            this.barLength = length;

            if (isIndetermined) {

                computeIndeterminateSegmentlength();
            }
        }

        /**
         * Set the period of bar animation refresh while the bar is in indeterminate
         * mode.
         *
         * @param period the period of refresh for cursor animation.
         */
        public void setRefreshBarPeriod(int period) {

            barRefreshPeriod = period;
        }

        /**
         * Set the length of segment constantly animated in indeterminate mode.
         *
         * @param length the given length (&gt; 0 and &lt; bar len).
         */
        public void setSegmentLength(int length) {

            if (length <= 0) {

                indeterminateSegmentLength = 1;

            } else if (length >= barLength) {

                indeterminateSegmentLength = barLength - 1;
            } else {

                indeterminateSegmentLength = length;
            }
        }

        private void computeIndeterminateSegmentlength() {

            indeterminateSegmentLength =
                    barLength / INDETERMINATE_SEGMENT_LENGTH_RATIO;
        }

        public void setTaskName(String name) {

            this.taskName = name;
        }

        public void setDoneMessage(String message) {

            this.completedMessage = message;
        }

        public void setIncompleteMessage(String message) {

            this.incompletedMessage = message;
        }

        private void updateLeftMargin(PrintStream out, int value, int maximum) {

            // Note: "carriage return" returns to the beginning of the line
            out.append("\r");

            if (taskName != null && taskName.length() > 0) {

                out.append(taskName).append(":");
            }

            if (maximum == 0) {

                out.append(String.format(" %" + leftMarginLength + "s", value));
            } else {

                out.append(String.format(" %" + leftMarginLength + "s", value + "/" + maximum));
            }
        }

        private void refreshDeterminateBar(int value, int maximum) {

            if (maximum == 0) {

                throw new IllegalStateException("maximum is not defined");
            }

            double progressPercentage = (double) value / maximum;

            updateLeftMargin(ps, value, maximum);

            ps.print(" [");

            int i = 0;
            for (; i < (int) (progressPercentage * barLength); i++) {
                ps.print(segment);
            }
            for (; i < barLength; i++) {
                ps.print(" ");
            }
            ps.print("]");

            if (value == maximum) {
                ps.println(" " + completedMessage);
            }
        }

        private void refreshIndeterminateBar(int value, boolean hasCompleted) {

            updateLeftMargin(ps, value, 0);

            int i = 0;

            if (hasCompleted) {

                ps.print(" [");
                for (; i < barLength; i++) {
                    ps.print(segment);
                }
                ps.print("]");
                ps.println(" " + completedMessage);

                return;
            } else if (updateCount % barRefreshPeriod == 0) {
                lastIndeterminedBarSb = new StringBuilder(" [");

                for (; i < currentCursorPosition; i++) {
                    lastIndeterminedBarSb.append(" ");
                }

                for (int j = 0; j < indeterminateSegmentLength; j++) {
                    lastIndeterminedBarSb.append(segment);
                }

                for (; i < barLength - indeterminateSegmentLength; i++) {
                    lastIndeterminedBarSb.append(" ");
                }
                lastIndeterminedBarSb.append("]");

                /** has current cursor reached boundaries ? */
                if (currentCursorPosition == barLength - indeterminateSegmentLength) {

                    isCurrentTowardPositiveInfinity = false;
                } else if (currentCursorPosition == 0) {

                    isCurrentTowardPositiveInfinity = true;
                }

                /** next direction */
                if (isCurrentTowardPositiveInfinity) {

                    currentCursorPosition++;
                } else {

                    currentCursorPosition--;
                }
            }

            ps.print(lastIndeterminedBarSb);

            updateCount++;
        }

        /**
         * Set the print stream for bar display.
         *
         * @param ps the output stream.
         */
        public void setPrintStream(PrintStream ps) {

            Objects.requireNonNull(ps);

            this.ps = ps;
        }

        public PrintStream getPrintStream() {

            return ps;
        }
    }
}