package nextstep.support.resolver;

import nextstep.auth.JwtTokenConfig;
import nextstep.auth.JwtTokenProvider;
import nextstep.member.MemberService;
import nextstep.support.annotation.AuthorizationPrincipal;
import nextstep.support.exception.AuthorizationExcpetion;
import nextstep.support.exception.RoomEscapeExceptionCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationPrincipalArgumentResolver(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthorizationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            token = token.replace(JwtTokenConfig.TOKEN_CLASS, "");
        }
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthorizationExcpetion(RoomEscapeExceptionCode.INVALID_TOKEN);
        }
        return memberService.findByUsername(jwtTokenProvider.getPrincipal(token));
    }
}
