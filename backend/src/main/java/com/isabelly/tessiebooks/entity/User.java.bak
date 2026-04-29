package com.isabelly.tessiebooks.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @JsonIgnore
    private String email;
    
    @JsonIgnore
    private String password;
    
    private String bio;
    private String profileImage;

    private Integer totalBooksRead = 0;
    private Integer totalPagesRead = 0;
    private LocalDate joinDate;
    private String location;
    private String favoriteGenre;


    @ManyToMany
    @JsonIgnore
    @JoinTable(
        name = "user_following",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private List<User> following = new ArrayList<>();


    @ManyToMany(mappedBy = "following")
    @JsonIgnore
    private List<User> followers = new ArrayList<>();


   @PrePersist
protected void onCreate() {
    if (joinDate == null) {
        joinDate = LocalDate.now();
    }
    if (totalBooksRead == null) totalBooksRead = 0;
    if (totalPagesRead == null) totalPagesRead = 0;
}

   
    public void follow(User user) {
        if (!following.contains(user)) {
            following.add(user);
        }
    }
// ✅ Correto - retorna List<User>
public List<User> getFollowers() {
    return followers;
}

public List<User> getFollowing() {
    return following;
}

// ✅ Se quiser a quantidade, crie métodos separados:

    public void unfollow(User user) {
        following.remove(user);
    }

    public boolean isFollowedBy(User user) {
        return followers.contains(user);
}

    public boolean isFollowing(User user) {
        return following.contains(user);
    }

    public int getFollowersCount() {
        return followers != null ? followers.size() : 0;
    }

 
    public int getFollowingCount() {
        return following != null ? following.size() : 0;
    }



// Getters e Setters
public Integer getTotalBooksRead() { return totalBooksRead; }
public void setTotalBooksRead(Integer totalBooksRead) { this.totalBooksRead = totalBooksRead; }

public Integer getTotalPagesRead() { return totalPagesRead; }
public void setTotalPagesRead(Integer totalPagesRead) { this.totalPagesRead = totalPagesRead; }
}