package com.chapterconnect.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "member_profiles")
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String major;

    private Integer graduationYear;

    private String pledgeClass;

    private String chapterPosition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus membershipStatus;

    public MemberProfile() {
    }

    public MemberProfile(
            User user,
            String firstName,
            String lastName,
            String major,
            Integer graduationYear,
            String pledgeClass,
            String chapterPosition,
            MembershipStatus membershipStatus) {

        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.graduationYear = graduationYear;
        this.pledgeClass = pledgeClass;
        this.chapterPosition = chapterPosition;
        this.membershipStatus = membershipStatus;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getPledgeClass() {
        return pledgeClass;
    }

    public void setPledgeClass(String pledgeClass) {
        this.pledgeClass = pledgeClass;
    }

    public String getChapterPosition() {
        return chapterPosition;
    }

    public void setChapterPosition(String chapterPosition) {
        this.chapterPosition = chapterPosition;
    }

    public MembershipStatus getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        this.membershipStatus = membershipStatus;
    }
}