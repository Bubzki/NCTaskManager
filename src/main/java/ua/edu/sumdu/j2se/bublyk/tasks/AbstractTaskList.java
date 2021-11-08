package ua.edu.sumdu.j2se.bublyk.tasks;

public abstract class AbstractTaskList {
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
     * @throws IllegalArgumentException if timestamps are negative or <code>from</code> is greater than <code>to</code>
     */
    public AbstractTaskList incoming(int from, int to) throws IllegalArgumentException {
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (from > to) {
            throw new IllegalArgumentException("Time \"to\" must be greater than \"from\".");
        }
        AbstractTaskList tempTaskList = getTaskList();
        for (int i = 0; i < size(); ++i) {
            if (getTask(i).nextTimeAfter(from) != -1 && getTask(i).getStartTime() <= to) {
                for (int j = getTask(i).getStartTime(); j <= getTask(i).getEndTime();
                        j += getTask(i).getRepeatInterval()) {
                    if (j > from && j <= to) {
                        tempTaskList.add(getTask(i));
                        break;
                    }
                }
            }
        }
        return tempTaskList;
    }

    /**
     * The method that creates an object of the required type.
     *
     * @return the specified object
     */
    private AbstractTaskList getTaskList() {
        if (this.getClass().equals(ArrayTaskList.class)) {
            return TaskListFactory.createTaskList(ListTypes.types.ARRAY);
        }
        else {
            return TaskListFactory.createTaskList(ListTypes.types.LINKED);
        }
    }
}
