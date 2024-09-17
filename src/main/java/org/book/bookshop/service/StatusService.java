package org.book.bookshop.service;

import org.book.bookshop.model.Status;
import org.book.bookshop.repository.StatusRepository;

import java.sql.SQLException;
import java.util.Optional;

public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService() {
        this.statusRepository = new StatusRepository();
    }

    public Status findStatusByName(String name) throws SQLException {
        Optional<Status> status = statusRepository.findStatusByName(name);
        return status.stream().findFirst().orElse(null);
    }

    public Status saveStatus(String statusName) throws SQLException {
        return statusRepository.save(new Status(statusName));
    }

    public void deleteStatus(Status status) throws SQLException {
        statusRepository.delete(status);
    }
}
