package ua.edu.sumdu.j2se.bublyk.tasks;

import java.util.Objects;

public class Task implements Cloneable {
    private String title;
    private int time;
    private int start;
    private int end;
    private int interval;
    private boolean active;
    private boolean repeated;

    /**
     * Constructor that creates an inactive task
     * which is executed in the set period of time
     * without a repeating and has the given name.
     *
     * @param title  the task name
     * @param time  the notification time
     *
     * @throws IllegalArgumentException if "time" is negative
     */
    public Task(String title, int time) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException("Time must equal to zero or be greater than it.");
        }
        this.title = title;
        this.time = time;
        this.active = false;
        this.repeated = false;
    }

    /**
     * Constructor that creates an inactive task
     * which is executed in the set period of time (both start and end inclusive)
     * with the seated interval and has the given name.
     *
     * @param title  the task name
     * @param start  the notification start time
     * @param end  the notification end time
     * @param interval  Time interval after which it is necessary to repeat task notification.
     *
     * @throws IllegalArgumentException if timestamps are negative, "start" is greater than "end"
     * or "interval" isn't positive
     */
    public Task(String title, int start, int end, int interval) throws IllegalArgumentException {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (start > end) {
            throw new IllegalArgumentException("The end of task must be greater than the start of task.");
        }
        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero.");
        }
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.active = false;
        this.repeated = true;
    }

    /**
     * Getter for the task title.
     *
     * @return the task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for a name of task.
     *
     * @param title  task name
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for a task status.
     *
     * @return a task status
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for a task status.
     *
     * @param active  the task status
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Getter for the time of a non-repeating task.
     *
     * @return "time" if a task is non-repeating and "repeat start time" if repeating
     */
    public int getTime() {
        return (isRepeated() ? start : time);
    }

    /**
     * Setter for the time of a non-repeating task.
     * If a task is repeating, then it should become non-repeating.
     *
     * @param time  notification time
     *
     * @throws IllegalArgumentException if time is negative
     */
    public void setTime(int time) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException("Time must equal to zero or be greater than it.");
        }
        if (isRepeated()) {
            this.repeated = false;
            this.start = 0;
            this.end = 0;
            this.interval = 0;
        }
        this.time = time;
    }

    /**
     * Getter for the start time of a repeating task start time.
     *
     * @return "time" if a task is non-repeating and "start" if is repeating
     */
    public int getStartTime() {
        return (isRepeated() ? start : time);
    }

    /**
     * Getter for the end time of a repeating task.
     *
     * @return "time" if a task is non-repeating and "end" if is repeating
     */
    public int getEndTime() {
        return (isRepeated() ? end : time);
    }

    /**
     * Getter for the interval time of a repeating task.
     *
     * @return "0" if a task is non-repeating and "interval" if is repeating
     */
    public int getRepeatInterval() {
        return (isRepeated() ? interval : 0);
    }

    /**
     * Setter for the time of a repeating task.
     * If a task is non-repeating, then it should become repeating.
     *
     * @param start  the notification start time
     * @param end  the notification end time
     * @param interval  Time interval after which it is necessary to repeat task notification.
     *
     * @throws IllegalArgumentException if timestamps are negative or "interval" isn't positive
     */
    public void setTime(int start, int end, int interval) throws IllegalArgumentException {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (start > end) {
            throw new IllegalArgumentException("The end of task must be greater than the start of task.");
        }
        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero.");
        }
        if (!isRepeated()) {
            this.repeated = true;
            this.time = 0;
        }
        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    /**
     * The method for checking the repeatability of a task.
     *
     * @return the information about repeatability of a task
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * The method which returns the time of the next task execution.
     *
     * @param current the specified time
     *
     * @return Return the time of the next task execution after the specified time,
     * if after the specified time a task wasn't executed, then the method returns "-1".
     *
     * @throws IllegalArgumentException if "specified time" is negative
     */
    public int nextTimeAfter(int current) throws IllegalArgumentException {
        if (current < 0) {
            throw new IllegalArgumentException("Specified time must equal to zero or be greater than it.");
        }
        if (isActive()) {
            if (!isRepeated()) {
                return (current >= time ? -1 : time);
            } else {
                if (current < end) {
                    for (int i = start; i <= end; i += interval) {
                        if (current < i) {
                            return i;
                        }
                    }
                }
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        if (isRepeated()) {
            return "Task \"" + title + "\": {start = " + start
                    + "; end = " + end + "; interval = " + interval
                    + "; active -> " + active + "; repeated -> " + repeated + "}";
        } else {
            return "Task \"" + title + "\": {time = " + time
                    + "; active -> " + active + "; repeated -> " + repeated + "}";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return time == task.time && start == task.start && end == task.end && interval == task.interval
                && active == task.active && repeated == task.repeated && Objects.equals(title, task.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, time, start, end, interval, active, repeated);
    }

    @Override
    public Task clone() throws CloneNotSupportedException {
        return (Task) super.clone();
    }
}
