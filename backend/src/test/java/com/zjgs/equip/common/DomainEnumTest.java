package com.zjgs.equip.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainEnumTest {
    @Test
    void classTypeMapsLegacyCodes() {
        assertThat(ClassType.fromCode(1)).isEqualTo(ClassType.ARM);
        assertThat(ClassType.fromCode(2)).isEqualTo(ClassType.IOT);
        assertThatThrownBy(() -> ClassType.fromCode(9)).isInstanceOf(BusinessException.class);
    }

    @Test
    void borrowStatusMapsLegacyCodes() {
        assertThat(BorrowStatus.fromCode(0)).isEqualTo(BorrowStatus.APPLYING);
        assertThat(BorrowStatus.fromCode(1)).isEqualTo(BorrowStatus.BORROWED);
        assertThat(BorrowStatus.fromCode(2)).isEqualTo(BorrowStatus.RETURNED);
        assertThat(BorrowStatus.fromCode(3)).isEqualTo(BorrowStatus.REJECTED);
        assertThat(BorrowStatus.fromCode(4)).isEqualTo(BorrowStatus.CANCELLED);
    }

    @Test
    void workStatusFallback() {
        assertThat(WorkStatus.fromCode(2)).isEqualTo(WorkStatus.ENDED);
        assertThat(WorkStatus.fromCode(99)).isEqualTo(WorkStatus.NOT_STARTED);
    }
}
