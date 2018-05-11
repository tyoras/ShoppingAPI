package io.tyoras.shopping.user;

import org.junit.Test;

import static io.tyoras.shopping.user.ProfileVisibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

public class ProfileVisibilityTest {

    @Test
    public void valueOfOrNull_should_return_null_with_invalid_code() {
        //given
        String invalidCode = "invalid";

        //when
        ProfileVisibility result = ProfileVisibility.valueOfOrNull(invalidCode);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void valueOfOrNull_should_work_with_valid_code() {
        //given
        String validCode = "PUBLIC";

        //when
        ProfileVisibility result = ProfileVisibility.valueOfOrNull(validCode);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(PUBLIC);
    }

}
