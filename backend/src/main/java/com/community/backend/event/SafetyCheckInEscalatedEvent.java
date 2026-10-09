package com.community.backend.event;
import java.util.List;
import com.community.backend.entity.SafetyCheckIn;
import com.community.backend.entity.TrustedContact;
public record SafetyCheckInEscalatedEvent(SafetyCheckIn checkIn,List<TrustedContact> contacts) {}
