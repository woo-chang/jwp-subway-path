package subway.domain.station;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.InvalidStationNameException;

class NameTest {

    @ParameterizedTest
    @DisplayName("역 이름이 2글자이하, 11글자 이상일 경우 예외를 던진다.")
    @ValueSource(strings = {"역", "열한글자가넘는역이름입니다역"})
    void validate_with_invalid_length(final String input) {
        assertThatThrownBy(() -> new Name(input))
                .isInstanceOf(InvalidStationNameException.class)
                .hasMessage("역 이름은 2글자에서 11글자까지 가능합니다.");
    }

    @Test
    @DisplayName("역 이름이 역으로 끝나지 않을 경우 예외를 던진다.")
    void validate_with_invalid_name_format() {
        assertThatThrownBy(() -> new Name("선릉"))
                .isInstanceOf(InvalidStationNameException.class)
                .hasMessage("역 이름은 '역'으로 끝나야 합니다.");
    }

    @ParameterizedTest
    @DisplayName("역 이름이 한글, 숫자로 구성되지 않을 경우 예외를 던진다.")
    @ValueSource(strings = {"NewYork역", "!!역"})
    void validate_with_invalid_name_element(final String input) {
        assertThatThrownBy(() -> new Name(input))
                .isInstanceOf(InvalidStationNameException.class)
                .hasMessage("역 이름은 한글, 숫자만 가능합니다.");
    }

    @ParameterizedTest
    @DisplayName("이름이 빈 칸이거나 null 일 때 예외를 던진다.")
    @NullAndEmptySource
    void validate_with_blank_name(final String input) {
        assertThatThrownBy(() -> new Name(input))
                .isInstanceOf(InvalidStationNameException.class)
                .hasMessage("역 이름은 공백일 수 없습니다.");

    }
}
