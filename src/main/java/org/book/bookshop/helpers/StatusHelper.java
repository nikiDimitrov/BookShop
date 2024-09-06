package org.book.bookshop.helpers;

import org.book.bookshop.model.Status;
import org.book.bookshop.service.StatusService;

public  class StatusHelper {
    private static final StatusService statusService = new StatusService();

    public static Status getStatusByName(String statusName) {
        Status status = statusService.findStatusByName(statusName);

        if(status == null) {
            status = statusService.saveStatus(statusName);
        }

        return status;
    }
}
