package subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.line.Line;
import subway.domain.section.Section;
import subway.domain.station.Station;
import subway.entity.LineEntity;
import subway.entity.SectionEntity;
import subway.entity.StationEntity;
import subway.exception.InvalidLineException;

@JdbcTest
class LineRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
        lineRepository = new LineRepository(lineDao, sectionDao);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        final Line line = new Line("2호선", "초록색");

        final Line result = lineRepository.save(line);

        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("2호선"),
                () -> assertThat(result.getColor()).isEqualTo("초록색")
        );
    }

    @Nested
    @DisplayName("노선 조회 시 ")
    class FindById {

        private LineEntity lineEntity;

        @BeforeEach
        void setUp() {
            lineEntity = lineDao.save(new LineEntity("2호선", "초록색"));
            final StationEntity upward = stationDao.save(new StationEntity("잠실역"));
            final StationEntity downward = stationDao.save(new StationEntity("잠실새내역"));
            sectionDao.save(new SectionEntity(lineEntity.getId(), upward.getId(), downward.getId(), 10));
        }

        @Test
        @DisplayName("ID로 조회할 때 존재하는 노선이라면 노선 정보를 반환한다.")
        void findById() {
            final Line result = lineRepository.findById(lineEntity.getId());

            final List<Section> sections = result.getSections();
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(lineEntity.getId()),
                    () -> assertThat(result.getName()).isEqualTo(lineEntity.getName()),
                    () -> assertThat(result.getColor()).isEqualTo(lineEntity.getColor()),
                    () -> assertThat(sections).hasSize(2),
                    () -> assertThat(sections.get(0).getUpward().getName()).isEqualTo("잠실역"),
                    () -> assertThat(sections.get(0).getDownward().getName()).isEqualTo("잠실새내역"),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(10),
                    () -> assertThat(sections.get(1).getUpward().getName()).isEqualTo("잠실새내역"),
                    () -> assertThat(sections.get(1).getDownward().getName()).isEqualTo(Station.TERMINAL.getName()),
                    () -> assertThat(sections.get(1).getDistance()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("ID로 조회할 때 존재하지 않는 노선이라면 예외를 던진다.")
        void findByInvalidId() {
            assertThatThrownBy(() -> lineRepository.findById(-2L))
                    .isInstanceOf(InvalidLineException.class)
                    .hasMessage("존재하지 않는 노선 ID 입니다.");
        }

        @Test
        @DisplayName("모든 노선 정보를 조회한다.")
        void findAll() {
            final List<Line> lines = lineRepository.findAll();

            final List<Section> sections = lines.get(0).getSections();
            assertAll(
                    () -> assertThat(lines).hasSize(1),
                    () -> assertThat(lines.get(0).getName()).isEqualTo("2호선"),
                    () -> assertThat(lines.get(0).getColor()).isEqualTo("초록색"),
                    () -> assertThat(sections).hasSize(2),
                    () -> assertThat(sections.get(0).getUpward().getName()).isEqualTo("잠실역"),
                    () -> assertThat(sections.get(0).getDownward().getName()).isEqualTo("잠실새내역"),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(10),
                    () -> assertThat(sections.get(1).getUpward().getName()).isEqualTo("잠실새내역"),
                    () -> assertThat(sections.get(1).getDownward().getName()).isEqualTo(Station.TERMINAL.getName()),
                    () -> assertThat(sections.get(1).getDistance()).isEqualTo(0)
            );
        }
    }

    @Nested
    @DisplayName("노선 정보 업데이트 시")
    class Update {

        @Test
        @DisplayName("섹션이 추가 됐을 때 노선 정보를 업데이트한다.")
        void updateWhenStationAdded() {
            final LineEntity lineEntity = lineDao.save(new LineEntity("2호선", "초록색"));
            final StationEntity upward = stationDao.save(new StationEntity("잠실역"));
            final StationEntity middle = stationDao.save(new StationEntity("종합운동장역"));
            final StationEntity downward = stationDao.save(new StationEntity("잠실새내역"));
            final SectionEntity sectionEntity = sectionDao.save(
                    new SectionEntity(
                            lineEntity.getId(),
                            upward.getId(),
                            downward.getId(),
                            10
                    )
            );
            final Line line = Line.of(lineEntity, List.of(sectionEntity));
            line.addSection(Station.from(upward), Station.from(middle), 3);

            lineRepository.update(line);

            final Line result = lineRepository.findById(lineEntity.getId());
            final List<Station> stations = result.getStations();
            assertAll(
                    () -> assertThat(stations).hasSize(3),
                    () -> assertThat(stations).extracting(Station::getName).containsExactly(
                            "잠실역",
                            "종합운동장역",
                            "잠실새내역"
                    )
            );
        }


        @Test
        @DisplayName("섹션이 삭제 됐을 때 노선 정보를 업데이트한다.")
        void updateWhenStationDeleted() {
            final LineEntity lineEntity = lineDao.save(new LineEntity("2호선", "초록색"));
            final StationEntity upward = stationDao.save(new StationEntity("잠실역"));
            final StationEntity downward = stationDao.save(new StationEntity("잠실새내역"));
            final SectionEntity sectionEntity = sectionDao.save(
                    new SectionEntity(
                            lineEntity.getId(),
                            upward.getId(),
                            downward.getId(),
                            10
                    )
            );
            final Line line = Line.of(lineEntity, List.of(sectionEntity));
            line.deleteStation(Station.from(upward));

            lineRepository.update(line);

            final Line result = lineRepository.findById(lineEntity.getId());
            final List<Station> stations = result.getStations();
            assertThat(stations).isEmpty();
        }
    }
}
