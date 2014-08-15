package test.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import aleph.TestPersistentContext;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DefaultTestConfig.class)
public abstract class AbstractTest {

	@Autowired
	private WebApplicationContext wac;

	@Resource
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private TransactionTemplate template;

	private MockMvc mockMvc;

	private MockHttpSession session;

	@Before
	public void setUp() {
		template.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TestPersistentContext.get().clear();
			}
		});
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setForceEncoding(true);
		filter.setEncoding("UTF-8");
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilter(filter)
				.addFilter(springSecurityFilterChain)
				.alwaysDo(print())
				.build();
	}

	protected void saveAll() {
		template.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				TestPersistentContext.get().saveAll();
			}
		});
	}

	protected void inTransaction(TransactionCallback<Object> action) {
		template.execute(action);
	}

	public ResultActions perform(MockHttpServletRequestBuilderFactory factory) throws Exception {
		return perform(factory.create());
	}

	public ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
		if (session != null) {
			requestBuilder.session(session);
		}
		requestBuilder.contentType(MediaType.APPLICATION_JSON);
		return mockMvc.perform(requestBuilder);
	}

	protected void signIn(UserEntry user) throws Exception {
		if (session != null && !session.isInvalid()) {
			session.invalidate();
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		session = (MockHttpSession) mockMvc.perform(post("/api/login")
				.param("username", user.getUsername())
				.param("password", user.getPassword()))
				.andExpect(status().isOk())
				.andReturn().getRequest().getSession();
	}

	protected void signOut() {
		if (session != null) {
			session.invalidate();
			SecurityContextHolder.getContext().setAuthentication(null);
		}
	}
}
