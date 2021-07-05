package com.jwt.seckill.entity;

import java.io.Serializable;

/**
 * (Task)实体类
 *
 * @author makejava
 * @since 2021-06-21 14:17:15
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 801744184424788046L;

    private Long id;

    private Long lastId;

    private String finish;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

}
