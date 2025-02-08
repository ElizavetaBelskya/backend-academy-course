package ru.seminar.homework.hw5.grpc.config;

import io.grpc.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;
import java.util.Collections;

@GrpcGlobalServerInterceptor
@RequiredArgsConstructor
public class GrpcConfig implements ServerInterceptor {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authorizationHeader = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));

        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));

            String[] parts = credentials.split(":");
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (passwordEncoder.matches(password, userDetails.getPassword())) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return next.startCall(call, headers);
                }
            }
        }

        throw new StatusRuntimeException(Status.UNAUTHENTICATED);
    }

}


