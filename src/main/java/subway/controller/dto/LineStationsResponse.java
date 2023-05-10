package subway.controller.dto;

import java.util.List;

public class LineStationsResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineStationsResponse(final Long id, final String name, final String color,
                                 final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}