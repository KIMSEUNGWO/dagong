package project.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.study.authority.admin.dto.*;
import project.study.domain.*;
import project.study.jpaRepository.AdminJpaRepository;
import project.study.repository.AdminRepository;
import project.study.repository.FreezeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminJpaRepository adminJpaRepository;
    private final AdminRepository adminRepository;
    private final FreezeRepository freezeRepository;

    @Transactional
    public Optional<Admin> adminLogin(String account, String password){

        Optional<Admin> byAccount = adminJpaRepository.findByAccount(account);
        if (byAccount.get().getPassword().equals(password)){
            return adminJpaRepository.findByAccount(account);
        }

        return null;
    }


    public Optional<Admin> findById(Long id){
        if (id == null) return Optional.empty();
        return adminJpaRepository.findById(id);
    }

    public Page<SearchMemberDto> searchMember(String word, int pageNumber, String freezeOnly){
        PageRequest pageable = PageRequest.of(pageNumber - 1, 10);
        return adminRepository.searchMember(word, pageable, freezeOnly);
    }

    public Page<SearchExpireMemberDto> searchExpireMember(String word, int pageNumber){
        PageRequest pageable = PageRequest.of(pageNumber - 1, 10);
        return adminRepository.searchExpireMember(word, pageable);
    }

    public Page<SearchRoomDto> searchRoom(String word, int pageNumber){
        PageRequest pageable = PageRequest.of(pageNumber - 1, 10);
        return adminRepository.searchRoom(word, pageable);
    }

    public Page<SearchNotifyDto> searchNotify(String word, int pageNumber, String containComplete){
        PageRequest pageable = PageRequest.of(pageNumber - 1, 10);
        return adminRepository.searchNotify(word, pageable, containComplete);
    }

    public SearchNotifyReadMoreDto searchNotifyReadMore(Long notifyId){
        SearchNotifyReadMoreDto searchNotifyReadMoreDto = adminRepository.searchNotifyReadMore(notifyId);
        SearchNotifyImageDto searchNotifyImageDto = adminRepository.searchNotifyImage(notifyId);
        for (String s : searchNotifyImageDto.getNotifyImageOriginalName()){
            searchNotifyReadMoreDto.setNotifyImageStoreName(searchNotifyImageDto.getNotifyImageStoreName());
            searchNotifyReadMoreDto.setNotifyImageOriginalName(searchNotifyImageDto.getNotifyImageOriginalName());
        }
        return searchNotifyReadMoreDto;
    }

    public SearchNotifyMemberInfoDto searchNotifyMemberInfo(String account, Long notifyId){
        SearchNotifyMemberInfoDto searchNotifyMemberInfoDto = adminRepository.searchNotifyMemberInfo(notifyId);
        String memberProfile = adminRepository.findMemberProfile(account);
        searchNotifyMemberInfoDto.setMemberProfile(memberProfile);
        return searchNotifyMemberInfoDto;
    }


    @Transactional
    public void notifyStatusChange(RequestNotifyStatusChangeDto dto){
        adminRepository.notifyStatusChange(dto);
    }

    @Transactional
    public void notifyMemberFreeze(RequestNotifyMemberFreezeDto dto){
        adminRepository.notifyMemberFreeze(dto);
        freezeRepository.save(dto);
    }

    @Transactional
    public void deleteRoom (RequestDeleteRoomDto dto){
        adminRepository.deleteJoinRoom(dto);
        adminRepository.insertRoomDelete(dto);
    }

}
