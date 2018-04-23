package com.mmall.pojo;

import java.util.Date;

public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

    public Category(Integer id, Integer parentId, String name, Boolean status, Integer sortOrder, Date createTime, Date updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.status = status;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Category() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    /**
     * 我们重写了equals和hashCode方法，因为在使用Set的时候，Set的排重只对默认的基本数据类型有效，
     * 对于我们自己定义的类的排重，如果不重写equals和hashCode方法是无法起到排重作用的。与此同时，
     * 补充说明一下为什么要重写这两个方法，因为Set集在排重时会调用当前存储对象的equals方法，equals
     * 方法里面判断两个对象是否相同，又是通过这两个对象的HashCode方法进行比较的，如果只重写HashCode
     * 方法，那我们还可以修改equals里面判断对象元素的方法，这样最终equals方法返回的值依然是false，
     * 也就是说，如果HashCode是true，那equals不一定是true；但如若equals返回true，那么HashCode便
     * 一定是true。所以在想让Set能够真正对两个自定义的对象进行排重的话，最好就重写该两个方法，并且
     * 选择性地调用对象的属性进行判断。
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return id != null ? id.equals(category.id) : category.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}