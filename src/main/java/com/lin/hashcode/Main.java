package com.lin.hashcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lin on 2018/7/2.
 */
public class Main {

    public static void main(String[] args){

        Set<Mykey> set=new HashSet<>();
        //重写了hashCode和equals方法
        Mykey m1=new Mykey(1,"laowang");
        Mykey m2=new Mykey(1,"laowang");
        set.add(m1);
        set.add(m2);
        //System.out.println("result: "+set.contains(m2));
        for(Mykey m:set){
            System.out.println("set:"+m +"  hashcode="+m.hashCode());
        }

        Map<Mykey,Integer> map =new HashMap<>();
        map.put(m1,1);
        map.put(m2,2);
        //System.out.println("map: "+map.get(m2));
        Set<Map.Entry<Mykey, Integer>> entries = map.entrySet();
        for(Map.Entry<Mykey,Integer> e:entries){
            System.out.println(e.getKey()+" "+e.getValue()+"  hashcode="+e.getKey().hashCode());
        }



    }
}
