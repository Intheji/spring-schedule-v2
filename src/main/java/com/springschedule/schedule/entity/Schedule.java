package com.springschedule.schedule.entity;

import com.springschedule.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "schedules")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false)
    private String authorName;

    public Schedule(String title, String content, String authorName) {
        this.title = title;
        this.content = content;
        this.authorName = authorName;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
