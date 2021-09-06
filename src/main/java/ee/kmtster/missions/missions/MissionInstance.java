package ee.kmtster.missions.missions;

public interface MissionInstance<T extends Mission> {
    T getMission();
    boolean isFinished();
    String progress();
    String display();
}
