package ua.edu.sumdu.j2se.bublyk.tasks;

public class LinkedTaskList extends AbstractTaskList {
    private int size;
    private Node first;
    private Node last;

    /**
     * The class for creating nodes that are components of a linked list.
     * A node has an object stored in a list and links to the next and previous node.
     */
    private static class Node {
        Task item;
        Node next;
        Node previous;

        Node(Task item, Node previous, Node next) {
            this.item = item;
            this.previous = previous;
            this.next = next;
        }
    }

    /**
     * The method searches for the node with the specified index.
     *
     * @param index the specified task index
     *
     * @return a node with the specified task index
     */
    private Node getNode(int index) {
        Node temp;
        if (index + 1 <= (Math.round((float)size / 2.))) {
            temp = first;
            for (int i = 0; i < index; ++i) {
                temp = temp.next;
            }
        } else {
            temp = last;
            for (int i = size - 1; i > index; --i) {
                temp = temp.previous;
            }
        }
        return temp;
    }

    /**
     * The method that creates a non-null node with the specified task.
     *
     * @param item task that need add to node
     */
    private void createNode(Task item) {
        Node last = this.last;
        Node newNode = new Node(item, last, null);
        this.last = newNode;
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        size++;
    }

    /**
     * The method that deletes a non-null node with the specified task.
     *
     * @param node with the specified task that should be deleted
     */
    private void deleteNode(Node node) {
        if (size > 1) {
            if (node.previous == null) {
                first = node.next;
                node.next.previous = null;
                node.next = null;
            } else if (node.next == null) {
                last = node.previous;
                node.previous.next = null;
                node.previous = null;
            } else {
                node.previous.next = node.next;
                node.next.previous = node.previous;
                node.next = null;
                node.previous = null;
            }
        }
        node.item = null;
        size--;
    }

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
        createNode(task);
    }

    /**
     * The method that removes a task from the list.
     * If there were several such tasks in the list,
     * it will delete such task, which was added the first.
     *
     * @param task a specified task that needs to remove
     * @return "true" if the task on the list, "false" if the task not on the list
     *
     * @throws NullPointerException if task is null pointer
     */
    public boolean remove(Task task) throws NullPointerException {
        if (task == null) {
            throw new NullPointerException("Cannot remove null pointer.");
        }
        if (size != 0) {
            for (Node temp = first; temp != null; temp = temp.next) {
                if (task.equals(temp.item)) {
                    deleteNode(temp);
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
        return getNode(index).item;
    }
}
