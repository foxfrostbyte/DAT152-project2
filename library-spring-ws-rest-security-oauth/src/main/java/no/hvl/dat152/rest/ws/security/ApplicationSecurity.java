/**
 * 
 */
package no.hvl.dat152.rest.ws.security;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author tdoy
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurity {
	
	@Autowired
	private AuthTokenFilter authTokenFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf->csrf.disable());
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.authorizeHttpRequests(authorize -> 
				authorize.anyRequest().authenticated());
		
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
		jwtAuthenticationConverter.setPrincipalClaimName("email");
		
		http.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwtconfig -> jwtconfig.jwtAuthenticationConverter(jwtAuthenticationConverter)));
		
		http.addFilterAfter(authTokenFilter, BearerTokenAuthenticationFilter.class);
		
		return http.build();		
	}

	
	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		
		// initialize as empty collections
		Collection<GrantedAuthority> rgrantedAuthorities = new java.util.ArrayList<>();
		Collection<GrantedAuthority> cgrantedAuthorities = new java.util.ArrayList<>();
		
		// this is realm roles
		try {
			Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
			if (realmAccess != null) {
				Collection<String> realmroles = realmAccess.get("roles");
				if (realmroles != null) {
					rgrantedAuthorities = realmroles.stream()
							.map(role -> new SimpleGrantedAuthority(role))
							.collect(Collectors.toList());
				}
			}
		}catch(Exception ignored) {

		}
		try {
			Map<String, Map<String, Collection<String>>> resource_claim = jwt.getClaim("resource_access");
			if (resource_claim != null) {
				Map<String, Collection<String>> clientAccess = resource_claim.get("dat152oblig2");
				if (clientAccess != null) {
					Collection<String> roles = clientAccess.get("roles");
					if (roles != null) {
						cgrantedAuthorities = roles.stream()
								.map(role -> new SimpleGrantedAuthority(role))
								.collect(Collectors.toList());
					}
				}
			}
		} catch(Exception ignored) {
		
		}
		
		Collection<GrantedAuthority> allAuthorities = new java.util.ArrayList<>();
		allAuthorities.addAll(cgrantedAuthorities);
		allAuthorities.addAll(rgrantedAuthorities);
		
		System.out.println("All Roles = "+allAuthorities);
		
		return allAuthorities;
	}
}
