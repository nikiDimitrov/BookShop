package org.book.bookshop.helpers;

import org.book.bookshop.model.Status;
import org.book.bookshop.service.StatusService;


public  class StatusHelper {
    private static final StatusService statusService = new StatusService();

    public static Result<Status> getStatusByName(String statusName) {
        Result<Status> result = statusService.findStatusByName(statusName);

        if(result.isSuccess()) {
            return result;
        }
        else {
            result = statusService.saveStatus(statusName);
            return result;
        }
    }
}
