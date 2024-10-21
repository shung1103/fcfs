package org.hanghae99.fcfs.common.entity;

public enum UserSocialEnum {
    KAKAO(Social.KAKAO),
    NAVER(Social.NAVER),
    GOOGLE(Social.GOOGLE);

    private final String social;

    UserSocialEnum(String social) { this.social = social; }

    public String getSocial() { return this.social; }

    public static class Social {
        public static final String KAKAO = "SOCIAL_KAKAO";
        public static final String NAVER = "SOCIAL_NAVER";
        public static final String GOOGLE = "SOCIAL_GOOGLE";
    }
}
