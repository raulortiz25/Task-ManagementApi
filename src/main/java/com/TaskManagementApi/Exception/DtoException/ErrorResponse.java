package com.TaskManagementApi.Exception.DtoException;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timeStamp,
                            int Status,
                            String error,
                            String message,
                            Object details,
                            String path) {

    public ErrorResponse(LocalDateTime timeStamp, int status, String error, String message, String path){
      this(timeStamp, status, error, message, null, path);
    }
}
