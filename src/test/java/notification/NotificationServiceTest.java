package notification;

import metrics.MetricsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.User;
import user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MetricsService metricsService;
    @Mock
    private EmailSender emailSender;
    @Mock
    private PushSender pushSender;
    @Mock
    private SmsSender smsSender;
    private NotificationService systemUnderTest;

    @BeforeEach
    void setup() {
        when(emailSender.channel()).thenReturn(NotificationChannel.EMAIL);
        when(pushSender.channel()).thenReturn(NotificationChannel.PUSH);
        when(smsSender.channel()).thenReturn(NotificationChannel.SMS);
        systemUnderTest =
            new NotificationService(userRepository, metricsService, Set.of(emailSender, smsSender, pushSender));
    }

    @Test
    void givenUserIsEmpty_whenSendMessage_thenDoNothing() {
        //given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when
        systemUnderTest.send(new Notification());

        //then
        verify(emailSender, never()).send(any());
        verify(pushSender, never()).send(any());
        verify(smsSender, never()).send(any());
    }

    @Test
    void givenUserIsInactive_whenSendMessage_thenDoNothing() {
        //given
        User inactiveUser = new User(new UUID(1L, 1L), false, Set.of());
        when(userRepository.findById(any())).thenReturn(Optional.of(inactiveUser));

        //when
        systemUnderTest.send(new Notification());

        //then
        verify(emailSender, never()).send(any());
        verify(pushSender, never()).send(any());
        verify(smsSender, never()).send(any());
    }

    @Test
    void givenMultipleActiveChannels_whenSendMessage_thenUseAllChannels() {
        //given
        UUID userId = new UUID(1L, 1L);
        User user = new User(
            userId, true, Set.of(NotificationChannel.EMAIL, NotificationChannel.PUSH)
        );
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Notification notification = new Notification(
            new UUID(2L, 1L),
            userId,
            "test"
        );
        //when
        systemUnderTest.send(notification);

        //then
        verify(emailSender, times(1)).send(notification);
        verify(pushSender, times(1)).send(notification);
        verify(smsSender, never()).send(any());
        verify(metricsService, times(1)).incrementSent(NotificationChannel.EMAIL);
        verify(metricsService, times(1)).incrementSent(NotificationChannel.PUSH);
        verify(metricsService, never()).incrementSent(NotificationChannel.SMS);
    }

    @Test
    void givenMultipleActiveChannelsAndOneSenderFails_whenSendMessage_thenOtherSenderProceedProcessing() {
        //given
        UUID userId = new UUID(1L, 1L);
        User user = new User(
            userId, true, Set.of(NotificationChannel.EMAIL, NotificationChannel.PUSH, NotificationChannel.SMS)
        );
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException()).when(emailSender).send(any());
        Notification notification = new Notification(
            new UUID(2L, 1L),
            userId,
            "test"
        );
        //when
        systemUnderTest.send(notification);

        //then
        verify(emailSender, times(1)).send(notification);
        verify(pushSender, times(1)).send(notification);
        verify(smsSender, times(1)).send(notification);
        verify(metricsService, times(1)).incrementFailed(NotificationChannel.EMAIL);
        verify(metricsService, times(1)).incrementSent(NotificationChannel.PUSH);
        verify(metricsService, times(1)).incrementSent(NotificationChannel.SMS);
    }
}