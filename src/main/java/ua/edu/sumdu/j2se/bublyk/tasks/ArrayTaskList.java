package ua.edu.sumdu.j2se.bublyk.tasks;

public class ArrayTaskList {
    private final static int DEFAULT_CAPACITY = 10;
    private final static float RATIO = 1.5f;
    private Task[] tasks = new Task[DEFAULT_CAPACITY];

    /**
     * The method to add a task to the list.
     *
     * @param task a specified task that needs to add
     */
    public void add(Task task) {
        final int size = size();
        if (size == tasks.length) {
            // Increasing array capacity to 1.5 time
            Task[] tempTasks = new Task[Math.round(size * RATIO)];
            System.arraycopy(tasks, 0, tempTasks, 0, size);
            tasks = tempTasks;
        }
        tasks[size] = task;
    }

    /**
     * The method that removes a task from the list.
     * If there were several such tasks in the list,
     * it will delete such task, which was added the first.
     *
     * @param task a specified task that needs to remove
     * @return "true" if the task on the list, "false" if the task not on the list
     */
    public boolean remove(Task task) {
        int size = size();
        if (size != 0) {
            for (int i = 0; i < size; ++i) {
                if (task.hashCode() == tasks[i].hashCode()) {
                    if (i != size - 1) {
                        System.arraycopy(tasks, i + 1, tasks, i, size - i - 1);
                    }
                    tasks[size - 1] = null;
                    //Refresh "size" variable
                    size = size();
                    //Decreasing array capacity to 1.5 time if the capacity is exactly or more than 1.5 times the number of elements
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
        for (int i = 0; i < tasks.length; ++i) {
            if (tasks[i] == null) {
                return i;
            }
        }
        return tasks.length;
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
     * @return a task with the specified index
     */
    public Task getTask(int index) {
        return tasks[index];
    }

    /**
     * The method that finds a subset of tasks
     * that are scheduled to run at least once after time "from" and no later than "to".
     *
     * @param from the start time of the interval
     * @param to the end time of the interval
     * @return the subset of tasks that fit the specified time period
     */
    public ArrayTaskList incoming(int from, int to) {
        ArrayTaskList tempTaskList = new ArrayTaskList();
        for (int i = 0; i < this.size(); ++i) {
            if (this.tasks[i].nextTimeAfter(from) != -1 && this.tasks[i].getStartTime() <= to) {
                for (int j = this.tasks[i].getStartTime(); j <= this.tasks[i].getEndTime();
                        j += this.tasks[i].getRepeatInterval()) {
                    if (j > from && j <= to) {
                        tempTaskList.add(this.tasks[i]);
                        break;
                    }
                }
            }
        }
        return tempTaskList;
    }
}
