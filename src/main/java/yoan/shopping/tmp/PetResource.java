package yoan.shopping.tmp;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/pet")
@Api(value = "/pet", description = "Operations about pets")
@Produces({ "application/json", "application/xml" })
public class PetResource {
	
	private Logger LOGGER = LoggerFactory.getLogger(PetResource.class);

	@GET
	@Path("/{petId}")
	@ApiOperation(value = "Find pet by ID", notes = "Returns a pet when ID < 10.  ID > 10 or nonintegers will simulate API error conditions", response = Pet.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Pet not found") })
	public Response getPetById(
			@ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true) @PathParam("petId") String petId)
			throws NotFoundException {
		Pet pet = new Pet();
		pet.setId(42);
		pet.setName("name1");
		pet.setStatus("st");
		pet.setPhotoUrls(Lists.newArrayList("elem1", "elem2"));
		LOGGER.error("ah ah");
		return Response.ok().entity(pet).build();
	}

}
