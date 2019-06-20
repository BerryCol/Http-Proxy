package com.apollo.server;

/**
 * 代理类型
 * http
 * https
 */
public enum ProxyType {
    HTTP("http"),
    HTTPS("https");

    private final String type;

    ProxyType(String type) {
        this.type=type;
    }


    public static ProxyType parse(String name){
        for(ProxyType p:values()){
            if(p.type.equals(name)){
                return p;
            }
        }
        return null;
    }

}
