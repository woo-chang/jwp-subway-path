package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.entity.LineEntity;
import subway.entity.SectionEntity;
import subway.entity.StationEntity;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("해당 노선의 모든 구간 정보를 조회한다.")
    void findAllByLineId() {
        final LineEntity lineEntity = lineDao.save(new LineEntity("2호선", "초록색"));
        final StationEntity upward = stationDao.save(new StationEntity("잠실역"));
        final StationEntity downward = stationDao.save(new StationEntity("잠실새내역"));
        final SectionEntity entity = new SectionEntity(lineEntity.getId(), upward.getId(), downward.getId(), 10);
        final SectionEntity savedEntity = sectionDao.save(entity);

        final List<SectionEntity> sections = sectionDao.findAllByLineId(savedEntity.getLineId());

        assertThat(sections).containsExactly(savedEntity);
    }
}