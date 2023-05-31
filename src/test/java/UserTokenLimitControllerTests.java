import com.api.focusmate.FocusmateApplication;
import com.api.focusmate.model.Limit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = FocusmateApplication.class, initializers = TestInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserTokenLimitControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldAllowToPostLimit() throws Exception {
        Limit limit = new Limit();
        limit.setUrl("https://example.com");
        limit.setLimitTime(3600L);

        mockMvc.perform(post("/user/token_example/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(limit)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToPostLimit() throws Exception {
        Limit limit = new Limit();
        limit.setUrl("https://example.com");
        limit.setLimitTime(3600L);

        mockMvc.perform(post("/user/token_example1/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(limit)))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql", "classpath:sql/add_limit.sql"})
    public void shouldAllowToPutLimit() throws Exception {
        Limit limit = new Limit();
        limit.setUrl("https://example.com");
        limit.setLimitTime(6000L);

        mockMvc.perform(put("/user/token_example/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(limit)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToPutLimit() throws Exception {
        Limit limit = new Limit();
        limit.setUrl("https://example.com");
        limit.setLimitTime(6000L);

        mockMvc.perform(put("/user/token_example1/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(limit)))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldAllowToPatchLimit() throws Exception {
        Limit limit = new Limit();
        limit.setUrl("https://example.com");
        limit.setLimitTime(6000L);

        mockMvc.perform(patch("/user/token_example/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(limit)))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldAllowToDeleteLimit() throws Exception {
        Limit limit = new Limit();
        limit.setUrl("https://example.com");
        limit.setLimitTime(6000L);

        mockMvc.perform(delete("/user/token_example/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(limit)))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldAllowToGetLimit() throws Exception {
        mockMvc.perform(get("/user/token_example/limit")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToGetLimit() throws Exception {
        mockMvc.perform(get("/user/token_example1/limit")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(404));
    }
}