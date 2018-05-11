package io.tyoras.shopping.root;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildInfoTest {

    @Test(expected = IllegalArgumentException.class)
    public void buildInfo_should_fail_with_null_version() {
        //given
        String nullVersion = null;

        //when
        try {
            new BuildInfo(nullVersion, LocalDateTime.now());
        } catch (IllegalArgumentException iae) {
            //then
            assertThat(iae.getMessage()).isEqualTo("The version field is mandatory");
            throw iae;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildInfo_should_fail_with_blank_version() {
        //given
        String blankVersion = "  ";

        //when
        try {
            new BuildInfo(blankVersion, LocalDateTime.now());
        } catch (IllegalArgumentException iae) {
            //then
            assertThat(iae.getMessage()).isEqualTo("The version field is mandatory");
            throw iae;
        }
    }

    @Test
    public void buildInfo_should_work_without_build_date() {
        //given
        LocalDateTime nullDate = null;

        //when
        BuildInfo info = new BuildInfo("version", nullDate);

        //then
        assertThat(info).isNotNull();
        assertThat(info.getBuildDate()).isNull();
    }
}
