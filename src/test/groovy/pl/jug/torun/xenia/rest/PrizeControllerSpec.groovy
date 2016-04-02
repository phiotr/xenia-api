package pl.jug.torun.xenia.rest

import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.jug.torun.xenia.Application
import spock.lang.Stepwise

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@WebAppConfiguration
@IntegrationTest
class PrizeControllerSpec  {

    @Autowired
    protected WebApplicationContext webApplicationContext

    protected MockMvc request

    @Before
    void setup() {
        if (request == null) {
            request = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .dispatchOptions(true)
                    .build()
        }
    }

    @Test
    void shouldNotAllowToAddProductWithNameThatIsInUsed() {
        //given:
        String json = '''
{ "name": "Test", "producer": "Microsoft" }
'''

        //when:
        def response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        response.andExpect(status().isCreated())
                .andExpect(jsonPath('$.resourceUrl', is(equalTo('/prize/1'))))

        //when:
        response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.message', is(equalTo("Prize with name 'Test' already exists"))))

        //when:
        response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content('{ "name": "Test2", "producer": "Microsoft" }'))

        //then:
        response.andExpect(status().isCreated())
                .andExpect(jsonPath('$.resourceUrl', is(equalTo('/prize/2'))))
    }
}
