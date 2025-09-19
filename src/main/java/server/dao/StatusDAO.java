package server.dao;

import server.models.Status;

public class StatusDAO extends BaseDAO<Status, Integer> {
    public StatusDAO() {
        super(Status.class);
    }
}
