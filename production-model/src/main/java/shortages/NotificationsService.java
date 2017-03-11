package shortages;

public interface NotificationsService {
    void alertPlanner(Shortages shortages);

    void softNotifyPlanner(Shortages shortages);

    void markOnPlan(Shortages shortages);
}
