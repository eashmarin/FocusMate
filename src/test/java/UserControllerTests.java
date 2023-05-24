import com.api.focusmate.FocusmateApplication;
import com.api.focusmate.rerository.UserRepository;
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
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void shouldAllowToPostUser() throws Exception {
        userRepository.deleteAll();
        mockMvc.perform(post("/user")
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldAllowToPutUser() throws Exception {
        mockMvc.perform(put("/user")
        ).andExpect(status().is(404));
    }

    @Test
    public void shouldAllowToPatchUser() throws Exception {
        mockMvc.perform(patch("/user")
        ).andExpect(status().is(404));
    }

    @Test
    @Sql(scripts = {"classpath:sql/add_user.sql"})
    public void shouldNotAllowToDeleteUser() throws Exception {
        mockMvc.perform(delete("/user/token_example1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(404));
    }
}