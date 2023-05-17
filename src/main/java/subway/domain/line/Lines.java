package subway.domain.line;

import java.util.List;

public class Lines {

    private final List<Line> lines;

    public Lines(final List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }
}
