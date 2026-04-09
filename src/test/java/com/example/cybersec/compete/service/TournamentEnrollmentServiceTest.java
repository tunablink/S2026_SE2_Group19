package com.example.cybersec.compete.service;

import com.example.cybersec.compete.domain.CompeteBracket;
import com.example.cybersec.compete.domain.UserCompeteProfile;
import com.example.cybersec.compete.repository.TournamentEnrollmentRepository;
import com.example.cybersec.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentEnrollmentServiceTest {

    @Mock
    private TournamentEnrollmentRepository enrollmentRepository;

    @InjectMocks
    private TournamentEnrollmentService enrollmentService;

    @Test
    void resolveBracket_firstEnrollment_isRookie() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        UserCompeteProfile profile = new UserCompeteProfile(user);
        profile.setBracket(CompeteBracket.ADVANCED);
        when(enrollmentRepository.countByUser(user)).thenReturn(0L);

        assertThat(enrollmentService.resolveBracketForUser(user, profile)).isEqualTo(CompeteBracket.ROOKIE);
    }

    @Test
    void resolveBracket_afterFirst_followsProfile() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        UserCompeteProfile profile = new UserCompeteProfile(user);
        profile.setBracket(CompeteBracket.ADVANCED);
        when(enrollmentRepository.countByUser(user)).thenReturn(3L);

        assertThat(enrollmentService.resolveBracketForUser(user, profile)).isEqualTo(CompeteBracket.ADVANCED);
    }
}
