package yoan.shopping.test;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/test")
@Api(value = "/test", description = "Test operations")
@Produces({ "application/json", "application/xml" })
public class TestResource {
	
	@GET
	@Path("/{testId}")
	@ApiOperation(value = "Find by ID", notes = "Returns a test object ", response = TestObject.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Test object not found") })
	public Response getTestyId(
			@ApiParam(value = "ID that needs to be fetched", allowableValues = "range[1,5]", required = true) @PathParam("testId") String testId)
			throws NotFoundException {
		TestObject testObj = new TestObject();
		testObj.setId(42);
		testObj.setName("name1");
		testObj.setStatus("st");
		testObj.setList(Lists.newArrayList("elem1", "elem2"));
		return Response.ok().entity(testObj).build();
	}

}
