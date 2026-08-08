package com.sam.service;

import com.sam.constant.NotificationStatus;
import com.sam.constant.NotificationType;
import com.sam.dto.NotificationDAO;
import com.sam.dto.NotificationResponse;
import com.sam.entity.Order;
import com.sam.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(User user,
                                            NotificationType type,
                                            String title,
                                            String message,
                                            Order order
                                            );

    //ADMIN
    List<NotificationResponse> getAllNotification();

    Page<NotificationDAO> getAllNotificationOfUser(int page,int size,NotificationStatus status);

    void readNotification(Long id);

    void readAll();

    Long deleteNotification(Long id);
}
