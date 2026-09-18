package com.chapterconnect.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.chapterconnect.app.model.AccountStatus;
import com.chapterconnect.app.model.Announcement;
import com.chapterconnect.app.model.AnnouncementStatus;
import com.chapterconnect.app.model.EventStatus;
import com.chapterconnect.app.model.Role;
import com.chapterconnect.app.model.User;
import com.chapterconnect.app.repository.AnnouncementRepository;
import com.chapterconnect.app.repository.EventRepository;
import com.chapterconnect.app.repository.MemberProfileRepository;
import com.chapterconnect.app.repository.UserRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY,
        connection = EmbeddedDatabaseConnection.H2)
@Transactional
class SecurityIntegrationTests {

    // Synthetic credentials used only in the isolated, in-memory test database.
    private static final String TEST_PASSWORD = "test-only-password";
    private static final String BROTHER_EMAIL = "brother@example.test";
    private static final String OFFICER_EMAIL = "officer@example.test";
    private static final String ADMIN_EMAIL = "admin@example.test";
    private static final String MEMBER_EMAIL = "new-member@example.test";

    private static final String EVENT_REQUEST = """
            {
                "title": "Chapter meeting",
                "description": "Test chapter meeting",
                "eventType": "CHAPTER",
                "startDateTime": "2030-10-01T18:00:00",
                "endDateTime": "2030-10-01T19:00:00",
                "location": "Chapter room"
            }
            """;

    private static final String MEMBER_REQUEST = """
            {
                "email": "new-member@example.test",
                "password": "test-only-password",
                "firstName": "Test",
                "lastName": "Member",
                "major": "Computer Science",
                "graduationYear": 2030,
                "pledgeClass": "Fall 2026",
                "chapterPosition": "Member",
                "role": "BROTHER"
            }
            """;

