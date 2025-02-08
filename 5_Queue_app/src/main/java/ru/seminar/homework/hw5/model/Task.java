package ru.seminar.homework.hw5.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    private String id;

    private Status status;

    private Map<Status, Long> statusStartTimesMap;

    private Map<Status, Long> statusTime;

    {
        statusTime = new HashMap<>();
        statusStartTimesMap = new HashMap<>();
        for (Status statusValue: Status.values()) {
            statusStartTimesMap.put(statusValue, 0L);
            statusTime.put(statusValue, 0L);
        }
    }

    public void setStatus(Status status) {
        if (this.status != null) {
            Long time = statusTime.get(this.status);
            statusTime.put(this.status, time + (System.currentTimeMillis() - statusStartTimesMap.get(this.status)));
            statusStartTimesMap.put(this.status, 0L);
        }
        this.status = status;
        statusStartTimesMap.put(status, System.currentTimeMillis());
    }


}
