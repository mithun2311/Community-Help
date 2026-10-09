package com.community.backend.event;
import java.util.List;
import com.community.backend.entity.SOSIncident;
import com.community.backend.entity.TrustedContact;
public record SosTriggeredEvent(SOSIncident incident,List<TrustedContact> contacts) {}
