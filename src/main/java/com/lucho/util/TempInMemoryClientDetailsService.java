package com.lucho.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.InMemoryClientDetailsService;

public class TempInMemoryClientDetailsService extends InMemoryClientDetailsService {
	
	public TempInMemoryClientDetailsService() {
		Map<String, ClientDetails> map = new HashMap<String, ClientDetails>();
		BaseClientDetails trains = new BaseClientDetails();
		trains.setClientId("trains-client-id");
		trains.setClientSecret("trains-client-secret");
        GrantedAuthority gai = new SimpleGrantedAuthority("ROLE_USER");
		trains.setAuthorities(Collections.singletonList(gai));
		map.put("trains-client-id", trains);
		setClientDetailsStore(map);
	}

}
