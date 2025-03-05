package com.jensonxu.rpc;

import lombok.Getter;
import lombok.Builder;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
/*
 *   服务端响应实体类
 */
public class RPCResponse {
    private String message;
}
