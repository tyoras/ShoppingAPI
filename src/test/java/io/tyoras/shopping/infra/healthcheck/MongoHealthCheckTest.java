package io.tyoras.shopping.infra.healthcheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import com.codahale.metrics.health.HealthCheck.Result;
import com.mongodb.MongoException;

import io.tyoras.shopping.infra.healthcheck.MongoHealthCheck;
import io.tyoras.shopping.test.fongo.FongoBackedTest;

public class MongoHealthCheckTest extends FongoBackedTest {
	
	@Spy
	@InjectMocks
	MongoHealthCheck testedHealthCheck;
	
	@Test
	public void name_should_be_mongo() {
		//when
		String result = testedHealthCheck.getName();
		
		//then
		assertThat(result).isEqualTo("mongo");
	}
	
	@Test
	public void check_should_return_healthy_if_it_can_access_mongo() throws Exception {
		//when
		Result result = testedHealthCheck.check();
		
		//then
		assertThat(result.isHealthy()).isTrue();
	}
	
	@Test
	public void check_should_return_healthy_if_it_cannot_access_mongo() throws Exception {
		//given
		doThrow(new MongoException("unreachable")).when(testedHealthCheck).accessDB();
		
		//when
		Result result = testedHealthCheck.check();
		
		//then
		assertThat(result.isHealthy()).isFalse();
	}

}
