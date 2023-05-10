package subway.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionCreateRequest {

    @NotNull(message = "상행 역 ID는 존재해야 합니다.")
    private Long upwardStationId;

    @NotNull(message = "하행 역 ID는 존재해야 합니다.")
    private Long downwardStationId;

    @NotNull(message = "역 간의 거리는 존재해야 합니다.")
    @Positive(message = "역 간의 거리는 0보다 커야합니다.")
    private Integer distance;

    public SectionCreateRequest() {
    }

    public SectionCreateRequest(final Long upwardStationId, final Long downwardStationId, final Integer distance) {
        this.upwardStationId = upwardStationId;
        this.downwardStationId = downwardStationId;
        this.distance = distance;
    }

    public Long getUpwardStationId() {
        return upwardStationId;
    }

    public Long getDownwardStationId() {
        return downwardStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}