    private static final String REVIEW_REQUEST = """
            {"approved": true, "reviewNote": "Approved for the chapter"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberProfileRepository memberProfileRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private User officer;
    private User admin;

    @BeforeEach
    void createUsers() {
        // HTTP Basic exercises the real filter chain and CustomUserDetailsService.
        String passwordHash = passwordEncoder.encode(TEST_PASSWORD);
        userRepository.save(new User(BROTHER_EMAIL, passwordHash, Role.BROTHER, AccountStatus.ACTIVE));
        officer = userRepository.save(new User(OFFICER_EMAIL, passwordHash, Role.OFFICER, AccountStatus.ACTIVE));
        admin = userRepository.save(new User(ADMIN_EMAIL, passwordHash, Role.ADMIN, AccountStatus.ACTIVE));
        flushAndClear();
    }

    @Test
    void brotherCannotCreateEvent() throws Exception {
        mockMvc.perform(post("/api/events")
                        .with(httpBasic(BROTHER_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EVENT_REQUEST))
                // The existing service denies this via IllegalArgumentException,
                // which GlobalExceptionHandler currently maps to HTTP 400.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Brothers are not allowed to create events."));

        flushAndClear();
        assertThat(eventRepository.count()).isZero();
    }

    @Test
    void officerCanCreateEvent() throws Exception {
        mockMvc.perform(post("/api/events")
                        .with(httpBasic(OFFICER_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EVENT_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Chapter meeting"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.createdByEmail").value(OFFICER_EMAIL))
                .andExpect(jsonPath("$.createdByRole").value("OFFICER"));

        flushAndClear();
        assertThat(eventRepository.findAll()).singleElement().satisfies(event -> {
            assertThat(event.getTitle()).isEqualTo("Chapter meeting");
            assertThat(event.getStatus()).isEqualTo(EventStatus.SCHEDULED);
            assertThat(event.getCreatedBy().getId()).isEqualTo(officer.getId());
        });
    }

    @Test
    void officerCannotCreateMember() throws Exception {
        long originalUserCount = userRepository.count();

        mockMvc.perform(post("/api/members")
                        .with(httpBasic(OFFICER_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MEMBER_REQUEST))
                .andExpect(status().isForbidden());

        flushAndClear();
        assertThat(userRepository.count()).isEqualTo(originalUserCount);
        assertThat(userRepository.findByEmail(MEMBER_EMAIL)).isEmpty();
        assertThat(memberProfileRepository.count()).isZero();
    }

    @Test
    void adminCanCreateMember() throws Exception {
        mockMvc.perform(post("/api/members")
                        .with(httpBasic(ADMIN_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MEMBER_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(MEMBER_EMAIL))
                .andExpect(jsonPath("$.role").value("BROTHER"))
                .andExpect(jsonPath("$.membershipStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        flushAndClear();
        User member = userRepository.findByEmail(MEMBER_EMAIL).orElseThrow();
        assertThat(member.getRole()).isEqualTo(Role.BROTHER);
        assertThat(member.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, member.getPasswordHash())).isTrue();
        assertThat(memberProfileRepository.findByUserId(member.getId())).hasValueSatisfying(profile -> {
            assertThat(profile.getFirstName()).isEqualTo("Test");
            assertThat(profile.getLastName()).isEqualTo("Member");
        });
    }

    @Test
    void officerCanCreateAndSubmitOwnAnnouncement() throws Exception {
        mockMvc.perform(post("/api/announcements")
                        .with(httpBasic(OFFICER_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "Chapter update", "message": "Meeting this week"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.createdByEmail").value(OFFICER_EMAIL));

        flushAndClear();
        var drafts = announcementRepository.findByCreatedById(officer.getId());
        assertThat(drafts).singleElement().satisfies(draft -> {
            assertThat(draft.getStatus()).isEqualTo(AnnouncementStatus.DRAFT);
            assertThat(draft.getTitle()).isEqualTo("Chapter update");
        });
        Long id = drafts.getFirst().getId();

        mockMvc.perform(put("/api/announcements/{id}/submit", id)
                        .with(httpBasic(OFFICER_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.createdByEmail").value(OFFICER_EMAIL));

        Announcement submitted = reloadAnnouncement(id);
        assertThat(submitted.getStatus()).isEqualTo(AnnouncementStatus.PENDING_APPROVAL);
        assertThat(submitted.getCreatedBy().getId()).isEqualTo(officer.getId());
        assertThat(submitted.getReviewedBy()).isNull();
    }

    @Test
    void officerCannotSubmitAnotherUsersAnnouncement() throws Exception {
        Long id = announcementRepository.saveAndFlush(new Announcement(
                "Another user's draft", "Private draft", admin, AnnouncementStatus.DRAFT)).getId();

        mockMvc.perform(put("/api/announcements/{id}/submit", id)
                        .with(httpBasic(OFFICER_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You may only submit your own announcement."));

        Announcement unchanged = reloadAnnouncement(id);
        assertThat(unchanged.getStatus()).isEqualTo(AnnouncementStatus.DRAFT);
        assertThat(unchanged.getCreatedBy().getId()).isEqualTo(admin.getId());
    }

    @Test
    void officerCannotReviewAnnouncement() throws Exception {
        Long id = createAnnouncement(AnnouncementStatus.PENDING_APPROVAL);

        mockMvc.perform(put("/api/announcements/{id}/review", id)
                        .with(httpBasic(OFFICER_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REVIEW_REQUEST))
                .andExpect(status().isForbidden());

        Announcement unchanged = reloadAnnouncement(id);
        assertThat(unchanged.getStatus()).isEqualTo(AnnouncementStatus.PENDING_APPROVAL);
        assertThat(unchanged.getReviewedBy()).isNull();
        assertThat(unchanged.getReviewedAt()).isNull();
        assertThat(unchanged.getReviewNote()).isNull();
    }

    @Test
    void adminCanReviewAndPublishAnnouncement() throws Exception {
        Long id = createAnnouncement(AnnouncementStatus.PENDING_APPROVAL);

        mockMvc.perform(put("/api/announcements/{id}/review", id)
                        .with(httpBasic(ADMIN_EMAIL, TEST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REVIEW_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reviewedByEmail").value(ADMIN_EMAIL))
                .andExpect(jsonPath("$.reviewNote").value("Approved for the chapter"))
                .andExpect(jsonPath("$.reviewedAt").isNotEmpty());

        Announcement approved = reloadAnnouncement(id);
        assertThat(approved.getStatus()).isEqualTo(AnnouncementStatus.APPROVED);
        assertThat(approved.getReviewedBy().getId()).isEqualTo(admin.getId());
        assertThat(approved.getReviewNote()).isEqualTo("Approved for the chapter");
        assertThat(approved.getReviewedAt()).isNotNull();
        assertThat(approved.getPublishedAt()).isNull();

        mockMvc.perform(put("/api/announcements/{id}/publish", id)
                        .with(httpBasic(ADMIN_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());

        Announcement published = reloadAnnouncement(id);
        assertThat(published.getStatus()).isEqualTo(AnnouncementStatus.PUBLISHED);
        assertThat(published.getPublishedAt()).isNotNull();
        assertThat(published.getReviewedBy().getId()).isEqualTo(admin.getId());
        assertThat(published.getCreatedBy().getId()).isEqualTo(officer.getId());
    }

    @Test
    void brotherCanViewOnlyPublishedAnnouncements() throws Exception {
        Long publishedId = createAnnouncement(AnnouncementStatus.PUBLISHED);
        createAnnouncement(AnnouncementStatus.DRAFT);
        createAnnouncement(AnnouncementStatus.PENDING_APPROVAL);
        createAnnouncement(AnnouncementStatus.APPROVED);
        createAnnouncement(AnnouncementStatus.REJECTED);
        flushAndClear();

        mockMvc.perform(get("/api/announcements/published")
                        .with(httpBasic(BROTHER_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(publishedId))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"))
                .andExpect(jsonPath("$[0].title").value("Test announcement"))
                .andExpect(jsonPath("$[0].message").value("Test message"));
    }

    private Long createAnnouncement(AnnouncementStatus status) {
        return announcementRepository.saveAndFlush(new Announcement(
                "Test announcement", "Test message", officer, status)).getId();
    }

    private Announcement reloadAnnouncement(Long id) {
        flushAndClear();
        return announcementRepository.findById(id).orElseThrow();
    }

    private void flushAndClear() {
        // Verify stored state rather than only inspecting Hibernate's cached entities.
        entityManager.flush();
        entityManager.clear();
    }
}
