package ua.edu.sumdu.j2se.bublyk.tasks;

import java.util.stream.Stream;

public abstract class AbstractTaskList implements Iterable<Task>, Cloneable {
    public abstract void add(Task task);

    public abstract boolean remove(Task task);

    public abstract int size();

    public abstract Task getTask(int index);

    /**
     * The method that finds a subset of tasks
     * that are scheduled to run at least once after time <code>from</code> and no later than <code>to</code>.
     *
     * @param from the start time of the interval
     * @param to the end time of the interval
     *
     * @return the subset of tasks that fit the specified time period
     *
     * @throws IllegalArgumentException if...
     * <ul>
     * <li>timestamps are negative</li>
     * <li><code>from</code> is greater than <code>to</code></li>
     * </ul>
     */
    public final AbstractTaskList incoming(int from, int to) throws IllegalArgumentException {
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (from > to) {
            throw new IllegalArgumentException("Time \"to\" must be greater than \"from\".");
        }
        AbstractTaskList tempTaskList = getTaskList();
        getStream().filter((e) -> e.nextTimeAfter(from) != -1 && e.nextTimeAfter(from) <= to).forEach(tempTaskList::add);
        return tempTaskList;
    }

    /**
     * The method that creates an object of the required type.
     *
     * @return the specified object
     */
    protected abstract AbstractTaskList getTaskList();

    @Override
    public AbstractTaskList clone() throws CloneNotSupportedException {
        return (AbstractTaskList) super.clone();
    }

    /**
     * The method that creates a stream of tasks from some list.
     *
     * @return the stream of tasks
     */
    public abstract Stream<Task> getStream();
}
