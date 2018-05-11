package io.tyoras.shopping.root.resource;

import io.tyoras.shopping.infra.rest.Link;
import io.tyoras.shopping.root.BuildInfo;
import io.tyoras.shopping.root.repository.BuildInfoRepository;
import io.tyoras.shopping.root.representation.RootRepresentation;
import io.tyoras.shopping.test.TestHelper;
import io.tyoras.shopping.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RootResourceTest {

    @Mock
    private BuildInfoRepository mockedBuildInfoRepo;

    @Spy
    @InjectMocks
    private RootResource testedResource;

    @Before
    public void beforeClass() {
        when(mockedBuildInfoRepo.getCurrentBuildInfos()).thenReturn(BuildInfo.DEFAULT);
    }

    @Test
    public void getRootLinks_should_contains_self_link() {
        //given
        String expectedURL = "http://test";
        UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
        doReturn(mockedUriInfo).when(testedResource).getUriInfo();

        //when
        List<Link> links = testedResource.getRootLinks();

        //then
        assertThat(links).isNotNull();
        assertThat(links).isNotEmpty();
        assertThat(links).contains(Link.self(expectedURL));
    }

    @Test
    public void root_should_work() {
        //given
        String expectedURL = "http://test";
        UriInfo mockedUriInfo = TestHelper.mockUriInfo(expectedURL);
        User connectedUser = TestHelper.generateRandomUser();
        doReturn(mockedUriInfo).when(testedResource).getUriInfo();

        //when
        Response response = testedResource.root(connectedUser);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        RootRepresentation representation = (RootRepresentation) response.getEntity();
        assertThat(representation).isNotNull();
        assertThat(representation.getConnectedUserId()).isEqualTo(connectedUser.getId());
        assertThat(representation.getLinks()).contains(Link.self(expectedURL));
    }

}
