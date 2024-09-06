package org.book.bookshop.service;

import org.book.bookshop.model.Status;
import org.book.bookshop.repository.StatusRepository;

import java.util.Optional;

public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService() {
        this.statusRepository = new StatusRepository();
    }

    public Status findStatusByName(String name) {
        Optional<Status> status = statusRepository.findStatusByName(name);
        return status.stream().findFirst().orElse(null);
    }

    public Status saveStatus(String statusName) {
        return statusRepository.save(new Status(statusName));
    }

    public void deleteStatus(Status status) {
        statusRepository.delete(status);
    }
}
