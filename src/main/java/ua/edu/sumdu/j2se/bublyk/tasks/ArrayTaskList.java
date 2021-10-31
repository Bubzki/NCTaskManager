package ua.edu.sumdu.j2se.bublyk.tasks;

public class ArrayTaskList {
    private final static int DEFAULT_CAPACITY = 10;
    private final static float RATIO = 1.5f;
    private int size;
    private Task[] tasks = new Task[DEFAULT_CAPACITY];

    /**
     * The method that add a task to the list and
     * increases array capacity to 1.5 times if size is greater than capacity.
     *
     * @param task a specified task that needs to add
     *
     * @throws NullPointerException if task is null pointer
     */
    public void add(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Cannot add null pointer.");
        }
        if (size == tasks.length) {
            Task[] tempTasks = new Task[Math.round(size * RATIO)];
            System.arraycopy(tasks, 0, tempTasks, 0, size);
            tasks = tempTasks;
        }
        tasks[size] = task;
        size++;
    }

    /**
     * The method that removes a task from the list.
     * If there were several such tasks in the list,
     * it will delete such task, which was added the first.
     *
     * @param task a specified task that needs to remove
     *
     * @return "true" if the task on the list, "false" if the task not on the list
     *
     * @throws NullPointerException if task is null pointer
     */
    public boolean remove(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Cannot remove null pointer.");
        }
        if (size != 0) {
            for (int i = 0; i < size; ++i) {
                if (task.equals(tasks[i])) {
                    if (i != size - 1) {
                        System.arraycopy(tasks, i + 1, tasks, i, size - i - 1);
                    }
                    tasks[size - 1] = null;
                    size--;
                    trimCapacity();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The method that returns the number of tasks in the list.
     *
     * @return the size of the list
     */
    public int size() {
        return size;
    }

    /**
     * The method that decreases array capacity by one third,
     * if the capacity is equal or more than 1.5 times the number of elements
     */
    private void trimCapacity() {
        if (size != 0 && ((float)tasks.length / (float)size) >= RATIO
                && tasks.length > DEFAULT_CAPACITY) {
            Task[] tempTasks;
            if ((tasks.length / RATIO) <= DEFAULT_CAPACITY) {
                tempTasks = new Task[DEFAULT_CAPACITY];
            } else {
                tempTasks = new Task[Math.round(tasks.length / RATIO)];
            }
            System.arraycopy(tasks, 0, tempTasks, 0, size);
            tasks = tempTasks;
        }
    }

    /**
     * Getter for an array capacity.
     *
     * @return an array capacity
     */
    public int capacity() {
        return tasks.length;
    }

    /**
     * The method that returns the task that is at the specified location in list,
     * the first task has an index of 0.
     *
     * @param index the specified task index
     *
     * @return a task with the specified index
     *
     * @throws IndexOutOfBoundsException if index is out of the list range.
     */
    public Task getTask(int index) throws IndexOutOfBoundsException {
        if (index >= size) {
            throw new IndexOutOfBoundsException("The index is out of range.");
        }
        return tasks[index];
    }

    /**
     * The method that finds a subset of tasks
     * that are scheduled to run at least once after time "from" and no later than "to".
     *
     * @param from the start time of the interval
     * @param to the end time of the interval
     *
     * @return the subset of tasks that fit the specified time period
     *
     * @throws IllegalArgumentException if timestamps are negative or "from" is greater than "to"
     */
    public ArrayTaskList incoming(int from, int to) throws IllegalArgumentException {
        if (from < 0 || to < 0) {
            throw new IllegalArgumentException("Timestamps must equal to zero or be greater than it.");
        }
        if (from > to) {
            throw new IllegalArgumentException("Time \"to\" must be greater than \"from\".");
        }
        ArrayTaskList tempTaskList = new ArrayTaskList();
        for (int i = 0; i < this.size; ++i) {
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
}
