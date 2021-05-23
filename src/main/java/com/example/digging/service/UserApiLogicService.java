package com.example.digging.service;

import com.example.digging.domain.entity.Tags;
import com.example.digging.domain.entity.User;
import com.example.digging.domain.network.Header;
import com.example.digging.domain.network.request.CheckUserRequest;
import com.example.digging.domain.network.request.UserApiRequest;
import com.example.digging.domain.network.response.RecentDiggingResponse;
import com.example.digging.domain.network.response.TotalTagResponse;
import com.example.digging.domain.network.response.UserApiResponse;
import com.example.digging.domain.repository.TagsRepository;
import com.example.digging.domain.repository.UserRepository;
import com.example.digging.ifs.CrudInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserApiLogicService implements CrudInterface<UserApiRequest, UserApiResponse> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagsRepository tagsRepository;

    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {
        UserApiRequest userApiRequest = request.getData();

        User user = User.builder()
                .username(userApiRequest.getUsername())
                .email(userApiRequest.getEmail())
                .password(userApiRequest.getPassword())
                .provider(userApiRequest.getProvider())
                .role("Admin")
                .interest(userApiRequest.getInterest())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User newUser = userRepository.save(user);

        return response(newUser);
    }

    @Override
    public Header<UserApiResponse> read(Integer id) {
        System.out.println(id);
        Optional<User> optional = userRepository.findById(id);
        return optional
                .map(user -> response(user))
                .orElseGet(
                        ()->Header.ERROR("데이터 없음")
                );
    }

    @Override
    public Header<UserApiResponse> update(Integer id, Header<UserApiRequest> request) {
        UserApiRequest userApiRequest = request.getData();
        Optional<User> optional = userRepository.findById(id);
        System.out.println(userApiRequest);
        System.out.println(optional);

        return optional
                .map(user -> {
                    user.setRole(userApiRequest.getRole())
                            .setUpdatedAt(LocalDateTime.now())
                            ;
                    return user;
                })
                .map(user -> userRepository.save(user))
                .map(updateUser -> response(updateUser))
                .orElseGet(
                        ()->Header.ERROR("데이터 없음")
                );

    }

    @Override
    public Header delete(Integer id) {

        System.out.println(id);
        Optional<User> optional = userRepository.findById(id);
        return optional
                .map(user -> {
                    userRepository.delete(user);
                    return Header.OK();
                })
                .orElseGet(
                        ()->Header.ERROR("데이터 없음")
                );

    }

    public Header<TotalTagResponse> getUserTotalTags(Header<CheckUserRequest> request) {

        List<Tags> userTagList = tagsRepository.findAllByUserId(request.getData().getId());
        int userTagNum = userTagList.size();
        ArrayList<String> tagStr = new ArrayList<String>();

        for (int i =0;i<userTagNum;i++) {
            tagStr.add(userTagList.get(i).getTags());
        }

        if (userTagNum>0){
            TotalTagResponse totalTagResponse = TotalTagResponse.builder()
                    .totalNum(userTagNum)
                    .totalTags(tagStr)
                    .build();
            return Header.OK(totalTagResponse);
        }else{
            return Header.ERROR("태그 없음");
        }


    }

    public Header<RecentDiggingResponse> getUserRecentDigging(Header<CheckUserRequest> request) {
        return null;
    }

    private Header<UserApiResponse> response(User user){
        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .provider(user.getProvider())
                .role(user.getRole())
                .interest(user.getInterest())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return Header.OK(userApiResponse);

    }

}
