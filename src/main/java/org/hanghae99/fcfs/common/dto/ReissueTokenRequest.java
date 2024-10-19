package org.hanghae99.fcfs.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ReissueTokenRequest {
    private final String refreshToken;

    public ReissueTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
