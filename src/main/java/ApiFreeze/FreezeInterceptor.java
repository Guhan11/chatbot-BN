package ApiFreeze;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FreezeInterceptor implements HandlerInterceptor {

	private final FreezeService freezeService;

	public FreezeInterceptor(FreezeService freezeService) {
		this.freezeService = freezeService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (freezeService.getFreezeState()) {
			// Freeze is enabled; block certain operations (fetch/store)
			if (request.getRequestURI().contains("/api/costInvoice")) {

//				response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
//				response.getWriter().write("API is frozen, data cannot be fetched or stored.");

				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);// 405 Method not allowed
				response.getWriter().write("Method not allowed due to system freeze.");
				
				return false;
			}
		}
		return true;
	}

}
