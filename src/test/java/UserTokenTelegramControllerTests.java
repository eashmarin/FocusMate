import com.api.focusmate.FocusmateApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = FocusmateApplication.class, initializers = TestInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserTokenTelegramControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToPostTelegram() throws Exception {
        mockMvc.perform(post("/user/token_example/telegram")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql", "classpath:sql/add_limit.sql"})
    public void shouldAllowToPutTelegram() throws Exception {
        mockMvc.perform(put("/user/token_example/telegram/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql", "classpath:sql/add_limit.sql"})
    public void shouldNotAllowToPutTelegram() throws Exception {
        mockMvc.perform(put("/user/token_example1/telegram/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToPatchTelegram() throws Exception {
        mockMvc.perform(patch("/user/token_example/telegram")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToDeleteTelegram() throws Exception {
        mockMvc.perform(delete("/user/token_example/telegram")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldAllowToGetTelegram() throws Exception {
        mockMvc.perform(get("/user/token_example/telegram")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToGetTelegram() throws Exception {
        mockMvc.perform(get("/user/token_example1/telegram")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }
}