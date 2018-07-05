package com.lin.hashcode;

/**
 * Created by lin on 2018/7/2.
 */
public class Mykey{

    private int id;
    private String name;

    public Mykey(int id,String name){
        this.id=id;
        this.name=name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        //Mykey m=(Mykey) obj;
        return obj!=null?(name.equals(((Mykey) obj).name)&&id==(((Mykey) obj).id)):false;
    }

    @Override
    public int hashCode() {
        //return super.hashCode();

        return (id+"").hashCode();
    }

    @Override
    public String toString() {
        return "Mykey{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
