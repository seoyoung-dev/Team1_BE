package team1.BE.seamless.service;

import team1.BE.seamless.DTO.UserDTO.UserDetails;
import team1.BE.seamless.DTO.UserDTO.UserSimple;
import team1.BE.seamless.DTO.UserDTO.UserUpdate;
import team1.BE.seamless.entity.UserEntity;
import team1.BE.seamless.mapper.UserMapper;
import team1.BE.seamless.repository.UserRepository;
import team1.BE.seamless.util.auth.ParsingPram;
import team1.BE.seamless.util.errorException.BaseHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ParsingPram parsingPram;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
        ParsingPram parsingPram) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.parsingPram = parsingPram;
    }

    public UserDetails getUser(HttpServletRequest req) {
        UserEntity user = userRepository.findByEmail(parsingPram.getEmail(req))
            .orElseThrow(() -> new BaseHandler(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다."));
        return userMapper.toUserDetails(user);
    }

    @Transactional
    public UserSimple updateUser(HttpServletRequest req, @Valid UserUpdate update) {
        UserEntity user = userRepository.findByEmail(parsingPram.getEmail(req))
            .orElseThrow(() -> new BaseHandler(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다."));

        userMapper.toUpdate(user,update);

        return userMapper.toUserSimple(user);
    }

    @Transactional
    public UserEntity createUser(@Valid UserSimple simple) {
        return userRepository.findByEmail(simple.getEmail())
            .orElseGet(() -> userMapper.toEntity(
                simple.getUsername(),
                simple.getEmail(),
                simple.getPicture()
            ));
    }
}
