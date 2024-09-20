package org.book.bookshop.service;

import org.book.bookshop.helpers.Result;
import org.book.bookshop.model.Status;
import org.book.bookshop.repository.StatusRepository;

import java.sql.SQLException;
import java.util.Optional;

public class StatusService {

    private final StatusRepository statusRepository;

    public StatusService() {
        this.statusRepository = new StatusRepository();
    }

    public Result<Status> findStatusByName(String name) {
        try {
            Optional<Status> status = statusRepository.findStatusByName(name);
            return status.map(Result::success)
                    .orElse(Result.failure("Status not found for name: " + name));
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while fetching status by name. %s!", e.getMessage()));
        }
    }

    public Result<Status> saveStatus(String statusName) {
        try {
            Status newStatus = new Status(statusName);
            return Result.success(statusRepository.save(newStatus));
        } catch (SQLException e) {
            return Result.failure(String.format("Database error while saving status. %s!", e.getMessage()));
        }
    }
}
