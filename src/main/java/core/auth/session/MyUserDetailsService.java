package core.auth.session;

import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repository.UserRepository;


@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // UsernameNotFoundException이 발동되면 AuthenticationException 이 발동된다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User userPS = userRepository.findByUsername(username)
                .orElseThrow(
                        ()->new UsernameNotFoundException("유저네임을 찾을 수 없습니다")
                );
        return new MyUserDetails(userPS);
    }
}
