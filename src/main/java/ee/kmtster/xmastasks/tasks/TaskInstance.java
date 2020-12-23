package ee.kmtster.xmastasks.tasks;

public interface TaskInstance<T extends XmasTask> {
    T getTask();
    boolean isFinished();
    String progress();
    String display();
}
