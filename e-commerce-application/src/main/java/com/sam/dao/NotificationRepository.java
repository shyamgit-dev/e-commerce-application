package com.sam.dao;

import com.sam.constant.NotificationStatus;
import com.sam.entity.Notification;
import com.sam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

     Page<Notification> findAll(Pageable pageable);

     Page<Notification> findByUser(User user,Pageable pageable);

     Page<Notification> findByUserAndStatus(User user, NotificationStatus status,Pageable pageable);

     Optional<Notification> findByUserAndId(User user,Long id);

     @Query("SELECT n FROM Notification n WHERE n.user=:user and n.status=NotificationStatus.UNREAD")
     List<Notification> getAllUnread(User user);

}
