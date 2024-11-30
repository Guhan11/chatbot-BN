package ApiFreeze;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FreezeConfig implements WebMvcConfigurer {

	 private final FreezeInterceptor freezeInterceptor;

	    public FreezeConfig(FreezeInterceptor freezeInterceptor) {
	        this.freezeInterceptor = freezeInterceptor;
	    }
	    
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(freezeInterceptor).addPathPatterns("/api/costInvoice/**"); // Apply it only to API endpoints
	}

}
