package com.lin.scanner;

import javax.swing.*;

/**
 * Created by lin on 2018/6/30.
 */
public class Main {

    /**
     * 入口
     */
    public static void main(String[] args) {
        // 启动
        SwingUtilities.invokeLater(new ThreadScan());
    }

}
