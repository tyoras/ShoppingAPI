package io.tyoras.shopping.infra.util.helper;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.Month.NOVEMBER;
import static org.assertj.core.api.Assertions.assertThat;

public class DateHelperTest {

    private final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Test
    public void toDate_should_return_null_with_null_param() {
        //given
        LocalDateTime nullParam = null;

        //when
        Date result = DateHelper.toDate(nullParam);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void toDate_should_work() throws ParseException {
        //given
        LocalDateTime localDateTime = LocalDateTime.of(1986, NOVEMBER, 15, 10, 42);
        Date expectedDate = formatter.parse("15/11/1986 10:42");

        //when
        Date result = DateHelper.toDate(localDateTime);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedDate);
    }

    @Test
    public void toLocalDateTime_should_return_null_with_null_param() {
        //given
        Date nullParam = null;

        //when
        LocalDateTime result = DateHelper.toLocalDateTime(nullParam);

        //then
        assertThat(result).isNull();
    }

    @Test
    public void toLocalDateTime_should_work() throws ParseException {
        //given
        Date date = formatter.parse("15/11/1986 10:42");
        LocalDateTime expectedLocalDateTime = LocalDateTime.of(1986, NOVEMBER, 15, 10, 42);

        //when
        LocalDateTime result = DateHelper.toLocalDateTime(date);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedLocalDateTime);
    }
}
