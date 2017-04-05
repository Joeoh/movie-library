package tech.joes.Controllers;


import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tech.joes.Application;
import tech.joes.Models.Movie;
import tech.joes.Repositories.MovieRepository;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class MovieControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Mock
    MovieRepository mockMovieRepository;

    @InjectMocks
    private MovieController movieController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(movieController)
                .build();

    }

    /*
    * Generate some random Movie objects
    * */
    private ArrayList<Movie> getDummyData(int numItems){
        Faker faker = new Faker();
        ArrayList<Movie> dummyData = new ArrayList<>();
        for(int i = 0; i < numItems; i++) {
            dummyData.add(new Movie(faker.lorem().word(), faker.number().numberBetween(1970,2017), faker.number().numberBetween(1,9999),faker.lorem().paragraph()));
        }

        return dummyData;
    }


    /*
    *   Tests that calling /movies/ returns all available movies in the repository
    * */
    @Test
    public void test_movies_endpoint_returns_all_available() throws Exception {

        int numDummyData = 5;

        ArrayList<Movie> dummyData = getDummyData(numDummyData);
        when(mockMovieRepository.findAll()).thenReturn(dummyData);


        mockMvc.perform(get("/movies/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(numDummyData)));

        //Clear the movie repo for next test
        mockMovieRepository.deleteAll();
    }

    /*
    *   Tests that calling /movies/{id} returns the correct item from the repository
    * */
    @Test
    public void test_single_movie_access_returns_correct_movie() throws Exception {

        int numDummyData = 5;
        int indexToTest = 2;
        ArrayList<Movie> dummyData = getDummyData(numDummyData);

        Movie expectedResult = dummyData.get(indexToTest);


        when(mockMovieRepository.findOne(indexToTest)).thenReturn(expectedResult);


        mockMvc.perform(get("/movies/"+indexToTest))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id", is(expectedResult.getId())))
                .andExpect(jsonPath("$.title", is(expectedResult.getTitle())))
                .andExpect(jsonPath("$.blurb", is(expectedResult.getBlurb())))
                .andExpect(jsonPath("$.releaseYear", is(expectedResult.getReleaseYear())))
                .andExpect(jsonPath("$.runtime", is(expectedResult.getRuntime())));

        //Clear the movie repo for next test
        mockMovieRepository.deleteAll();
    }

}
