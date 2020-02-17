package com.hibicode.pockafkastremwithjson;

public class TotalPerClient {

    private String name;
    private Long total = 0L;

    public TotalPerClient() {
    }

    public TotalPerClient(String name, Long total) {
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

}
