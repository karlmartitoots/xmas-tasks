package ee.kmtster.xmastasks.tasks;

public interface TaskInstance {
    boolean isFinished();
    String progress();
    String display();
}
