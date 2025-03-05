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
 *   客户端请求实体类
 */
public class RPCRequest {
    private String interfaceName;
    private String methodName;
}
