package com.ead.course.client;

import com.ead.course.dtos.CourseUserDto;
import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    RestTemplate restTemplate;

    @Value("${ead.api.url.authUser}")
    private String REQUEST_URI;

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable){
        List<UserDto> userDtoList = null;

        String url = getURLforAllUsersByCourse(courseId, pageable);

        log.info("Resquest URL : {}" + url);
        try{
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<UserDto>> (){};
            ResponseEntity<ResponsePageDto<UserDto>> result = restTemplate.exchange(url , HttpMethod.GET,null,responseType);
            userDtoList = result.getBody().getContent();
            log.debug("Response Number of elements : {}", userDtoList.size());
        }catch(HttpStatusCodeException e){

            log.error("Error request/course {}", e);
        }
        log.info("Ending request / courses userId {}" , courseId);
        return new PageImpl<>(userDtoList);
    }

    private String getURLforAllUsersByCourse(UUID courseId, Pageable pageable) {
        return new StringBuilder(REQUEST_URI)
                .append("/users?courseId=").append(courseId)
                .append("&page=").append(pageable.getPageNumber())
                .append("&size=").append(pageable.getPageSize())
                .append("&sort=").append(pageable.getSort()).toString().replace(": ",",");
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId) {
        String url = new StringBuilder(REQUEST_URI).append("/users/").append(userId).toString();
        ResponseEntity<UserDto> result = restTemplate.exchange(url , HttpMethod.GET,null,UserDto.class);
        return result;
    }


    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = new StringBuilder(REQUEST_URI).append("/users/").append(userId).append("/courses/subscription").toString();

        var courseUserDto = new CourseUserDto();
        courseUserDto.setCourseId(courseId);
        courseUserDto.setUserId(userId);

        restTemplate.postForObject(url,courseUserDto,String.class);
    }
}