package com.test.barcap.cache.pojo;

import java.util.Objects;

public class Model
{
    private String key;
    private Double target;

    public Model(String key, Double target)
    {
        this.key = key;
        this.target = target;
    }
    public Double getTarget() {
        return target;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(key, model.key) && Objects.equals(target, model.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, target);
    }

    @Override
    public String toString() {
        return "Model{" +
                "key='" + key + '\'' +
                ", target=" + target +
                '}';
    }
}
