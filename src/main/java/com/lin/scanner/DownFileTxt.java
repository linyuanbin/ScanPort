package com.lin.scanner;

import java.io.File;

/**
 * Created by lin on 2018/6/30.
 */
public class DownFileTxt extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
         if (f.isDirectory())
             return true;

        if (f.getName().matches(".+\\.txt"))
            return true;

        return false;
    }

    @Override
    public String getDescription() {
        return "*.txt";
    }

}
