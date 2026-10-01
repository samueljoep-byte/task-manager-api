package com.example.taskmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import com.example.taskmanager.TaskStatus;
import java.time.LocalDateTime;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Task {
	
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;
    
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @PrePersist
	public void onCreate() {
	    createdAt = LocalDateTime.now();
	    updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
	    updatedAt = LocalDateTime.now();
	}

    public Task() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id=id;
	}
	public LocalDateTime getCreatedAt() {
	    return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
	    return updatedAt;
	}
	
	public User getUser() {
	    return user;
	}

	public void setUser(User user) {
	    this.user = user;
	}
}