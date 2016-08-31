package com.magetlabs;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
	// write your code here

        File dir = new File(".");

        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".strings.txt");
            }
        });

        for (File file: files) {
            String input = readFile(file);

            String [] splits = input.split("\n");

            StringBuilder header = new StringBuilder();

            header.append("//This file is generated, don't modify by hand\n");
            header.append("\n");
            header.append("#import <Foundation/Foundation.h>");
            header.append("\n");

            StringBuilder source = new StringBuilder();

            String front = file.getName().substring(0, file.getName().indexOf('.'));

            source.append("//This file is generated, don't modify by hand\n");
            source.append("\n");
            source.append("#import \"").append(front).append("StringConstants.h").append("\"\n");
            source.append("\n");

            for (String split: splits) {
                if (split.length() < 3 || split.startsWith("#")) {
                    continue;
                }
                String [] lines = split.split(" ");
                if (lines.length == 2) {
                    String attr = lines[0];
                    String value = lines[1];

                    String valueConstant = "_" + front.toUpperCase() + "_" + attr.toUpperCase();

                    header.append("extern NSString *const ").append(valueConstant).append(";\n");

                    source.append("NSString *const ").append(valueConstant).append(" = @\"").append(value).append("\";\n");
                }
            }

            File hFile = new File(dir, front + "StringConstants.h");

            File mFile = new File(dir, front + "StringConstants.m");

            writeFile(hFile, header.toString());
            writeFile(mFile, source.toString());
        }

    }

    public static String readFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes,"UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeFile(File target, String out) {

        System.out.println("Writing To: " + target.getAbsolutePath());

        System.out.println(out);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            fos.write(out.getBytes("UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
