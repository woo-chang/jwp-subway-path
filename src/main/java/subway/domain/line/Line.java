package subway.domain.line;

import java.util.LinkedList;
import java.util.List;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.domain.station.Station;
import subway.exception.InvalidDistanceException;
import subway.exception.InvalidSectionException;

public class Line {

    private static final int ADDITIONAL_INDEX = -1;
    private final Long id;
    private final Name name;
    private final Color color;
    private final Sections sections;

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new LinkedList<>());
    }

    public Line(final Long id, final String name, final String color, final List<Section> sections) {
        this.id = id;
        this.name = new Name(name);
        this.color = new Color(color);
        this.sections = new Sections(sections);
    }

    public void addSection(final Station upward, final Station downward, final int distance) {
        if (sections.isEmpty()) {
            addInitialSection(upward, downward, distance);
            return;
        }

        final int upwardPosition = sections.findPosition(upward);
        final int downwardPosition = sections.findPosition(downward);
        validateForAddSection(upwardPosition, downwardPosition);

        if (shouldAddUpward(upwardPosition)) {
            if (isFirstSection(downwardPosition)) {
                sections.add(0, new Section(upward, downward, distance));
                return;
            }
            addUpwardSectionBetweenStations(upward, downward, distance, downwardPosition);
            return;
        }

        if (isLastSection(upwardPosition)) {
            addDownwardSectionInLast(upward, downward, distance);
            return;
        }
        addDownwardSectionBetweenStations(upward, downward, distance, upwardPosition);
    }

    private void addInitialSection(final Station upward, final Station downward, final int distance) {
        sections.add(new Section(upward, downward, distance));
        sections.add(new Section(downward, Station.TERMINAL, 0));
    }

    private void validateForAddSection(final int upwardPosition, final int downwardPosition) {
        if (upwardPosition != Sections.NOT_EXIST_INDEX
                && downwardPosition != Sections.NOT_EXIST_INDEX) {
            throw new InvalidSectionException("두 역이 이미 노선에 존재합니다.");
        }
        if (upwardPosition == Sections.NOT_EXIST_INDEX
                && downwardPosition == Sections.NOT_EXIST_INDEX) {
            throw new InvalidSectionException("연결할 역 정보가 없습니다.");
        }
    }

    private boolean shouldAddUpward(final int upwardPosition) {
        return upwardPosition == ADDITIONAL_INDEX;
    }

    private boolean isFirstSection(final int downwardPosition) {
        return downwardPosition == 0;
    }

    private void addUpwardSectionBetweenStations(final Station upward, final Station downward, final int distance,
                                                 final int downwardPosition) {
        final int targetPosition = downwardPosition - 1;
        final Section section = sections.findSectionByPosition(targetPosition);
        sections.deleteByPosition(targetPosition);
        validateDistance(section.getDistance(), distance);
        sections.add(targetPosition, new Section(upward, downward, distance));
        sections.add(targetPosition, new Section(section.getUpward(), upward, section.getDistance() - distance));
    }

    private boolean isLastSection(final int upwardPosition) {
        return sections.size() - 1 == upwardPosition;
    }

    private void addDownwardSectionInLast(final Station upward, final Station downward, final int distance) {
        sections.deleteByPosition(sections.size() - 1);
        sections.add(sections.size(), new Section(upward, downward, distance));
        sections.add(sections.size(), new Section(downward, Station.TERMINAL, 0));
    }

    private void addDownwardSectionBetweenStations(final Station upward, final Station downward, final int distance,
                                                   final int upwardPosition) {
        final Section section = sections.findSectionByPosition(upwardPosition);
        sections.deleteByPosition(upwardPosition);
        validateDistance(section.getDistance(), distance);
        sections.add(upwardPosition, new Section(downward, section.getDownward(), section.getDistance() - distance));
        sections.add(upwardPosition, new Section(upward, downward, distance));
    }

    private void validateDistance(final int oldDistance, final int inputDistance) {
        if (oldDistance <= inputDistance) {
            throw new InvalidDistanceException("추가될 역의 거리는 추가될 위치의 두 역사이의 거리보다 작아야합니다.");
        }
    }

    public List<Station> show() {
        return sections.getUpwards();
    }
}