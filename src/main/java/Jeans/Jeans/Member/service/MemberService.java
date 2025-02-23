package Jeans.Jeans.Member.service;

import Jeans.Jeans.BasicEdit.domain.BasicEdit;
import Jeans.Jeans.BasicEdit.respository.BasicEditRepository;
import Jeans.Jeans.Follow.domain.Follow;
import Jeans.Jeans.Follow.domain.Status;
import Jeans.Jeans.Follow.repository.FollowRepository;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.domain.RefreshToken;
import Jeans.Jeans.Member.dto.*;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.MemberPhoto.domain.MemberPhoto;
import Jeans.Jeans.MemberPhoto.repository.MemberPhotoRepository;
import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Photo.repository.PhotoRepository;
import Jeans.Jeans.Team.domain.Team;
import Jeans.Jeans.Team.dto.TargetTeamDto;
import Jeans.Jeans.Team.repository.TeamRepository;
import Jeans.Jeans.TeamMember.domain.TeamMember;
import Jeans.Jeans.TeamMember.repository.TeamMemberRepository;
import Jeans.Jeans.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;
    private final BasicEditRepository basicEditRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final FollowRepository followRepository;
    private final PhotoRepository photoRepository;
    private final MemberPhotoRepository memberPhotoRepository;

    @Value("${spring.jwt.secret-key}")
    private String accessKey;

    @Value("${spring.jwt.refresh-key}")
    private String refreshKey;

    // Access 토큰 만료 시간을 1시간으로 설정
    private Long AccessExpireTimeMs = 1000 * 60 * 60L;

    // Refresh 토큰 만료 시간을 7일로 설정
    private Long RefreshExpireTimeMs = 7 * 24 * 1000 * 60 * 60L;

    public String signUp(String name, String birthday, String phone, String password, Long voiceType){
        if(existsByPhone(phone)) throw new RuntimeException(phone + "은 이미 존재하는 전화번호입니다.");

        Member member = new Member(name, birthday, phone, encoder.encode(password), voiceType, null);
        memberRepository.save(member);
        return "회원가입이 완료되었습니다.";
    }

    // 로그인
    public LoginResponseDto login(String phone, String password){
        // 존재하지 않는 아이디로 로그인을 시도한 경우를 캐치
        Member member = findMemberByPhone(phone);

        // 존재하는 아이디를 입력했지만 잘못된 비밀번호를 입력한 경우를 캐치
        if(!encoder.matches(password, member.getPassword())) throw new RuntimeException("잘못된 비밀번호를 입력했습니다.");

        // 로그인 성공 -> 토큰 생성
        String accessToken = JwtUtil.createAccessToken(member.getPhone(), accessKey, AccessExpireTimeMs);
        String refreshToken = JwtUtil.createRefreshToken(member.getPhone(), refreshKey, RefreshExpireTimeMs);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setMemberId(member.getMemberId());
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        int birthYear = Integer.parseInt(member.getBirthday().substring(0, 2));
        birthYear += (birthYear <= LocalDate.now().getYear() % 100) ? 2000 : 1900;
        int currentYear = LocalDate.now().getYear();
        Integer age = currentYear - birthYear + 1;

        Boolean exists = false;
        if (member.getProfileUrl() != null){
            exists = true;
        }
        return new LoginResponseDto(member.getMemberId(), age, exists, member.getPhone(), accessToken, refreshToken);
    }

    // AccessToken 재발급
    public LoginResponseDto refresh(String refreshTokenValue){
        // 해당 RefreshToken이 유효한지 DB에서 탐색
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenValue);

        // RefreshToken에 담긴 아이디 값 가져오기
        Claims claims = JwtUtil.parseRefreshToken(refreshToken.getValue(), refreshKey);
        String phone = claims.get("userId").toString();
        System.out.println("RefreshToken에 담긴 아이디 : " + phone);

        // 가져온 아이디에 해당하는 member가 존재하는지 확인
        Member member = findMemberByPhone(phone);

        // 새 AccessToken 생성
        String accessToken = JwtUtil.createAccessToken(member.getPhone(), accessKey, AccessExpireTimeMs);

        // 새 AccessToken과 기존 RefreshToken을 DTO에 담아 리턴
        return LoginResponseDto
                .builder()
                .memberId(member.getMemberId())
                .phone(member.getPhone())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    // 회원탈퇴
    public String delete(Authentication authentication){
        Member member = getLoginMember();
        memberRepository.delete(member);
        return "회원탈퇴가 완료되었습니다.";
    }

    // 본인 확인
    public String checkMember(Member user, String birthday, String phone){
        Member member = memberRepository.findByBirthdayAndPhone(birthday, phone)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));
        if (!user.equals(member)) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다.");
        }
        return "본인 확인이 완료되었습니다.";
    }

    // 이름 변경
    public void changeName(Member user, NameChangeReqDto requestDto){
        user.updateName(requestDto.getNewName());
        memberRepository.save(user);
    }

    // 비밀번호 변경
    public void changePassword(Member user, PasswordChangeReqDto requestDto) {
        user.updatePassword(encoder.encode(requestDto.getNewPassword()));
        memberRepository.save(user);
    }

    // 내 정보 조회
    public ProfileResponseDto getMyProfile(Member member){
        return new ProfileResponseDto(member.getName(), member.getProfileUrl(), member.getBirthday(), member.getPhone());
    }

    // 음성 타입 조회
    public VoiceTypeResDto getMyAgentType(Member member){
        return new VoiceTypeResDto(member.getVoiceType());
    }

    // 기본 보정 설정
    public void createBasicEdit(Member member, BasicEditRequestDto requestDto){
        BasicEdit basicEdit = new BasicEdit(member, requestDto.getEdit1(), requestDto.getEdit2(), requestDto.getEdit3());
        basicEditRepository.save(basicEdit);
    }

    // 기본 보정 설정 변경
    public void updateBasicEdit(Member member, BasicEditRequestDto requestDto){
        BasicEdit basicEdit = basicEditRepository.findByMember(member);
        basicEdit.updateBasicEdit(requestDto.getEdit1(), requestDto.getEdit2(), requestDto.getEdit3());
        basicEditRepository.save(basicEdit);
    }

    // 프로필 이미지 수정
    public ProfileUpdateResDto updateProfile(Member member, String profileUrl){
        member.updateProfile(profileUrl);
        memberRepository.save(member);
        Long memberId = member.getMemberId();
        return ProfileUpdateResDto.builder()
                .memberId(memberId)
                .profileUrl(member.getProfileUrl())
                .build();
    }

    // 팔로우할 회원 검색
    public FollowTargetDto getFollowTarget(String name, String phone){
        Member member = memberRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new EntityNotFoundException("해당 이름과 전화번호에 해당하는 회원이 존재하지 않습니다."));
        return new FollowTargetDto(member.getMemberId(), name, member.getProfileUrl());
    }

    // 홈 화면에서 친구, 팀 목록 조회
    public List<ChatRoomDto> getChatRoomList(Member user){
        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();
        List<Member> friends = findMembersWithSharedPhotos(user);
        List<Team> teams = findTeamsWithSharedPhotos(user);
        List<MemberPhoto> friendMemberPhotos = getSharedPhotosForAllFriends(user);

        Set<Long> addedMemberIds = new HashSet<>();
        Set<Long> addedTeamIds = new HashSet<>();

        for (MemberPhoto memberPhoto : friendMemberPhotos) {
            // sharer가 user일 경우, receiver는 friend로 설정
            Member relatedMember = memberPhoto.getSharer().equals(user) ? memberPhoto.getReceiver() : memberPhoto.getSharer();

            if (!addedMemberIds.contains(relatedMember.getMemberId())) {
                Follow follow = followRepository.findByFollowerAndFollowing(user, relatedMember);
                Photo latestPhoto = memberPhoto.getPhoto();

                // ChatRoomDto에 해당 정보를 담아서 추가
                chatRoomDtoList.add(new ChatRoomDto(
                        relatedMember.getMemberId(), null,
                        relatedMember.getName(),
                        relatedMember.getProfileUrl(),
                        follow != null ? follow.getNickname() : null,
                        latestPhoto != null ? latestPhoto.getCreatedDate() : null
                ));
                addedMemberIds.add(relatedMember.getMemberId());
            }
        }

        // 팀 목록에 대해 처리
        for (Team team : teams) {
            if (!addedTeamIds.contains(team.getTeamId())) {
                // 최신 사진을 가져오기
                Photo latestPhoto = photoRepository.findTopByTeamOrderByCreatedDateDesc(team);
                if (latestPhoto != null) {
                    chatRoomDtoList.add(new ChatRoomDto(null, team.getTeamId(), team.getName(), team.getImageUrl(), null, latestPhoto.getCreatedDate()));
                    addedTeamIds.add(team.getTeamId());
                }
            }
        }

        // 최신순으로 정렬
        chatRoomDtoList.sort((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()));
        ChatRoomDto newChatRoomDto = new ChatRoomDto(user.getMemberId(), null, user.getName(), user.getProfileUrl(), "나", null);
        chatRoomDtoList.add(0, newChatRoomDto);
        return chatRoomDtoList;
    }

    public List<MemberPhoto> getSharedPhotosForAllFriends(Member user) {
        List<MemberPhoto> allSharedPhotos = new ArrayList<>();

        // user와 친구들의 리스트를 가져옵니다
        List<Member> friends = memberRepository.findMembersWithSharedPhotos(user);

        // 각 friend에 대해 처리
        for (Member friend : friends) {
            // user가 sharer이고 friend가 receiver인 경우
            List<MemberPhoto> sharerToReceiver = memberPhotoRepository.findBySharerAndReceiverOrderByPhoto_CreatedDateDesc(user, friend);

            // user가 receiver이고 friend가 sharer인 경우
            List<MemberPhoto> receiverToSharer = memberPhotoRepository.findByReceiverAndSharerOrderByPhoto_CreatedDateDesc(user, friend);

            // 두 리스트를 합침
            allSharedPhotos.addAll(sharerToReceiver);
            allSharedPhotos.addAll(receiverToSharer);
        }

        // 모든 공유된 사진들을 최신순으로 정렬
        allSharedPhotos.sort((a, b) -> b.getPhoto().getCreatedDate().compareTo(a.getPhoto().getCreatedDate()));

        return allSharedPhotos;
    }

    public List<Member> findMembersWithSharedPhotos(Member user) {
        // sharer 또는 receiver로서 user와 관련된 사진들을 모두 가져오기
        List<MemberPhoto> memberPhotos = memberPhotoRepository.findBySharer(user);
        memberPhotos.addAll(memberPhotoRepository.findByReceiver(user));

        // 중복된 Member를 제거하면서 상대방 Member를 찾아냄
        Set<Member> members = new HashSet<>();
        for (MemberPhoto memberPhoto : memberPhotos) {
            // user가 sharer일 경우 receiver를, receiver일 경우 sharer를 리스트에 추가
            if (memberPhoto.getSharer().equals(user)) {
                members.add(memberPhoto.getReceiver());
            } else {
                members.add(memberPhoto.getSharer());
            }
        }

        // Set을 List로 변환하여 반환
        return new ArrayList<>(members);
    }

    public List<Team> findTeamsWithSharedPhotos(Member user) {
        // user가 속한 팀 목록
        List<TeamMember> teamMembers = teamMemberRepository.findAllByMember(user);
        List<Long> teamIds = teamMembers.stream()
                .map(teamMember -> teamMember.getTeam().getTeamId())
                .collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 팀 중에서 Photo가 있는 teamId 조회
        List<Photo> photos = photoRepository.findByTeam_TeamIdIn(teamIds);
        List<Long> photoTeamIds = photos.stream()
                .map(photo -> photo.getTeam().getTeamId())
                .distinct()
                .collect(Collectors.toList());

        if (photoTeamIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 해당 teamId에 해당하는 팀 목록
        return teamRepository.findAllById(photoTeamIds);
    }

    // 사진 공유 대상 선택 시 팀, 친구 목록 조회
    public ShareTargetDto getShareList(Member user){
        List<TeamMember> teamMemberList = teamMemberRepository.findAllByMember(user);

        List<Long> teamIds = new ArrayList<>();
        for (TeamMember teamMember : teamMemberList){
            teamIds.add(teamMember.getTeam().getTeamId());
        }

        List<TargetTeamDto> teamDtoList = new ArrayList<>();
        for (Long teamId : teamIds){
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 팀이 존재하지 않습니다."));
            teamDtoList.add(new TargetTeamDto(teamId, team.getName(), team.getImageUrl()));
        }

        List<Follow> follows = followRepository.findAllByFollowerAndStatus(user, Status.FRIEND);

        List<TargetMemberDto> memberDtoList = new ArrayList<>();
        for (Follow follow : follows){
            Long memberId = follow.getFollowing().getMemberId();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 member가 존재하지 않습니다."));
            memberDtoList.add(new TargetMemberDto(memberId, member.getName(), member.getProfileUrl(), follow.getNickname()));
        }

        return new ShareTargetDto(teamDtoList, memberDtoList);
    }

    // 회원 가입 시 입력한 전화번호를 가진 member 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone){
        return memberRepository.existsByPhone(phone);
    }

    // 아이디로 member 찾기
    @Transactional(readOnly = true)
    public Member findMemberByPhone(String phone){
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("아이디가 " + phone + "인 회원이 존재하지 않습니다."));
    }

    // 현재 로그인한 member 불러오기
    public Member getLoginMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "인증된 회원 정보가 없습니다."));
    }
}
