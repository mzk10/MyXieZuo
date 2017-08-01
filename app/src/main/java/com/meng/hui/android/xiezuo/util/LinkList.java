package com.meng.hui.android.xiezuo.util;


import java.io.Serializable;

public class LinkList<T> implements Serializable
{

    public LinkList()
    {
        this.cursor = new Cursor<T>();
    }

    private class Cursor<R> implements Serializable
    {
        public R obj;
        public Cursor<R> next;
    }

    private Cursor<T> cursor;

    public boolean add(T object)
    {
        Cursor<T> cursor_=this.cursor;
        while (cursor_.obj != null)
        {
            cursor_ = cursor_.next;
        }
        cursor_.obj = object;
        cursor_.next=new Cursor<T>();
        return true;
    }
    
    public boolean add(LinkList<T> list){
        Cursor<T> cursor_=this.cursor;
        while (cursor_.obj != null)
        {
            cursor_ = cursor_.next;
        }
        cursor_.obj=list.cursor.obj;
        cursor_.next=list.cursor.next;
        return true;
    }

    public boolean addToFirst(T object){
        Cursor<T> cursor_ = new Cursor<T>();
        cursor_.obj=object;
        cursor_.next=this.cursor;
        this.cursor=cursor_;
        return true;
    }
    
    public boolean moveToFirst(int index){
        Cursor<T> cursor_ = this.cursor;
        Cursor<T> tmp_cur = null;
        for (int i = 0; i < index; i++)
        {
            tmp_cur=cursor_;
            cursor_=cursor_.next;
            if (cursor_.next==null)
            {
                throwIndexOut();
            }
        }
        if (tmp_cur!=null)
        {
            tmp_cur.next=cursor_.next;
            cursor_.next=this.cursor;
            this.cursor=cursor_;
        }
        return true;
    }
    
    
    public boolean removeObject(T object)
    {
        if (this.cursor.obj==null)
        {
            throwNullList();
        }
        if(this.cursor.obj == object){
            this.cursor=this.cursor.next;
            return true;
        }else{
            Cursor<T> cursor_ = this.cursor;
            while(cursor_.next.obj!=null){
                if (cursor_.next.obj==object)
                {
                    cursor_.next=cursor_.next.next;
                    return true;
                }else{
                    cursor_=cursor_.next;
                }
            }
            return false;
        }
    }

    public T removeIndex(int index){
        if (index<0)
        {
//            indexbelow0();
            return null;
        }
        if (this.cursor.obj==null)
        {
//            throwNullList();
            return null;
        }
        if (index==0)
        {
            T obj = this.cursor.obj;
            this.cursor=this.cursor.next;
            return obj;
        }else{
            Cursor<T> cursor_ = this.cursor;
            for (int i = 0; i <(index-1) ; i++)
            {
                cursor_=cursor_.next;
                if (cursor_.next.obj==null)
                {
                    throwIndexOut();
                }
            }
            T obj = cursor_.next.obj;
            cursor_.next=cursor_.next.next;
            return obj;
        }
    }


    public T removeFirst(){
        return removeIndex(0);
    }
    
    public T removeLast(){
        
        return removeIndex(size()-1);
    }
    
    public boolean removeAll(){
        this.cursor.obj=null;
        this.cursor.next=null;
        return true;
    }
    
    public T get(int index){
        if (index<0)
        {
            indexbelow0();
        }
        int i=0;
        Cursor<T> cursor_ = this.cursor;
        while(i<index){
            cursor_=cursor_.next;
            if (cursor_.obj==null)
            {
//                throwIndexOut();
                return null;
            }
            i++;
        }
        T obj = cursor_.obj;
        return obj;
    }
    
    /*
    private Cursor<T> getCursor(int index){
        if (index<0)
        {
            indexbelow0();
        }
        int i=0;
        Cursor<T> cursor_ = this.cursor;
        while(i<index){
            cursor_=cursor_.next;
            if (cursor_.obj==null)
            {
                return null;
            }
            i++;
        }
        return cursor_;
    }
    */

    public int size()
    {
        int count = 0;
        Cursor<T> cursor_ = this.cursor;
        while (cursor_.obj!=null){
            cursor_=cursor_.next;
            count++;
        }
        return count;
    }
    
    /*
    for (int i = 0; i < this.size()-1; i++)
    {
        cursor_ = this.getCursor(i);
        Cursor<T> cur_1 = cursor_;
        Cursor<T> cur_2 = cursor_.next;
        boolean isTrue = compositor.compositor(cur_1.obj,cur_2.obj);
        if (isTrue)
        {
            cur_1.next=cur_2.next;
            cur_2.next=cur_1;
            if (cursor_up!=null)
            {
                cursor_up.next=cur_2;
            }else{
                this.cursor=cur_2;
            }
            cursor_up=cur_2;
            cursor_=cur_1;
            reorderCount++;
        }else{
            cursor_up = cur_1;
            cursor_ = cur_2;
        }
    }
    */
    
    
    public LinkList<T> reorder(Compositor<T> compositor){
        int reorderCount = 1;
        while (reorderCount > 0)
        {
            reorderCount = 0;
            Cursor<T> cursor_ = this.cursor;
            Cursor<T> cursor_up = null;

            while (cursor_.next.obj != null)
            {
                Cursor<T> cur_1 = cursor_;
                Cursor<T> cur_2 = cursor_.next;
                boolean isTrue = compositor.compositor(cur_1.obj, cur_2.obj);
                if (isTrue)
                {
                    cur_1.next = cur_2.next;
                    cur_2.next = cur_1;
                    if (cursor_up != null)
                    {
                        cursor_up.next = cur_2;
                    } else
                    {
                        this.cursor = cur_2;
                    }
                    cursor_up = cur_2;
                    cursor_ = cur_1;
                    reorderCount++;
                } else
                {
                    cursor_up = cur_1;
                    cursor_ = cur_2;
                }
            }
        }
        return this;
    }
    
    public interface Compositor<E>{
        /**
         * @param obj1 对比的元素1
         * @param obj2 对比的元素2
         * @return 是否交换位置
         */
        public boolean compositor(E obj1, E obj2);
    }
    
    private void indexbelow0()
    {
        throw new RuntimeException("参数不能小于0");
    }
    
    private void throwNullList()
    {
        throw new RuntimeException("空数组");
    }
    
    private void throwIndexOut()
    {
        throw new RuntimeException("数组越界");
    }
    
}