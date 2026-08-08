package com.sam.controller;

import com.sam.constant.NotificationStatus;
import com.sam.dto.NotificationDAO;
import com.sam.dto.NotificationResponse;
import com.sam.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/notifications/getAll")
    public ResponseEntity<List<NotificationResponse>> getAll()
    {
       return new ResponseEntity<>(notificationService.getAllNotification(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationDAO>> getAllNOtificationOfUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) NotificationStatus status
            )
    {
        return new ResponseEntity<>
                (notificationService.getAllNotificationOfUser(page,size,status),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<String> readNotification(@PathVariable Long id)
    {
        notificationService.readNotification(id);
        String output = "Status has been update";
        return new ResponseEntity<>(output,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/notifications/readAll")
    public  ResponseEntity<String> readAll()
    {
        notificationService.readAll();
        String output = "Status has been updated";
        return new ResponseEntity<>(output,HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id)
    {
        Long nfId = notificationService.deleteNotification(id);
        String result = "Notification has been deleted "+nfId;
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
