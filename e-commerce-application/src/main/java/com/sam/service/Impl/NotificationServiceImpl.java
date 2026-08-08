package com.sam.service.Impl;

import com.sam.constant.NotificationStatus;
import com.sam.constant.NotificationType;
import com.sam.dao.NotificationRepository;
import com.sam.dto.NotificationDAO;
import com.sam.dto.NotificationResponse;
import com.sam.entity.Notification;
import com.sam.entity.Order;
import com.sam.entity.User;
import com.sam.exception.InvalidActionException;
import com.sam.service.NotificationService;
import com.sam.utility.SecurityIntegration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("notificationService")
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final ModelMapper modelMapper;

    private final SecurityIntegration securityIntegration;

    @Transactional
    @Override
    public NotificationResponse createNotification(User user, NotificationType type, String title, String message,
                                                   Order order
                                                   ) {
        Objects.requireNonNull(title,"Title must not be null");
        Objects.requireNonNull(message,"Message must not be null");
        Objects.requireNonNull(user,"User must not be null");
        Objects.requireNonNull(type,"Notification type is mandatory");

        if(order!=null)
        {
            log.info("order Id # {}",order.getId());
        }

        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setMessage(message);
        notification.setTitle(title);
        notification.setOrder(order);
        notification.setType(type);
        notification.setUser(user);
        Notification savedNotification = notificationRepository.save(notification);

        log.info("Notification is created with type {} for user {} having notification Id {} ",
                savedNotification.getType(),
                user.getUsername(),
                savedNotification.getId()
                );

        return modelMapper.map(savedNotification,NotificationResponse.class);
    }

    //ADMIN ENDPOINT
    @Override
    public List<NotificationResponse> getAllNotification() {

        List<Notification> notifications = notificationRepository.findAll();

        return notifications.stream()
                  .map(notification -> modelMapper.map(notification,NotificationResponse.class))
                  .toList();
    }

    @Override
    public Page<NotificationDAO> getAllNotificationOfUser(int page,int size,NotificationStatus status) {

        //Authenticated First
        User user = securityIntegration.getAuthenticatedUser();

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC,"createdAt"));

        Page<Notification> notifications =
                 notificationRepository.findByUser(user,pageRequest);

        if(status!=null)
        {
            Page<Notification> notificationPage =
                    notificationRepository.findByUserAndStatus(user,status,pageRequest);

            return notificationPage.map(notification -> modelMapper.map(notification,NotificationDAO.class));

        }
        //Fetched All Notification of user
        return notifications.map(notification -> modelMapper.map(notification,NotificationDAO.class));
    }

    @Transactional
    @Override
    public void readNotification(Long id) {
        User user = securityIntegration.getAuthenticatedUser();

        Notification notification =
                notificationRepository.findByUserAndId(user,id)
                        .orElseThrow(()->new InvalidActionException("Notification Id not Matched"));

        if(notification.getStatus()==NotificationStatus.UNREAD)
        {
            notification.setReadAt(LocalDateTime.now());
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void readAll() {

        //Authenticated User First
        User user = securityIntegration.getAuthenticatedUser();

        //Fetched All Unread Notification for user
        List<Notification> notifications = notificationRepository.getAllUnread(user);

        //Traverse each notification and change status to read
        for(Notification notification:notifications)
        {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            //notificationRepository.save(notification);
        }
    }

    @Transactional
    @Override
    public Long deleteNotification(Long id) {
        User user = securityIntegration.getAuthenticatedUser();

        Notification notification =
                notificationRepository.findByUserAndId(user,id)
                        .orElseThrow(()-> new InvalidActionException("Notification Not Found"));
        notificationRepository.delete(notification);
        return id;
    }


}
